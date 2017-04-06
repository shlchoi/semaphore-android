/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * DataBus.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.data;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

public class DataBus {
    private static EventBus bus = new EventBus();

    private DataBus() { }

    public static void sendEvent(@NonNull Event event) {
        bus.post(event);
    }

    public static void subscribe(@NonNull Object subscriber) {
        bus.register(subscriber);
    }

    public static void unsubscribe(@NonNull Object subscriber) {
        bus.unregister(subscriber);
    }
}
