package ca.semaphore.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import ca.semaphore.app.data.DataBus;
import ca.semaphore.app.data.events.SnapshotEvent;
import ca.semaphore.app.models.Delivery;

public class DataStore {

    private static DataStore instance;
    private HashMap<String, Delivery> snapshotMap;

    private DataStore() {
        snapshotMap = new HashMap<>();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void addSnapshot(@NonNull String mailboxId, @NonNull Delivery delivery) {
        Delivery previous = null;
        if (snapshotMap.containsKey(mailboxId)) {
            previous = snapshotMap.get(mailboxId);
        }

        if (previous == null || previous.getTimestamp() < delivery.getTimestamp()) {
            snapshotMap.put(mailboxId, delivery);
            DataBus.sendEvent(new SnapshotEvent(mailboxId));
        }
    }

    @Nullable
    public Delivery getLastSnapshot(@NonNull String mailboxId) {
        return snapshotMap.get(mailboxId);
    }
}
