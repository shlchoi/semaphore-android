/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * Delivery.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.models;

import android.support.annotation.IntRange;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class Delivery {

    @PropertyName("timestamp")
    private long timestamp;

    @PropertyName("letters")
    private int letters;

    @PropertyName("magazines")
    private int magazines;

    @PropertyName("newspapers")
    private int newspapers;

    @PropertyName("parcels")
    private int parcels;

    @PropertyName("categorising")
    private boolean categorising;

    public Delivery() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Delivery(long timestamp,
                    @IntRange(from=0) int letters,
                    @IntRange(from=0) int magazines,
                    @IntRange(from=0) int newspapers,
                    @IntRange(from=0) int parcels) {
        this(timestamp, letters, magazines, newspapers, parcels, false);
    }

    public Delivery(long timestamp,
                    @IntRange(from=0) int letters,
                    @IntRange(from=0) int magazines,
                    @IntRange(from=0) int newspapers,
                    @IntRange(from=0) int parcels,
                    boolean categorising) {
        this.timestamp = timestamp;
        this.letters = letters;
        this.magazines = magazines;
        this.newspapers = newspapers;
        this.parcels = parcels;
        this.categorising = categorising;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLetters() {
        return letters;
    }

    public int getMagazines() {
        return magazines;
    }

    public int getNewspapers() {
        return newspapers;
    }

    public int getParcels() {
        return parcels;
    }

    public int getTotal() {
        return letters + magazines + newspapers + parcels;
    }

    public boolean isCategorising() {
        return categorising;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("timestamp: ").append(timestamp).append("\n");
        builder.append(letters).append(" letters\n")
               .append(magazines).append(" magazines\n")
               .append(newspapers).append(" newspapers\n")
               .append(parcels).append(" parcels\n")
               .append("categorising: ").append(categorising);
        return builder.toString();
    }
}