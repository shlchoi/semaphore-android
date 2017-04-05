package ca.semaphore.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.semaphore.app.R;
import ca.semaphore.app.R2;
import ca.semaphore.app.models.Mailbox;

public class MailboxFragment extends Fragment {

    private static final String TAG = MailboxFragment.class.getCanonicalName();

    @BindView(R2.id.mailbox_name)
    EditText mMailboxName;

    @BindView(R2.id.mailbox_serial)
    EditText mMailboxSerial;

    @BindView(R2.id.mailbox_button)
    Button mMailboxButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mailbox, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @OnClick(R2.id.mailbox_button)
    public void onMailboxClick() {
        boolean valid = true;
        if (TextUtils.isEmpty(mMailboxName.getText())) {
            mMailboxName.setError(getString(R.string.mailbox_error_name));
            valid = false;
        } else {
            mMailboxName.setError(null);
        }

        if (TextUtils.isEmpty(mMailboxSerial.getText())) {
            mMailboxSerial.setError(getString(R.string.mailbox_error_serial));
            valid = false;
        } else {
            mMailboxSerial.setError(null);
        }

        if (!valid) {
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        Mailbox mailbox = new Mailbox(mMailboxSerial.getText().toString(),
                mMailboxName.getText().toString());
        mDatabase.child("users")
                 .child(user.getUid())
                 .child("mailboxes")
                 .child(mailbox.getMailboxId())
                 .setValue(mailbox)
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful()) {
                         Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                         getActivity().setResult(Activity.RESULT_OK);
                         getActivity().finish();
                     }
                 });
    }
}
