package me.zubair.taskmanager.database;

import android.provider.BaseColumns;

/**
 * Contract class for the Task database table
 */
public final class TaskContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TaskContract() {}

    /* Inner class that defines the table contents */
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_COMPLETED = "completed";
        public static final String COLUMN_CREATED_AT = "created_at";
        
        // SQL statement to create the table
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_TITLE + " TEXT NOT NULL," +
                        COLUMN_DESCRIPTION + " TEXT," +
                        COLUMN_DUE_DATE + " INTEGER," +
                        COLUMN_PRIORITY + " INTEGER DEFAULT 1," +
                        COLUMN_COMPLETED + " INTEGER DEFAULT 0," +
                        COLUMN_CREATED_AT + " INTEGER)";
        
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
