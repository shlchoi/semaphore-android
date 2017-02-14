package ca.semaphore.app.activities;

import android.support.v7.app.AppCompatActivity;

import ca.semaphore.app.SemaphoreApplication;

public abstract class SemaphoreActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        SemaphoreApplication.onActivityResumed();
    }

    @Override
    protected void onPause() {
        SemaphoreApplication.onActivityPaused();
        super.onPause();
    }
}
