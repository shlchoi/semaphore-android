package ca.semaphore.app.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
    private static final int GROUP_NOTIFICATION_ID = -1;
    private static final String GROUP_NAME = "Semaphore";

    PowerManager.WakeLock wakeLock;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private DatabaseReference database;

    private NotificationManagerCompat notificationManager;

    private HashMap<String, Query> deliveryQueries;
    private HashMap<String, ChildEventListener> deliveryListeners;

    private HashMap<String, Query> snapshotQueries;
    private HashMap<String, ChildEventListener> snapshotListeners;

    private DeliveryDataSource deliveryDataSource;
    private MailboxDataSource mailboxDataSource;

    private HashMap<String, Integer> notificationIdMap;
    private HashMap<String, String> notificationMessageMap;

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (wakeLock != null) {
            wakeLock.acquire();
            Log.i(TAG, "Partial Wake Lock : " + wakeLock.isHeld());
        }

        snapshotQueries = new HashMap<>();
        snapshotListeners = new HashMap<>();
        deliveryQueries = new HashMap<>();
        deliveryListeners = new HashMap<>();

        deliveryDataSource = new DeliveryDataSource();
        mailboxDataSource = new MailboxDataSource();

        notificationMessageMap = new HashMap<>();
        notificationIdMap = new HashMap<>();
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
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                processUser(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
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
                processSnapshot(dataSnapshot, mailbox);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                processSnapshot(dataSnapshot, mailbox);
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
                processDelivery(delivery, mailbox, timestamp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Delivery delivery = dataSnapshot.getValue(Delivery.class);
                processDelivery(delivery, mailbox, timestamp);
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

    private void processUser(@NonNull DataSnapshot dataSnapshot) {
        final long timestamp = System.currentTimeMillis() / 1000;
        User user = dataSnapshot.getValue(User.class);

        // remove all previous listeners
        notificationIdMap.clear();
        for (Mailbox mailbox : user.getMailboxes().values()) {
            mailboxDataSource.update(FirebaseService.this,
                                     mailbox,
                                     null);
            registerSnapshotListener(mailbox);
            registerDeliveryListener(mailbox, timestamp);
            notificationIdMap.put(mailbox.getMailboxId(),
                                  notificationIdMap.size());
        }
    }

    private void processSnapshot(@NonNull DataSnapshot dataSnapshot, @NonNull Mailbox mailbox) {
        Delivery snapshot = dataSnapshot.getValue(Delivery.class);
        SemaphoreSharedPrefs.saveSnapshot(this, mailbox.getMailboxId(), snapshot);
        DataBus.sendEvent(new SnapshotEvent(mailbox.getMailboxId(), snapshot));
    }

    private void processDelivery(@NonNull Delivery delivery,
                                 @NonNull Mailbox mailbox,
                                 long timestamp) {
        deliveryDataSource.update(this, mailbox.getMailboxId(), delivery, null);

        if (delivery.getTimestamp() > timestamp && (delivery.isCategorising() || delivery.getTotal() > 0)) {
            notify(mailbox, delivery);
        }
    }

    private void notify(@NonNull Mailbox mailbox, @NonNull Delivery delivery) {
        String message;
        if (delivery.isCategorising()) {
            message = getString(R.string.notification_mail_received_categorising, mailbox.getName());
        } else {
            int notificationAmount = SemaphoreSharedPrefs.getNotificationAmount(this, mailbox.getMailboxId());
            notificationAmount += delivery.getTotal();
            SemaphoreSharedPrefs.saveNotificationAmount(this, mailbox.getMailboxId(), notificationAmount);
            message = getResources().getQuantityString(R.plurals.notification_mail_received_categorised,
                                                       notificationAmount,
                                                       notificationAmount,
                                                       mailbox.getName());
        }
        notificationMessageMap.put(mailbox.getMailboxId(), message);
        Log.i(TAG, message);

        if (SemaphoreApplication.isVisible()) {
            DataBus.sendEvent(new NotificationEvent(mailbox.getMailboxId(), message));
        } else {
            createMailboxNotification(mailbox.getMailboxId(), message);
        }
    }

    private void createMailboxNotification(@NonNull String mailboxId, @NonNull String message) {
        Intent mainIntent = MainActivity.createIntent(getApplicationContext(), mailboxId);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(),
                                                                  0,
                                                                  new Intent[]{mainIntent},
                                                                  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = getDefaultNotificationBuilder();
        builder.setContentText(message)
               .setContentIntent(pendingIntent);
        notificationManager.notify(notificationIdMap.get(mailboxId), builder.build());

        createSummaryNotification();
    }

    private void createSummaryNotification() {
        List<String> messages = new ArrayList<>(notificationMessageMap.values());
        if (messages.size() < 2) {
            notificationManager.cancel(GROUP_NOTIFICATION_ID);
            return;
        }

        Intent mainIntent = MainActivity.createIntent(getApplicationContext());
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(),
                                                                  0,
                                                                  new Intent[]{mainIntent},
                                                                  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = getDefaultNotificationBuilder();
        builder.setGroupSummary(true)
               .setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();

        for (int i = 0; i < messages.size(); i++) {
            inbox.addLine(messages.get(i));
        }

        builder.setStyle(inbox);

        notificationManager.notify(GROUP_NOTIFICATION_ID, builder.build());
    }

    @NonNull
    private NotificationCompat.Builder getDefaultNotificationBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setGroup(GROUP_NAME)
               .setSmallIcon(R.drawable.ic_statusbar)
               .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
               .setContentTitle(getString(R.string.notification_mail_title))
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .setAutoCancel(true);
        return builder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAcknowledgeNotification(@NonNull AckEvent ackEvent) {
        notificationManager.cancel(notificationIdMap.get(ackEvent.mailboxId));
        SemaphoreSharedPrefs.saveNotificationAmount(this, ackEvent.mailboxId, 0);
        notificationMessageMap.remove(ackEvent.mailboxId);
        createSummaryNotification();
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
