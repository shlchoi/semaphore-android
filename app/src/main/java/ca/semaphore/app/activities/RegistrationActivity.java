/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * RegistrationActivity.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private static Intent createIntent(@NonNull Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    public static void start(@NonNull Context context) {
        context.startActivity(createIntent(context));
    }

    public static void startForResult(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(createIntent(activity), requestCode);
    }
}
