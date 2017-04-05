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
import ca.semaphore.app.models.User;

public class RegistrationFragment extends Fragment {

    private static final String TAG = RegistrationFragment.class.getCanonicalName();

    @BindView(R2.id.registration_first_name)
    EditText mFirstName;

    @BindView(R2.id.registration_last_name)
    EditText mLastName;

    @BindView(R2.id.registration_email)
    EditText mEmail;

    @BindView(R2.id.registration_password)
    EditText mPassword;

    @BindView(R2.id.registration_confirm_password)
    EditText mConfirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container);
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

    @OnClick(R2.id.registration_button)
    public void onRegisterClick() {
        boolean valid = true;
        if (TextUtils.isEmpty(mFirstName.getText())) {
            mFirstName.setError(getString(R.string.registration_error_first_name));
            valid = false;
        } else {
            mFirstName.setError(null);
        }

        if (TextUtils.isEmpty(mLastName.getText())) {
            mLastName.setError(getString(R.string.registration_error_last_name));
            valid = false;
        } else {
            mLastName.setError(null);
        }

        if (TextUtils.isEmpty(mEmail.getText())) {
            mEmail.setError(getString(R.string.registration_error_email));
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.setError(getString(R.string.registration_error_password));
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (TextUtils.isEmpty(mConfirmPassword.getText())) {
            mConfirmPassword.setError(getString(R.string.registration_error_confirm_password));
            valid = false;
        } else if (!TextUtils.isEmpty(mPassword.getText()) && !TextUtils.isEmpty(mConfirmPassword.getText())
                && !TextUtils.equals(mPassword.getText(), mConfirmPassword.getText())) {
            mConfirmPassword.setError(getString(R.string.registration_error_password_match));
            valid = false;
        } else {
            mConfirmPassword.setError(null);
        }

        if (!valid) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(getActivity(), task -> {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegistrationFragment.this.getActivity(),
                                "Authentication failed.", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        User user = new User(firebaseUser.getUid(),
                                             mFirstName.getText().toString(),
                                             mLastName.getText().toString());

                        mDatabase.child("users").child(user.getUserId()).setValue(user);

                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                });
    }
}
