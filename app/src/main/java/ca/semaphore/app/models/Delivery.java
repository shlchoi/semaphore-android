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

    public Delivery() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Delivery(long timestamp,
                    @IntRange(from=0) int letters,
                    @IntRange(from=0) int magazines,
                    @IntRange(from=0) int newspapers,
                    @IntRange(from=0) int parcels) {
        this.timestamp = timestamp;
        this.letters = letters;
        this.magazines = magazines;
        this.newspapers = newspapers;
        this.parcels = parcels;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("timestamp: ").append(timestamp).append("\n");
        builder.append(letters).append(" letters\n")
               .append(magazines).append(" magazines\n")
               .append(newspapers).append(" newspapers\n")
               .append(parcels).append(" parcels\n");
        return builder.toString();
    }
}