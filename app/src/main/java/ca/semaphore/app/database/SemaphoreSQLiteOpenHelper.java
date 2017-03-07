package ca.semaphore.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import ca.semaphore.app.database.schema.DeliverySchema;
import ca.semaphore.app.database.schema.MailboxSchema;

public class SemaphoreSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "semaphore.db";
    private static final int DATABASE_VERSION = 3;

    public SemaphoreSQLiteOpenHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MailboxSchema.CREATE);
        db.execSQL(DeliverySchema.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MailboxSchema.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DeliverySchema.TABLE_NAME);
        onCreate(db);
    }
}
