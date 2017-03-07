package ca.semaphore.app.data.events;

import android.support.annotation.NonNull;

import ca.semaphore.app.data.Event;

public class AckEvent implements Event {
    @NonNull
    public String mailboxId;

    public AckEvent(@NonNull String mailboxId) {
        this.mailboxId = mailboxId;
    }
}
