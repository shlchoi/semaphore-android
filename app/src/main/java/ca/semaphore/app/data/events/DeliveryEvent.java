package ca.semaphore.app.data.events;

import android.support.annotation.NonNull;

import ca.semaphore.app.data.Event;

public class DeliveryEvent implements Event {

    @NonNull
    public String mailboxId;

    public DeliveryEvent(@NonNull String mailboxId) {
        this.mailboxId = mailboxId;
    }
}
