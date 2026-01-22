package me.zubair.taskmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper class for managing SQLite operations
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "TaskDbHelper";
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2; // Increased from 1 to 2

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tasks table
        db.execSQL(TaskContract.TaskEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle migrations between different database versions
        if (oldVersion < 2) {
            // Add created_at column if upgrading from version 1
            try {
                db.execSQL("ALTER TABLE " + TaskContract.TaskEntry.TABLE_NAME + 
                          " ADD COLUMN " + TaskContract.TaskEntry.COLUMN_CREATED_AT + " INTEGER;");
                Log.i(TAG, "Added created_at column to tasks table");
            } catch (Exception e) {
                Log.e(TAG, "Error adding created_at column: " + e.getMessage());
            }
        }
    }
}
