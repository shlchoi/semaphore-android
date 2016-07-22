package ca.semaphore.app.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class Mailbox extends Object {

    @PropertyName("mailboxId")
    private String mMailboxId;

    @PropertyName("name")
    private String mName;

    public Mailbox() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Mailbox(@NonNull String mailboxId, @NonNull String name) {
        mMailboxId = mailboxId;
        mName = name;
    }

    public String getMailboxId() {
        return mMailboxId;
    }

    public void setMailboxId(@NonNull String mailboxId) {
        mMailboxId = mailboxId;
    }

    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Mailbox) {
            Mailbox o = (Mailbox) obj;
            return TextUtils.equals(o.getMailboxId(), getMailboxId());
        }

        return false;
    }
}
