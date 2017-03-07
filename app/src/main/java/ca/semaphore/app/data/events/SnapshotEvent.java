package ca.semaphore.app.data.events;

import android.support.annotation.NonNull;

import ca.semaphore.app.data.Event;
import ca.semaphore.app.models.Delivery;

public class SnapshotEvent implements Event {

    @NonNull
    public String mailboxId;

    @NonNull
    public Delivery delivery;

    public SnapshotEvent(@NonNull String mailboxId, @NonNull Delivery snapshot) {
        this.mailboxId = mailboxId;
        this.delivery = snapshot;
    }
}
