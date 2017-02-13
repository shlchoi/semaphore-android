package ca.semaphore.app.data.events;

import android.support.annotation.NonNull;

import ca.semaphore.app.data.Event;
import ca.semaphore.app.models.Mailbox;

public class MailboxEvent implements Event {

    @NonNull
    public Mailbox mailbox;

    public MailboxEvent(@NonNull Mailbox mailbox) {
        this.mailbox = mailbox;
    }
}
