package ca.semaphore.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ca.semaphore.app.data.DataBus;
import ca.semaphore.app.data.events.MailboxEvent;
import ca.semaphore.app.database.schema.MailboxSchema;
import ca.semaphore.app.models.Mailbox;

public class MailboxDataSource {

    private static final String DELETE_WHERE = MailboxSchema.COLUMN_MAILBOX_ID + "= ?";

    private static final String[] COLUMNS = {
            MailboxSchema.COLUMN_MAILBOX_ID,
            MailboxSchema.COLUMN_NAME,
    };

    public void query(@NonNull Context context,
                      @Nullable final DatabaseCallback<List<Mailbox>> callback) {
        final SemaphoreSQLiteOpenHelper helper = new SemaphoreSQLiteOpenHelper(context);
        new AsyncTask<Void, Void, List<Mailbox>>() {
            @Override
            protected List<Mailbox> doInBackground(Void... params) {
                SQLiteDatabase database = helper.getReadableDatabase();
                List<Mailbox> mailboxes = new ArrayList<>();

                Cursor cursor = database.query(MailboxSchema.TABLE_NAME,
                                               COLUMNS,
                                               null,
                                               null,
                                               null,
                                               null,
                                               null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    mailboxes.add(cursorToMailbox(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
                return mailboxes;
            }

            @Override
            protected void onPostExecute(List<Mailbox> mailboxes) {
                super.onPostExecute(mailboxes);
                if (callback != null) {
                    callback.onSuccess(mailboxes);
                }
                helper.close();
            }
        }.execute();
    }

    public void update(@NonNull Context context,
                       @NonNull final Mailbox mailbox,
                       @Nullable final DatabaseCallback<Void> callback) {
        final SemaphoreSQLiteOpenHelper helper = new SemaphoreSQLiteOpenHelper(context);
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... params) {
                SQLiteDatabase database = helper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MailboxSchema.COLUMN_MAILBOX_ID, mailbox.getMailboxId());
                contentValues.put(MailboxSchema.COLUMN_NAME, mailbox.getName());

                return database.insertWithOnConflict(MailboxSchema.TABLE_NAME,
                                                     null,
                                                     contentValues,
                                                     SQLiteDatabase.CONFLICT_REPLACE);
            }

            @Override
            protected void onPostExecute(Long result) {
                super.onPostExecute(result);
                if (callback != null) {
                    callback.onSuccess(null);
                }
                if (result != -1) {
                    DataBus.sendEvent(new MailboxEvent(mailbox));
                }
                helper.close();
            }
        }.execute();
    }

    public void delete(@NonNull Context context,
                       @NonNull final String mailboxId,
                       @Nullable final DatabaseCallback<Void> callback) {
        final SemaphoreSQLiteOpenHelper helper = new SemaphoreSQLiteOpenHelper(context);
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                SQLiteDatabase database = helper.getWritableDatabase();
                String[] selectionArgs = {
                        mailboxId,
                };
                return database.delete(MailboxSchema.TABLE_NAME,
                                       DELETE_WHERE,
                                       selectionArgs);
            }

            @Override
            protected void onPostExecute(Integer count) {
                super.onPostExecute(count);
                if (callback != null) {
                    callback.onSuccess(null);
                }
                helper.close();
            }
        }.execute();
    }

    @NonNull
    private Mailbox cursorToMailbox(@NonNull Cursor cursor) {
        return new Mailbox(cursor.getString(0), cursor.getString(1));
    }
}
