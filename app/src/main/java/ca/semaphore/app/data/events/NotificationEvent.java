package ca.semaphore.app.data.events;

import android.support.annotation.NonNull;

import ca.semaphore.app.data.Event;

public class NotificationEvent implements Event {

    @NonNull
    public final String mailboxId;

    @NonNull
    public final String message;

    public NotificationEvent(String mailboxId, String message) {
        this.mailboxId = mailboxId;
        this.message = message;
    }
}
