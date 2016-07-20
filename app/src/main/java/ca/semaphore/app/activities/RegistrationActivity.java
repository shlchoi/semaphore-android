package ca.semaphore.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ca.semaphore.app.R;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = RegistrationActivity.class.getCanonicalName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private static Intent createIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    public static void start(Context context) {
        context.startActivity(createIntent(context));
    }

    public static void startForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(createIntent(activity), requestCode);
    }
}