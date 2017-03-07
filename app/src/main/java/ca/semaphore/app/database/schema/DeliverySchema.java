package ca.semaphore.app.database.schema;

public interface DeliverySchema {
    String TABLE_NAME = "deliveries";

    String COLUMN_MAILBOX = "mailbox";
    String COLUMN_TIMESTAMP = "timestamp";
    String COLUMN_LETTERS = "letters";
    String COLUMN_MAGAZINES = "magazines";
    String COLUMN_NEWSPAPERS = "newspapers";
    String COLUMN_PARCELS = "parcels";
    String COLUMN_CATEGORISING = "categorising";

    String CREATE = "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_MAILBOX + " TEXT NOT NULL, "
                    + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                    + COLUMN_LETTERS + " INTEGER DEFAULT 0, "
                    + COLUMN_MAGAZINES + " INTEGER DEFAULT 0, "
                    + COLUMN_NEWSPAPERS + " INTEGER DEFAULT 0, "
                    + COLUMN_PARCELS + " INTEGER DEFAULT 0, "
                    + COLUMN_CATEGORISING + " INTEGER DEFAULT 0, "
                    + "PRIMARY KEY (" + COLUMN_MAILBOX + ", " + COLUMN_TIMESTAMP + ")"
                    + ");";
}
