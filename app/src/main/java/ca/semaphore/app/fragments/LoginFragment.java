package ca.semaphore.app.fragments;

import android.animation.Animator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.semaphore.app.R;
import ca.semaphore.app.activities.LoginActivity;
import ca.semaphore.app.activities.MainActivity;
import ca.semaphore.app.activities.RegistrationActivity;
import ca.semaphore.app.utils.listeners.AnimatorListener;

public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getCanonicalName();

    @BindView(R.id.login_email)
    TextView mEmail;

    @BindView(R.id.login_password)
    TextView mPassword;

    @BindView(R.id.login_button)
    Button mButton;

    @BindView(R.id.login_logo_container)
    View mLogoContainer;

    @BindView(R.id.login_logo)
    ImageView mLogo;

    @BindView(R.id.login_background)
    View mBackground;

    @BindView(R.id.login_form_layout)
    View mFormLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean mReturning = true;
    private boolean runAnimation = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (mReturning) {
                    Drawable drawable = mLogo.getDrawable();
                    if (drawable instanceof Animatable) {
                        Animatable animatable = (Animatable) drawable;
                        animatable.start();
                    }

                    mLogoContainer.animate()
                                  .setDuration(500)
                                  .setStartDelay(1000)
                                  .translationY(0)
                                  .setListener(new AnimatorListener() {
                                      @Override
                                      public void onAnimationEnd(Animator animator) {
                                          MainActivity.start(getActivity());
                                          getActivity().finish();
                                      }
                                  });
                } else {
                    MainActivity.start(getActivity());
                    getActivity().finish();
                }
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                mReturning = false;

                if (runAnimation) {
                    Drawable drawable = mLogo.getDrawable();
                    if (drawable instanceof Animatable) {
                        Animatable animatable = (Animatable) drawable;
                        animatable.start();
                    }
                    mLogoContainer.animate()
                                  .setDuration(500)
                                  .setStartDelay(1000)
                                  .translationY(0)
                                  .setListener(new AnimatorListener() {
                                      @Override
                                      public void onAnimationStart(Animator animation) {
                                          runAnimation = false;
                                      }
                                  });
                    mBackground.animate().setDuration(500).setStartDelay(1000).translationY(0);
                    mFormLayout.animate().setDuration(500).setStartDelay(1000).translationY(0);
                }
                mEmail.setEnabled(true);
                mPassword.setEnabled(true);
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
        if (!verifyInputs()) {
            return;
        }

        mEmail.setEnabled(false);
        mPassword.setEnabled(false);
        mButton.setText(R.string.login_button_logging_in);
        mButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(getActivity(), task -> {
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

                });

    }

    private boolean verifyInputs() {
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

        return valid;
    }

    @OnClick(R.id.login_forgot_password)
    void onForgotPasswordClick() {

    }

    @OnClick(R.id.login_register)
    void onRegisterClick() {
        RegistrationActivity.startForResult(getActivity(), LoginActivity.REQUEST_CODE_REGISTER);
    }
}
