/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * SemaphoreActivity.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

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
