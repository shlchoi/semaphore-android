package ca.semaphore.app.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class SemaphoreSharedPrefs {

    private static final String SHARED_PREFS_NAME = "semaphore";
    private static final String LAST_MAILBOX = "mailbox";

    public static String getLastMailbox(@NonNull Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME,
                                                                    Context.MODE_PRIVATE);
        return sharedPref.getString(LAST_MAILBOX, null);
    }

    public static void saveMailbox(@NonNull Context context, @NonNull String mailboxId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME,
                                                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LAST_MAILBOX, mailboxId);
        editor.apply();
    }
}
