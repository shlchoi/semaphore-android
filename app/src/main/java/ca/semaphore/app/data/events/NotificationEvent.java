/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * NotificationEvent.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

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
