package ca.semaphore.app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.semaphore.app.R;
import ca.semaphore.app.activities.LoginActivity;
import ca.semaphore.app.activities.MailboxActivity;
import ca.semaphore.app.activities.MainActivity;
import ca.semaphore.app.adapters.DeliveryAdapter;
import ca.semaphore.app.adapters.MailboxAdapter;
import ca.semaphore.app.data.DataBus;
import ca.semaphore.app.data.events.AckEvent;
import ca.semaphore.app.data.events.DeliveryEvent;
import ca.semaphore.app.data.events.MailboxEvent;
import ca.semaphore.app.data.events.NotificationEvent;
import ca.semaphore.app.data.events.SnapshotEvent;
import ca.semaphore.app.database.DeliveryDataSource;
import ca.semaphore.app.database.MailboxDataSource;
import ca.semaphore.app.models.Delivery;
import ca.semaphore.app.models.Mailbox;
import ca.semaphore.app.services.FirebaseService;
import ca.semaphore.app.sharedprefs.SemaphoreSharedPrefs;
import ca.semaphore.app.transformers.DeliveryTransformer;
import ca.semaphore.app.utils.ViewUtils;
import ca.semaphore.app.utils.listeners.ItemSelectedListener;
import ca.semaphore.app.viewmodels.DeliveryViewModel;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getCanonicalName();
    private static final String ARG_MAILBOX_ID = "mailbox_id";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Spinner mailboxSpinner;

    private MailboxAdapter mailboxAdapter;
    private DeliveryAdapter historyAdapter;

    private MailboxDataSource mailboxDataSource;
    private DeliveryDataSource deliveryDataSource;

    @BindView(R.id.main_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.main_letters)
    TextView lettersTextView;

    @BindView(R.id.main_magazines)
    TextView magazinesTextView;

    @BindView(R.id.main_newspapers)
    TextView newspapersTextView;

    @BindView(R.id.main_parcels)
    TextView parcelsTextView;

    @BindView(R.id.main_empty_mailbox)
    TextView emptyMailboxTextView;

    @BindView(R.id.main_categorising_mailbox)
    View categorisingMailboxView;

    @BindView(R.id.main_history_view)
    View historyView;

    @BindView(R.id.main_empty_history)
    View emptyHistoryView;

    @BindView(R.id.main_loading_history)
    View loadingHistoryView;

    @BindView(R.id.main_history)
    RecyclerView historyRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Intent serviceIntent = new Intent(getActivity(), FirebaseService.class);
        getActivity().startService(serviceIntent);
        mailboxDataSource = new MailboxDataSource();
        deliveryDataSource = new DeliveryDataSource();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        View actionBar = inflater.inflate(R.layout.actionbar_main, null);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        mailboxSpinner = (Spinner) actionBar.findViewById(R.id.main_mailbox_spinner);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setCustomView(actionBar);
        }
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeMailboxSpinner();
        initializeHistoryRecyclerView();

        auth = FirebaseAuth.getInstance();
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                LoginActivity.start(getActivity());
                getActivity().finish();
            }
        };
    }

    private void initializeMailboxSpinner() {
        mailboxAdapter = new MailboxAdapter();
        mailboxSpinner.setAdapter(mailboxAdapter);
        mailboxSpinner.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Mailbox mailbox = mailboxAdapter.getItem(position);
                if (historyAdapter != null) {
                    retrieveMailboxData(mailbox);
                    SemaphoreSharedPrefs.saveMailbox(getActivity(), mailbox.getMailboxId());
                }
            }
        });

        mailboxDataSource.query(getActivity(), mailboxes -> {
            String lastMailboxId = null;
            if (getArguments() != null) {
                lastMailboxId = getArguments().getString(ARG_MAILBOX_ID);
            }
            if (TextUtils.isEmpty(lastMailboxId)) {
                lastMailboxId = SemaphoreSharedPrefs.getLastMailbox(getActivity());
            }

            for (Mailbox mailbox : mailboxes) {
                mailboxAdapter.addItem(mailbox);
            }

            int lastMailboxPosition = mailboxAdapter.getItemPosition(lastMailboxId);
            if (lastMailboxPosition > -1) {
                mailboxSpinner.setSelection(lastMailboxPosition);
            } else {
                if (mailboxSpinner.getSelectedItemPosition() != -1) {
                    Mailbox selected = mailboxAdapter.getItem(mailboxSpinner.getSelectedItemPosition());
                    lastMailboxId = selected.getMailboxId();
                } else if (mailboxAdapter.getCount() > 0) {
                    Mailbox selected = mailboxAdapter.getItem(0);
                    lastMailboxId = selected.getMailboxId();
                }
                SemaphoreSharedPrefs.saveMailbox(getActivity(), lastMailboxId);
            }

            updateCurrentItems(SemaphoreSharedPrefs.getSnapshot(getActivity(),
                                                                lastMailboxId));
            refreshActionBar();
        });
    }

    private void initializeHistoryRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(layoutManager);
        historyAdapter = new DeliveryAdapter(getContext());
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void retrieveMailboxData(Mailbox mailbox) {
        emptyHistoryView.setVisibility(View.GONE);
        historyView.setVisibility(View.GONE);
        loadingHistoryView.setVisibility(View.VISIBLE);
        deliveryDataSource.query(getActivity(),
                                 mailbox.getMailboxId(),
                                 deliveries -> {
                                     historyAdapter.setDeliveries(deliveries);
                                     if (deliveries.size() > 0) {
                                         historyView.setVisibility(View.VISIBLE);
                                         emptyHistoryView.setVisibility(View.GONE);
                                     } else {
                                         historyView.setVisibility(View.GONE);
                                         emptyHistoryView.setVisibility(View.VISIBLE);
                                     }
                                     loadingHistoryView.setVisibility(View.GONE);
                                 });
        updateCurrentItems(SemaphoreSharedPrefs.getSnapshot(getActivity(), mailbox.getMailboxId()));
        DataBus.sendEvent(new AckEvent(mailbox.getMailboxId()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMailboxEvent(@NonNull MailboxEvent mailboxEvent) {
        mailboxAdapter.addItem(mailboxEvent.mailbox);
        refreshActionBar();
        mailboxDataSource.query(getActivity(),
                                results -> {
                                    for (Mailbox mailbox : results) {
                                        mailboxAdapter.addItem(mailbox);
                                    }
                                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSnapshotEvent(@NonNull SnapshotEvent snapshotEvent) {
        if (mailboxAdapter.getCount() <= 0) {
            return;
        }
        if (TextUtils.equals(mailboxAdapter.getItem(mailboxSpinner.getSelectedItemPosition())
                                           .getMailboxId(),
                             snapshotEvent.mailboxId)) {
            updateCurrentItems(snapshotEvent.delivery);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeliveryEvent(@NonNull DeliveryEvent deliveryEvent) {
        if (mailboxAdapter.getCount() <= 0) {
            return;
        }

        if (TextUtils.equals(mailboxAdapter.getItem(mailboxSpinner.getSelectedItemPosition())
                                           .getMailboxId(),
                             deliveryEvent.mailboxId)) {
            // re-query database
            deliveryDataSource.query(getActivity(),
                                     deliveryEvent.mailboxId,
                                     results -> historyAdapter.setDeliveries(results));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationEvent(@NonNull NotificationEvent notificationEvent) {
        String currentMailboxId = mailboxAdapter.getItem(mailboxSpinner.getSelectedItemPosition())
                                              .getMailboxId();

        Snackbar snackbar;
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder().append(notificationEvent.message);
        spanBuilder.setSpan(new ForegroundColorSpan(Color.WHITE),
                            0,
                            notificationEvent.message.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (TextUtils.equals(notificationEvent.mailboxId, currentMailboxId)) {
            snackbar = Snackbar.make(coordinatorLayout,
                                     spanBuilder,
                                     BaseTransientBottomBar.LENGTH_LONG);
        } else {
            snackbar = Snackbar.make(coordinatorLayout,
                                     spanBuilder,
                                     BaseTransientBottomBar.LENGTH_INDEFINITE);
            int position = mailboxAdapter.getItemPosition(notificationEvent.mailboxId);
            if (position != -1) {
                snackbar.setAction(R.string.notification_view_mailbox, (view) -> {
                    mailboxSpinner.setSelection(position);
                });
            }
            snackbar.setActionTextColor(ResourcesCompat.getColor(getContext().getResources(),
                                                                 R.color.colorAccent,
                                                                 null));
        }

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                DataBus.sendEvent(new AckEvent(notificationEvent.mailboxId));
            }
        });
        snackbar.show();
    }

    private void refreshActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        if (mailboxAdapter.getCount() > 1) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
        } else {
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void updateCurrentItems(@Nullable Delivery delivery) {
        DeliveryViewModel viewModel = DeliveryTransformer.toViewModel(getActivity(), 0, delivery);
        categorisingMailboxView.setVisibility(viewModel.isCategorising ? View.VISIBLE : View.GONE);

        emptyMailboxTextView.setVisibility(viewModel.numItems == 0 && !viewModel.isCategorising ? View.VISIBLE : View.GONE);

        ViewUtils.setTextAndUpdateVisibility(lettersTextView, viewModel.mLettersText);
        ViewUtils.setTextAndUpdateVisibility(magazinesTextView, viewModel.mMagazineText);
        ViewUtils.setTextAndUpdateVisibility(newspapersTextView, viewModel.mNewspapersText);
        ViewUtils.setTextAndUpdateVisibility(parcelsTextView, viewModel.mParcelsText);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_mailbox:
                MailboxActivity.startForResult(getActivity(), MainActivity.REQUEST_CODE_MAILBOX_ADD);
                return true;
            case R.id.menu_sign_out:
                auth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        DataBus.subscribe(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mailboxAdapter.getCount() > 0) {
            String currentMailboxId = mailboxAdapter.getItem(mailboxSpinner.getSelectedItemPosition())
                                                    .getMailboxId();
            DataBus.sendEvent(new AckEvent(currentMailboxId));
        }

        if (mailboxAdapter != null && mailboxAdapter.getCount() > 0) {
            retrieveMailboxData(mailboxAdapter.getItem(mailboxSpinner.getSelectedItemPosition()));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        DataBus.unsubscribe(this);
    }

    public static MainFragment getInstance(@Nullable String mailboxId) {
        MainFragment fragment = new MainFragment();
        if (!TextUtils.isEmpty(mailboxId)) {
            Bundle args = new Bundle();
            args.putString(ARG_MAILBOX_ID, mailboxId);
            fragment.setArguments(args);
        }
        return fragment;
    }
}
