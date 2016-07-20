package ca.semaphore.app.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class User {

    @PropertyName("userId")
    private String mUserId;

    @PropertyName("firstName")
    private String mFirstName;

    @PropertyName("lastName")
    private String mLastName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String firstName, String lastName) {
        mUserId = userId;
        mFirstName = firstName;
        mLastName = lastName;
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
}
