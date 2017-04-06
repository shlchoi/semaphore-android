/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * CollectionUtils.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public class CollectionUtils {

    /**
     * Private constructor to prevent instantiation
     */
    private CollectionUtils() { }

    public static boolean isEmpty(@Nullable Collection collection) {
        if (collection != null) {
            return collection.size() <= 0;
        }
        return true;
    }

    public static boolean isNonEmpty(@Nullable Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> void addIfNonNull(@NonNull Collection<T> collection, @Nullable List<T> objects) {
        if (isEmpty(objects)) {
            return;
        }

        for (T object : objects) {
            addIfNonNull(collection, object);
        }
    }

    public static <T> void addIfNonNull(@NonNull Collection<T> collection, @Nullable T object) {
        if (object != null) {
            collection.add(object);
        }
    }
}
