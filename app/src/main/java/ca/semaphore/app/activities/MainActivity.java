package ca.semaphore.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ca.semaphore.app.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final int REQUEST_CODE_MAILBOX_ADD = 0x01;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static void start(@NonNull Context context) {
        context.startActivity(createIntent(context));
    }
}
