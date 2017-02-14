package ca.semaphore.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import ca.semaphore.app.R;
import ca.semaphore.app.fragments.MainFragment;

public class MainActivity extends SemaphoreActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final int REQUEST_CODE_MAILBOX_ADD = 0x01;
    public static final String EXTRA_MAILBOX_ID = "mailbox_id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment fragment = MainFragment.getInstance(getIntent().getStringExtra(EXTRA_MAILBOX_ID));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, fragment);
        transaction.commit();
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String mailboxId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_MAILBOX_ID, mailboxId);
        return intent;
    }

    public static void start(@NonNull Context context) {
        context.startActivity(createIntent(context));
    }
}
