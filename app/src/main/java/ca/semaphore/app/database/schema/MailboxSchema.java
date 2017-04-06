/**************************************************************************************************
 * Semaphore - Android
 * Android application accompanying Semaphore
 * See https://shlchoi.github.io/semaphore/ for more information about Semaphore
 *
 * MailboxSchema.java
 * Copyright (C) 2017 Samson H. Choi
 *
 * See https://github.com/shlchoi/semaphore-android/blob/master/LICENSE for license information
 *************************************************************************************************/

package ca.semaphore.app.database.schema;

public interface MailboxSchema {
    String TABLE_NAME = "mailboxes";

    String COLUMN_MAILBOX_ID = "mailbox_id";
    String COLUMN_NAME = "name";

    String CREATE = "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_MAILBOX_ID + " TEXT NOT NULL PRIMARY KEY, "
                    + COLUMN_NAME       + " TEXT NOT NULL"
                    + ");";
}
