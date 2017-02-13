package ca.semaphore.app.data.events;

import android.support.annotation.NonNull;

import ca.semaphore.app.data.Event;

public class SnapshotEvent implements Event {

    public String mailboxId;

    public SnapshotEvent(@NonNull String mailboxId) {
        this.mailboxId = mailboxId;
    }
}
