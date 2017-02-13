package ca.semaphore.app.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.HashMap;

@IgnoreExtraProperties
public class User {

    @PropertyName("userId")
    private String mUserId;

    @PropertyName("firstName")
    private String mFirstName;

    @PropertyName("lastName")
    private String mLastName;

    @PropertyName("mailboxes")
    private HashMap<String, Mailbox> mMailboxes;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(@NonNull String userId, @NonNull String firstName, @NonNull String lastName) {
        mUserId = userId;
        mFirstName = firstName;
        mLastName = lastName;

        mMailboxes = new HashMap<>();
    }

    public String getUserId() {
        return mUserId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public HashMap<String, Mailbox> getMailboxes() {
        return mMailboxes;
    }

    public Mailbox getMailbox(@NonNull String key) {
        return mMailboxes.get(key);
    }

    public void addMailbox(@NonNull Mailbox mailbox) {
        mMailboxes.put(mailbox.getMailboxId(), mailbox);
    }
}
