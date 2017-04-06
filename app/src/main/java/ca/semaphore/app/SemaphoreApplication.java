/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * SemaphoreApplication.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app;

import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

public final class SemaphoreApplication extends MultiDexApplication {

    private static boolean isVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static boolean isVisible() {
        return isVisible;
    }

    public static void onActivityResumed() {
        isVisible = true;
    }

    public static void onActivityPaused() {
        isVisible = false;
    }
}
