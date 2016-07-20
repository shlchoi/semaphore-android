package ca.semaphore.app.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class Mailbox {

    @PropertyName("mailboxId")
    private String mMailboxId;

    @PropertyName("name")
    private String mName;

    @PropertyName("owner")
    private String mOwner;

    public Mailbox() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Mailbox(String mailboxId, String name, String owner) {
        mMailboxId = mailboxId;
        mName = name;
        mOwner = owner;
    }

    public String getMailboxId() {
        return mMailboxId;
    }

    public String getName() {
        return mName;
    }

    public String getOwner() {
        return mOwner;
    }
}
