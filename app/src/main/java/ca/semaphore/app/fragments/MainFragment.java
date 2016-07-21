package ca.semaphore.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import ca.semaphore.app.R;
import ca.semaphore.app.activities.LoginActivity;
import ca.semaphore.app.activities.MailboxActivity;
import ca.semaphore.app.activities.MainActivity;
import ca.semaphore.app.adapters.MailboxAdapter;
import ca.semaphore.app.models.Mailbox;
import ca.semaphore.app.models.User;
import ca.semaphore.app.utils.ViewUtils;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getCanonicalName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    private Spinner mMailboxSpinner;

    private MailboxAdapter mMailboxAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container);
        View actionBar = inflater.inflate(R.layout.actionbar_main, null);
        ButterKnife.bind(this, view);

        mMailboxSpinner = ViewUtils.findView(actionBar, R.id.main_mailbox_spinner);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(actionBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMailboxAdapter = new MailboxAdapter(getActivity());
        mMailboxSpinner.setAdapter(mMailboxAdapter);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value
                                        User user = dataSnapshot.getValue(User.class);
                                        Toast.makeText(getActivity(), "Retrieved info for " + user.getFirstName(),
                                                Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });

                    Query mailboxQuery = mDatabase.child("mailboxes").orderByChild("owner").equalTo(user.getUid());
                    mailboxQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Mailbox mailbox = dataSnapshot.getValue(Mailbox.class);
                            Log.i(TAG, "Retrieved mailbox " + mailbox.getMailboxId());

                            if (mMailboxAdapter.getCount() == 2) {
                                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                                actionBar.setDisplayShowTitleEnabled(false);
                                actionBar.setDisplayShowCustomEnabled(true);
                            }
                            mMailboxAdapter.addItem(mailbox);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    LoginActivity.start(getActivity());
                    getActivity().finish();
                }
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_mailbox:
                MailboxActivity.startForResult(getActivity(), MainActivity.REQUEST_CODE_MAILBOX_ADD);
                return true;
            case R.id.menu_sign_out:
                mAuth.signOut();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
