package ca.semaphore.app.database;

public interface DatabaseCallback<T> {
    void onSuccess(T results);
}
