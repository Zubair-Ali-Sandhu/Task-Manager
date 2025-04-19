package me.zubair.taskmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.zubair.taskmanager.models.Task;

public class TaskRepository {
    private static final String TAG = "TaskRepository";
    private final TaskDbHelper dbHelper;

    public TaskRepository(Context context) {
        this.dbHelper = new TaskDbHelper(context);
    }

    /**
     * Get all tasks from the database
     */
    public List<Task> getAllTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();

        // Get the table info to check if created_at column exists
        boolean hasCreatedAtColumn = hasColumn(db, TaskContract.TaskEntry.TABLE_NAME, 
                                              TaskContract.TaskEntry.COLUMN_CREATED_AT);
        
        // Build projection based on available columns
        List<String> projectionList = new ArrayList<>();
        projectionList.add(TaskContract.TaskEntry._ID);
        projectionList.add(TaskContract.TaskEntry.COLUMN_TITLE);
        projectionList.add(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        projectionList.add(TaskContract.TaskEntry.COLUMN_DUE_DATE);
        projectionList.add(TaskContract.TaskEntry.COLUMN_PRIORITY);
        projectionList.add(TaskContract.TaskEntry.COLUMN_COMPLETED);
        
        if (hasCreatedAtColumn) {
            projectionList.add(TaskContract.TaskEntry.COLUMN_CREATED_AT);
        }
        
        String[] projection = projectionList.toArray(new String[0]);

        try (Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                TaskContract.TaskEntry.COLUMN_DUE_DATE + " ASC"
        )) {
            while (cursor.moveToNext()) {
                Task task = extractTaskFromCursor(cursor);
                tasks.add(task);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all tasks: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Get task by ID
     */
    public Task getTaskById(long taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Task task = null;

        // Get the table info to check if created_at column exists
        boolean hasCreatedAtColumn = hasColumn(db, TaskContract.TaskEntry.TABLE_NAME, 
                                              TaskContract.TaskEntry.COLUMN_CREATED_AT);
        
        // Build projection based on available columns
        List<String> projectionList = new ArrayList<>();
        projectionList.add(TaskContract.TaskEntry._ID);
        projectionList.add(TaskContract.TaskEntry.COLUMN_TITLE);
        projectionList.add(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        projectionList.add(TaskContract.TaskEntry.COLUMN_DUE_DATE);
        projectionList.add(TaskContract.TaskEntry.COLUMN_PRIORITY);
        projectionList.add(TaskContract.TaskEntry.COLUMN_COMPLETED);
        
        if (hasCreatedAtColumn) {
            projectionList.add(TaskContract.TaskEntry.COLUMN_CREATED_AT);
        }
        
        String[] projection = projectionList.toArray(new String[0]);

        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        try (Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                task = extractTaskFromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting task by ID: " + e.getMessage());
        }

        return task;
    }

    /**
     * Insert a new task
     */
    public long insertTask(Task task) {
        return addTask(task);
    }

    /**
     * Add a task
     */
    public long addTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.getDueDate());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        
        // Check if column exists in database before adding value
        if (hasColumn(db, TaskContract.TaskEntry.TABLE_NAME, TaskContract.TaskEntry.COLUMN_CREATED_AT)) {
            // Check if createdAt is greater than 0 and use the constant from TaskContract
            if (task.getCreatedAt() > 0) {
                values.put(TaskContract.TaskEntry.COLUMN_CREATED_AT, task.getCreatedAt());
            } else {
                values.put(TaskContract.TaskEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
            }
        }

        return db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
    }

    /**
     * Update an existing task
     * 
     * @param task The task to update
     * @return Number of rows affected (should be 1 if successful)
     */
    public int updateTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.getDueDate());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        
        // Check if column exists before trying to update it
        if (hasColumn(db, TaskContract.TaskEntry.TABLE_NAME, TaskContract.TaskEntry.COLUMN_CREATED_AT)) {
            values.put(TaskContract.TaskEntry.COLUMN_CREATED_AT, task.getCreatedAt());
        }
        
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(task.getId()) };
        
        return db.update(
            TaskContract.TaskEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        );
    }
    
    /**
     * Delete a task by its ID
     * 
     * @param taskId ID of the task to delete
     * @return Number of rows affected (should be 1 if successful)
     */
    public int deleteTask(long taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        
        return db.delete(
            TaskContract.TaskEntry.TABLE_NAME,
            selection,
            selectionArgs
        );
    }
    
    /**
     * Get all tasks due between two timestamps
     * 
     * @param startTime Start timestamp (inclusive)
     * @param endTime End timestamp (inclusive)
     * @return List of tasks due between the specified times
     */
    public List<Task> getTasksDueBetween(long startTime, long endTime) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();
        
        // Get the table info to check if created_at column exists
        boolean hasCreatedAtColumn = hasColumn(db, TaskContract.TaskEntry.TABLE_NAME, 
                                              TaskContract.TaskEntry.COLUMN_CREATED_AT);
        
        // Build projection based on available columns
        List<String> projectionList = new ArrayList<>();
        projectionList.add(TaskContract.TaskEntry._ID);
        projectionList.add(TaskContract.TaskEntry.COLUMN_TITLE);
        projectionList.add(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        projectionList.add(TaskContract.TaskEntry.COLUMN_DUE_DATE);
        projectionList.add(TaskContract.TaskEntry.COLUMN_PRIORITY);
        projectionList.add(TaskContract.TaskEntry.COLUMN_COMPLETED);
        
        if (hasCreatedAtColumn) {
            projectionList.add(TaskContract.TaskEntry.COLUMN_CREATED_AT);
        }
        
        String[] projection = projectionList.toArray(new String[0]);
        
        String selection = TaskContract.TaskEntry.COLUMN_DUE_DATE + " >= ? AND " +
                           TaskContract.TaskEntry.COLUMN_DUE_DATE + " <= ?";
        String[] selectionArgs = { String.valueOf(startTime), String.valueOf(endTime) };
        
        try (Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                TaskContract.TaskEntry.COLUMN_DUE_DATE + " ASC"
        )) {
            while (cursor.moveToNext()) {
                Task task = extractTaskFromCursor(cursor);
                tasks.add(task);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting tasks due between timestamps: " + e.getMessage());
        }
        
        return tasks;
    }
    
    /**
     * Check if a column exists in a database table
     *
     * @param db Database to check
     * @param tableName Table name to check
     * @param columnName Column name to check
     * @return True if column exists, false otherwise
     */
    private boolean hasColumn(SQLiteDatabase db, String tableName, String columnName) {
        boolean columnExists = false;
        
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int nameColumnIndex = cursor.getColumnIndex("name");
                    if (nameColumnIndex != -1) {
                        String name = cursor.getString(nameColumnIndex);
                        if (columnName.equalsIgnoreCase(name)) {
                            columnExists = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking if column exists: " + e.getMessage());
        }
        
        return columnExists;
    }
    
    /**
     * Extract a Task object from a cursor
     *
     * @param cursor Cursor positioned at the task data
     * @return Task object with data from cursor
     */
    private Task extractTaskFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION));
        long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DUE_DATE));
        int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_PRIORITY));
        boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_COMPLETED)) == 1;
        
        // Create Task using default constructor and set properties individually
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setCompleted(completed);
        
        // Check if created_at column exists in the cursor
        int createdAtColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_CREATED_AT);
        if (createdAtColumnIndex != -1) {
            long createdAt = cursor.getLong(createdAtColumnIndex);
            task.setCreatedAt(createdAt);
        }
        
        return task;
    }
}
