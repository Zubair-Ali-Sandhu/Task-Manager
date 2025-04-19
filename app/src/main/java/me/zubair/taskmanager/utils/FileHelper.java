package me.zubair.taskmanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class FileHelper {
    
    private static final String TAG = "FileHelper";
    
    /**
     * Callback interface for file operations
     */
    public interface FileOperationCallback {
        void onSuccess(String filePath);
        void onError(String errorMessage);
    }
    
    public FileHelper() {
        // Empty constructor
    }
    
    /**
     * Deletes all files associated with a specific task
     * @param context Application context
     * @param taskId Task ID
     * @return true if files were deleted successfully
     */
    public boolean deleteTaskFiles(Context context, long taskId) {
        try {
            File taskDir = new File(context.getFilesDir(), "tasks/" + taskId);
            if (taskDir.exists() && taskDir.isDirectory()) {
                deleteRecursive(taskDir);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting task files: " + e.getMessage());
            return false;
        }
    }
    
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
    
    /**
     * Gets all files associated with a specific task
     * @param context Application context
     * @param taskId Task ID
     * @return Array of files or empty array if no files exist
     */
    public File[] getTaskFiles(Context context, long taskId) {
        File taskDir = new File(context.getFilesDir(), "tasks/" + taskId);
        if (taskDir.exists() && taskDir.isDirectory()) {
            File[] files = taskDir.listFiles();
            return files != null ? files : new File[0];
        }
        return new File[0];
    }
    
    /**
     * Saves a file selected by the user to the task's directory
     * @param context Application context
     * @param uri URI of the file to save
     * @param taskId Task ID
     * @param callback Callback to handle success/failure
     */
    public void saveFile(Context context, Uri uri, long taskId, FileOperationCallback callback) {
        try {
            // Create task directory if it doesn't exist
            File taskDir = new File(context.getFilesDir(), "tasks/" + taskId);
            if (!taskDir.exists()) {
                taskDir.mkdirs();
            }
            
            // Get file name from URI
            String fileName = getFileNameFromUri(context, uri);
            if (fileName == null) {
                fileName = "file_" + System.currentTimeMillis();
            }
            
            // Create file in task directory
            File destinationFile = new File(taskDir, fileName);
            
            // Copy content from URI to file
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
                
                if (inputStream == null) {
                    callback.onError("Cannot read the selected file");
                    return;
                }
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                callback.onSuccess(destinationFile.getAbsolutePath());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving file: " + e.getMessage());
            callback.onError("Error saving file: " + e.getMessage());
        }
    }
    
    /**
     * Gets file name from URI
     * @param context Application context
     * @param uri URI of the file
     * @return File name or null if it couldn't be determined
     */
    private String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex("_display_name");
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
