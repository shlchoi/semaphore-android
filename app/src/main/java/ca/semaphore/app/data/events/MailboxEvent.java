/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * MailboxEvent.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

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
