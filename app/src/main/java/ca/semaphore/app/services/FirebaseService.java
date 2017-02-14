package ca.semaphore.app.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ca.semaphore.app.R;
import ca.semaphore.app.SemaphoreApplication;
import ca.semaphore.app.activities.MainActivity;
import ca.semaphore.app.data.DataBus;
import ca.semaphore.app.data.events.AckEvent;
import ca.semaphore.app.data.events.NotificationEvent;
import ca.semaphore.app.data.events.SnapshotEvent;
import ca.semaphore.app.database.DeliveryDataSource;
import ca.semaphore.app.database.MailboxDataSource;
import ca.semaphore.app.firebase.database.BaseChildEventListener;
import ca.semaphore.app.models.Delivery;
import ca.semaphore.app.models.Mailbox;
import ca.semaphore.app.models.User;
import ca.semaphore.app.sharedprefs.SemaphoreSharedPrefs;

public class FirebaseService extends Service {

    private static final String TAG = FirebaseService.class.getCanonicalName();

    PowerManager.WakeLock wakeLock;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private DatabaseReference database;

    private NotificationManagerCompat notificationManager;
    private int notifAmount;
    private Set<Mailbox> notifMailboxes;

    private HashMap<String, Query> deliveryQueries;
    private HashMap<String, ChildEventListener> deliveryListeners;

    private HashMap<String, Query> snapshotQueries;
    private HashMap<String, ChildEventListener> snapshotListeners;

    private DeliveryDataSource deliveryDataSource;
    private MailboxDataSource mailboxDataSource;

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (wakeLock != null) {
            wakeLock.acquire();
            Log.i(TAG, "Partial Wake Lock : " + wakeLock.isHeld());
        }

        notifAmount = 0;
        notifMailboxes = new HashSet<>();
        snapshotQueries = new HashMap<>();
        snapshotListeners = new HashMap<>();
        deliveryQueries = new HashMap<>();
        deliveryListeners = new HashMap<>();

        deliveryDataSource = new DeliveryDataSource();
        mailboxDataSource = new MailboxDataSource();
        DataBus.subscribe(this);
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {

        notificationManager = NotificationManagerCompat.from(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                database.child("users")
                        .child(auth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value
                                        final long timestamp = System.currentTimeMillis();
                                        User user = dataSnapshot.getValue(User.class);

                                        // remove all previous listeners
                                        for (Mailbox mailbox : user.getMailboxes().values()) {
                                            mailboxDataSource.update(FirebaseService.this,
                                                                     mailbox,
                                                                     null);
                                            registerSnapshotListener(mailbox);
                                            registerDeliveryListener(mailbox, timestamp);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
            } else {
                unregisterListeners();
            }
        };

        auth.addAuthStateListener(authListener);
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerSnapshotListener(@NonNull final Mailbox mailbox) {
        Query snapshotQuery = database.child("snapshots")
                                      .child(mailbox.getMailboxId())
                                      .orderByKey()
                                      .limitToLast(1);

        ChildEventListener listener = new BaseChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                super.onChildAdded(dataSnapshot, s);
                Delivery snapshot = dataSnapshot.getValue(Delivery.class);
                SemaphoreSharedPrefs.saveSnapshot(FirebaseService.this,
                                                  mailbox.getMailboxId(),
                                                  snapshot);
                DataBus.sendEvent(new SnapshotEvent(mailbox.getMailboxId(), snapshot));
            }
        };

        snapshotQuery.addChildEventListener(listener);
        if (!snapshotQueries.containsKey(mailbox.getMailboxId())) {
            snapshotQueries.put(mailbox.getMailboxId(), snapshotQuery);
        }
        if (!snapshotListeners.containsKey(mailbox.getMailboxId())) {
            snapshotListeners.put(mailbox.getMailboxId(), listener);
        }
    }

    private void registerDeliveryListener(@NonNull final Mailbox mailbox, final long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30); // 30 days of deliveries

        Query deliveryQuery = database.child("deliveries")
                                      .child(mailbox.getMailboxId())
                                      .orderByKey()
                                      .startAt(Long.toString(cal.getTimeInMillis() / 1000));

        ChildEventListener listener = new BaseChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Delivery delivery = dataSnapshot.getValue(Delivery.class);
                deliveryDataSource.update(FirebaseService.this,
                                          mailbox.getMailboxId(),
                                          delivery,
                                          null);

                if (delivery.getTotal() > 0 && delivery.getTimestamp() > (timestamp / 1000)) {
                    FirebaseService.this.notify(mailbox, delivery);
                }
            }
        };
        deliveryQuery.addChildEventListener(listener);

        if (!deliveryQueries.containsKey(mailbox.getMailboxId())) {
            deliveryQueries.put(mailbox.getMailboxId(), deliveryQuery);
        }
        if (!deliveryListeners.containsKey(mailbox.getMailboxId())) {
            deliveryListeners.put(mailbox.getMailboxId(), listener);
        }
    }

    private void notify(@NonNull Mailbox mailbox, @NonNull Delivery delivery) {
        notifAmount += delivery.getTotal();
        notifMailboxes.add(mailbox);

        String message;
        if (notifMailboxes.size() > 1) {
            message = getResources().getString(R.string.notification_mail_received_multi_mailbox,
                                               notifAmount);
        } else {
            message = getResources().getQuantityString(R.plurals.notification_mail_received_single_mailbox,
                                                       notifAmount,
                                                       notifAmount,
                                                       mailbox.getName());
        }
        Log.i(TAG, message);

        if (SemaphoreApplication.isVisible()) {
            DataBus.sendEvent(new NotificationEvent(mailbox.getMailboxId(), message));
        } else {
            notifyUser(mailbox.getMailboxId(), message);
        }
    }

    private void notifyUser(@NonNull String mailboxId, @NonNull String message) {
        Intent mainIntent = MainActivity.createIntent(getApplicationContext(), mailboxId);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(),
                                                                  0,
                                                                  new Intent[]{mainIntent},
                                                                  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_statusbar)
               .setContentTitle(getString(R.string.notification_mail_title))
               .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
               .setContentText(message)
               .setAutoCancel(true)
               .setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }

    private void unregisterSnapshotListener(@NonNull String mailboxId) {
        snapshotQueries.get(mailboxId)
                       .removeEventListener(snapshotListeners.get(mailboxId));
        snapshotQueries.remove(mailboxId);
        snapshotListeners.remove(mailboxId);
    }

    private void unregisterDeliveryListener(@NonNull String mailboxId) {
        deliveryQueries.get(mailboxId)
                       .removeEventListener(deliveryListeners.get(mailboxId));
        deliveryQueries.remove(mailboxId);
        deliveryListeners.remove(mailboxId);
    }

    private void unregisterListeners() {
        if (deliveryQueries.size() > 0) {
            for (String mailboxId : deliveryQueries.keySet()) {
                unregisterDeliveryListener(mailboxId);
            }
        }

        if (snapshotQueries.size() > 0) {
            for (String mailboxId : snapshotQueries.keySet()) {
                unregisterSnapshotListener(mailboxId);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationEvent(@NonNull AckEvent ackEvent) {
        notificationManager.cancel(0);
        notifAmount = 0;
        notifMailboxes.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBus.unsubscribe(this);
        if (wakeLock != null) {
            wakeLock.release();
            Log.i(TAG, "Partial Wake Lock : " + wakeLock.isHeld());
            wakeLock = null;
        }

        unregisterListeners();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }
}
