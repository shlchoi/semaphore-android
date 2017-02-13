package ca.semaphore.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ca.semaphore.app.R;

public class MailboxActivity extends AppCompatActivity {
    private static final String TAG = MailboxActivity.class.getCanonicalName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, MailboxActivity.class);
    }

    public static void start(@NonNull Context context) {
        context.startActivity(createIntent(context));
    }

    public static void startForResult(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(createIntent(activity), requestCode);
    }
}
