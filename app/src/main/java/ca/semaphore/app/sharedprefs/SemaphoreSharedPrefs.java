package ca.semaphore.app.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import ca.semaphore.app.models.Delivery;

public class SemaphoreSharedPrefs {

    private static final String SHARED_PREFS_NAME = "semaphore";
    private static final String KEY_LAST_MAILBOX = "mailbox";

    private static final String KEY_SNAPSHOT_TIMESTAMP = "%s_timestamp";
    private static final String KEY_SNAPSHOT_LETTERS = "%s_letters";
    private static final String KEY_SNAPSHOT_MAGAZINES = "%s_magazines";
    private static final String KEY_SNAPSHOT_NEWSPAPERS = "%s_newspapers";
    private static final String KEY_SNAPSHOT_PARCELS = "%s_parcels";

    @Nullable
    public static String getLastMailbox(@NonNull Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME,
                                                                    Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_LAST_MAILBOX, null);
    }

    public static void saveMailbox(@NonNull Context context, @NonNull String mailboxId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME,
                                                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LAST_MAILBOX, mailboxId);
        editor.apply();
    }

    @Nullable
    public static Delivery getSnapshot(@NonNull Context context, @Nullable String mailboxId) {
        if (TextUtils.isEmpty(mailboxId)) {
            return null;
        }

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME,
                                                                    Context.MODE_PRIVATE);
        long timestamp = sharedPref.getLong(String.format(KEY_SNAPSHOT_TIMESTAMP, mailboxId), 0L);
        int letters = sharedPref.getInt(String.format(KEY_SNAPSHOT_LETTERS, mailboxId), 0);
        int magazines = sharedPref.getInt(String.format(KEY_SNAPSHOT_MAGAZINES, mailboxId), 0);
        int newspapers = sharedPref.getInt(String.format(KEY_SNAPSHOT_NEWSPAPERS, mailboxId), 0);
        int parcels = sharedPref.getInt(String.format(KEY_SNAPSHOT_PARCELS, mailboxId), 0);

        if (timestamp == 0) {
            return null;
        }
        return new Delivery(timestamp, letters, magazines, newspapers, parcels);
    }

    public static void saveSnapshot(@NonNull Context context,
                                    @NonNull String mailboxId,
                                    @NonNull Delivery delivery) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME,
                                                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(String.format(KEY_SNAPSHOT_TIMESTAMP, mailboxId), delivery.getTimestamp());
        editor.putInt(String.format(KEY_SNAPSHOT_LETTERS, mailboxId), delivery.getLetters());
        editor.putInt(String.format(KEY_SNAPSHOT_MAGAZINES, mailboxId), delivery.getMagazines());
        editor.putInt(String.format(KEY_SNAPSHOT_NEWSPAPERS, mailboxId), delivery.getNewspapers());
        editor.putInt(String.format(KEY_SNAPSHOT_PARCELS, mailboxId), delivery.getParcels());
        editor.apply();
    }
}
