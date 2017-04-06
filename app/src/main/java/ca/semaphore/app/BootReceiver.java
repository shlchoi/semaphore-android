/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * BootReceiver.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import ca.semaphore.app.services.FirebaseService;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getCanonicalName();

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent arg) {
        if (TextUtils.equals(arg.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "Firebase BootReceiver Starting");
            Intent intent = new Intent(context, FirebaseService.class);
            context.startService(intent);
            Log.i(TAG, "Firebase BootReceiver Started");
        }
    }
}
