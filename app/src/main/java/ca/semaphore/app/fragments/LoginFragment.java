package ca.semaphore.app.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.semaphore.app.R;
import ca.semaphore.app.activities.LoginActivity;
import ca.semaphore.app.activities.MainActivity;
import ca.semaphore.app.activities.RegistrationActivity;

public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getCanonicalName();

    @BindView(R.id.login_email)
    TextView mEmail;

    @BindView(R.id.login_password)
    TextView mPassword;

    @BindView(R.id.login_button)
    Button mButton;

    @BindView(R.id.login_logo)
    View mLogo;

    @BindView(R.id.login_background)
    View mBackground;

    @BindView(R.id.login_form_layout)
    View mFormLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mReturning = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (mReturning) {
                        mLogo.animate().setDuration(500).setStartDelay(1000).translationY(0)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        MainActivity.start(getActivity());
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                    } else {
                        MainActivity.start(getActivity());
                        getActivity().finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    mReturning = false;
                    mLogo.animate().setDuration(500).setStartDelay(1000).translationY(0);
                    mBackground.animate().setDuration(500).setStartDelay(1000).translationY(0);
                    mFormLayout.animate().setDuration(500).setStartDelay(1000).translationY(0);
                    mEmail.setEnabled(true);
                    mPassword.setEnabled(true);
                }
            }
        };
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

    @OnClick(R.id.login_button)
    void onLoginClick() {
        Log.i(TAG, "Clicked");

        boolean valid = true;

        if (TextUtils.isEmpty(mEmail.getText())) {
            mEmail.setError(getString(R.string.login_error_email));
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.setError(getString(R.string.login_error_password));
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (!valid) {
            return;
        }

        mEmail.setEnabled(false);
        mPassword.setEnabled(false);
        mButton.setText(R.string.login_button_logging_in);
        mButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(LoginFragment.this.getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mEmail.setEnabled(true);
                            mPassword.setEnabled(true);
                            mButton.setText(R.string.login_button);
                            mButton.setEnabled(true);
                        }

                    }
                });

    }

    @OnClick(R.id.login_forgot_password)
    void onForgotPasswordClick() {

    }

    @OnClick(R.id.login_register)
    void onRegisterClick() {
        RegistrationActivity.startForResult(getActivity(), LoginActivity.REQUEST_CODE_REGISTER);
    }
}
