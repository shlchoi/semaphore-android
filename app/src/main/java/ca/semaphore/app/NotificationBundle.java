package ca.semaphore.app;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public class NotificationBundle {

    private static final String TOTAL = "notif_total";
    private static final String MAILBOX_ID = "notif_mailbox_id";
    private static final String MAILBOX_NAME = "notif_mailbox_name";

    private Bundle bundle;

    public NotificationBundle(@IntRange(from = 0) int total,
                              @NonNull String mailboxId,
                              @NonNull String mailboxName) {
        this(new Bundle(), total, mailboxId, mailboxName);
    }

    public NotificationBundle(@NonNull Bundle bundle,
                              @IntRange(from = 0) int total,
                              @NonNull String mailboxId,
                              @NonNull String mailboxName) {
        this.bundle = bundle;
        putTotal(total);
        putMailboxId(mailboxId);
        putMailboxName(mailboxName);
    }

    public void putTotal(@IntRange(from = 0) int total) {
        bundle.putInt(TOTAL, total);
    }

    public void putMailboxId(@NonNull String mailboxId) {
        bundle.putString(MAILBOX_ID, mailboxId);
    }

    public void putMailboxName(@NonNull String mailboxName) {
        bundle.putString(MAILBOX_NAME, mailboxName);
    }

    public Bundle bundle() {
        return bundle;
    }

    public static int getTotal(@NonNull Bundle bundle) {
        return bundle.getInt(TOTAL, 0);
    }

    public static String getMailboxId(@NonNull Bundle bundle) {
        return bundle.getString(MAILBOX_ID);
    }

    public static String getMailboxName(@NonNull Bundle bundle) {
        return bundle.getString(MAILBOX_NAME);
    }
}
