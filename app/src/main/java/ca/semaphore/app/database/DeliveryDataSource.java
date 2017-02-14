package ca.semaphore.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.semaphore.app.data.DataBus;
import ca.semaphore.app.data.events.DeliveryEvent;
import ca.semaphore.app.database.schema.DeliverySchema;
import ca.semaphore.app.models.Delivery;

public class DeliveryDataSource {

    private static final String SELECT_WHERE = DeliverySchema.COLUMN_MAILBOX + " = ?"
                                               + " AND " + DeliverySchema.COLUMN_TIMESTAMP + " >= ?"
                                               + " AND NOT (" + DeliverySchema.COLUMN_LETTERS + "= 0"
                                               + " AND " + DeliverySchema.COLUMN_MAGAZINES + "= 0"
                                               + " AND " + DeliverySchema.COLUMN_NEWSPAPERS + "= 0"
                                               + " AND " + DeliverySchema.COLUMN_PARCELS + "= 0)";
    private static final String SELECT_GROUP_BY = "date(" + DeliverySchema.COLUMN_TIMESTAMP + ","
                                                  + " 'unixepoch', 'localtime')";
    private static final String SELECT_ORDER_BY = DeliverySchema.COLUMN_TIMESTAMP + " DESC";

    private static final String[] COLUMNS = {
            DeliverySchema.COLUMN_TIMESTAMP,
            "SUM(" + DeliverySchema.COLUMN_LETTERS + ")",
            "SUM(" + DeliverySchema.COLUMN_MAGAZINES + ")",
            "SUM(" + DeliverySchema.COLUMN_NEWSPAPERS + ")",
            "SUM(" + DeliverySchema.COLUMN_PARCELS + ")",
    };

    public void query(@NonNull Context context,
                      @NonNull final String mailboxId,
                      @Nullable final DatabaseCallback<List<Delivery>> callback) {
        final SemaphoreSQLiteOpenHelper helper = new SemaphoreSQLiteOpenHelper(context);
        new AsyncTask<Void, Void, List<Delivery>>() {
            @Override
            protected List<Delivery> doInBackground(Void... params) {
                SQLiteDatabase database = helper.getReadableDatabase();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -31); // 30 days of deliveries
                String[] selectionArgs = {
                        mailboxId,
                        Long.toString(cal.getTimeInMillis() / 1000),
                };
                List<Delivery> deliveries = new ArrayList<>();

                Cursor cursor = database.query(DeliverySchema.TABLE_NAME,
                                               COLUMNS, SELECT_WHERE,
                                               selectionArgs, SELECT_GROUP_BY,
                                               null, SELECT_ORDER_BY);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    deliveries.add(cursorToDelivery(cursor));
                    cursor.moveToNext();

                }
                cursor.close();
                return deliveries;
            }

            @Override
            protected void onPostExecute(List<Delivery> deliveries) {
                super.onPostExecute(deliveries);
                if (callback != null) {
                    callback.onSuccess(deliveries);
                }
                helper.close();
            }
        }.execute();
    }

    public void update(@NonNull Context context,
                       @NonNull final String mailbox,
                       @NonNull final Delivery delivery,
                       @Nullable final DatabaseCallback<Void> callback) {
        final SemaphoreSQLiteOpenHelper helper = new SemaphoreSQLiteOpenHelper(context);
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... params) {
                SQLiteDatabase database = helper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DeliverySchema.COLUMN_MAILBOX, mailbox);
                contentValues.put(DeliverySchema.COLUMN_TIMESTAMP, delivery.getTimestamp());
                contentValues.put(DeliverySchema.COLUMN_LETTERS, delivery.getLetters());
                contentValues.put(DeliverySchema.COLUMN_MAGAZINES, delivery.getMagazines());
                contentValues.put(DeliverySchema.COLUMN_NEWSPAPERS, delivery.getNewspapers());
                contentValues.put(DeliverySchema.COLUMN_PARCELS, delivery.getParcels());

                // using ignore here as data should never change on Firebase, only new data is added
                return database.insertWithOnConflict(DeliverySchema.TABLE_NAME,
                                                     null,
                                                     contentValues,
                                                     SQLiteDatabase.CONFLICT_IGNORE);
            }

            @Override
            protected void onPostExecute(Long result) {
                super.onPostExecute(result);
                if (callback != null) {
                    callback.onSuccess(null);
                }
                if (result != -1) {
                    DataBus.sendEvent(new DeliveryEvent(mailbox));
                }
                helper.close();
            }
        }.execute();
    }

    @NonNull
    private Delivery cursorToDelivery(@NonNull Cursor cursor) {
        return new Delivery(cursor.getLong(0),
                            cursor.isNull(1) ? 0 : cursor.getInt(1),
                            cursor.isNull(2) ? 0 : cursor.getInt(2),
                            cursor.isNull(3) ? 0 : cursor.getInt(3),
                            cursor.isNull(4) ? 0 : cursor.getInt(4));
    }
}
