package ca.semaphore.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import ca.semaphore.app.DataStore;
import ca.semaphore.app.R;
import ca.semaphore.app.activities.LoginActivity;
import ca.semaphore.app.activities.MailboxActivity;
import ca.semaphore.app.activities.MainActivity;
import ca.semaphore.app.adapters.DeliveryAdapter;
import ca.semaphore.app.adapters.MailboxAdapter;
import ca.semaphore.app.data.DataBus;
import ca.semaphore.app.data.events.DeliveryEvent;
import ca.semaphore.app.data.events.MailboxEvent;
import ca.semaphore.app.data.events.SnapshotEvent;
import ca.semaphore.app.database.DeliveryDataSource;
import ca.semaphore.app.database.MailboxDataSource;
import ca.semaphore.app.models.Mailbox;
import ca.semaphore.app.services.FirebaseService;
import ca.semaphore.app.sharedprefs.SemaphoreSharedPrefs;
import ca.semaphore.app.transformers.DeliveryTransformer;
import ca.semaphore.app.utils.ViewUtils;
import ca.semaphore.app.viewmodels.DeliveryViewModel;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getCanonicalName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Spinner mailboxSpinner;

    private MailboxAdapter mailboxAdapter;
    private DeliveryAdapter historyAdapter;

    private MailboxDataSource mailboxDataSource;
    private DeliveryDataSource deliveryDataSource;

    @BindView(R.id.main_letters)
    TextView lettersTextView;

    @BindView(R.id.main_magazines)
    TextView magazinesTextView;

    @BindView(R.id.main_newspapers)
    TextView newspapersTextView;

    @BindView(R.id.main_parcels)
    TextView parcelsTextView;

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
        View view = inflater.inflate(R.layout.fragment_main, container);
        View actionBar = inflater.inflate(R.layout.actionbar_main, null);
        ButterKnife.bind(this, view);

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
        mailboxAdapter = new MailboxAdapter(getActivity());
        mailboxSpinner.setAdapter(mailboxAdapter);
        mailboxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Mailbox mailbox = mailboxAdapter.getItem(position);
                if (historyAdapter != null) {
                    deliveryDataSource.query(getActivity(),
                                             mailbox.getMailboxId(),
                                             deliveries -> historyAdapter.setDeliveries(deliveries));
                    updateCurrentItems(mailbox.getMailboxId());
                    SemaphoreSharedPrefs.saveMailbox(getActivity(), mailbox.getMailboxId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mailboxDataSource.query(getActivity(),
                                mailboxes -> {
                                    String lastMailboxId = SemaphoreSharedPrefs.getLastMailbox(getActivity());
                                    int lastMailboxPosition = -1;

                                    for (int i = 0; i < mailboxes.size(); i++) {
                                        Mailbox mailbox = mailboxes.get(i);
                                        mailboxAdapter.addItem(mailbox);
                                        if (TextUtils.equals(mailbox.getMailboxId(), lastMailboxId)) {
                                            lastMailboxPosition = i;
                                        }
                                    }

                                    if (lastMailboxPosition > -1) {
                                        mailboxSpinner.setSelection(lastMailboxPosition);
                                    }
                                    refreshActionBar();
                                });
    }

    private void initializeHistoryRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(historyRecyclerView.getContext(),
                                                                  layoutManager.getOrientation());
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        historyRecyclerView.addItemDecoration(divider);
        historyAdapter = new DeliveryAdapter(getContext());
        historyRecyclerView.setAdapter(historyAdapter);
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
            updateCurrentItems(snapshotEvent.mailboxId);
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

    private void updateCurrentItems(@NonNull String mailboxId) {
        DeliveryViewModel viewModel = DeliveryTransformer.toViewModel(getActivity(),
                                                                      DataStore.getInstance()
                                                                               .getLastSnapshot(mailboxId));
        ViewUtils.setTextAndUpdateVisibility(lettersTextView, viewModel.mLettersText);
        ViewUtils.setTextAndUpdateVisibility(magazinesTextView, viewModel.mMagazineText);
        ViewUtils.setTextAndUpdateVisibility(newspapersTextView, viewModel.mNewspapersText);
        ViewUtils.setTextAndUpdateVisibility(parcelsTextView, viewModel.mParcelsText);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        DataBus.unsubscribe(this);
    }
}
