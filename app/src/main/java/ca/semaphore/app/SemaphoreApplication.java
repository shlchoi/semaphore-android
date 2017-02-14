package ca.semaphore.app;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

public final class SemaphoreApplication extends Application {

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
