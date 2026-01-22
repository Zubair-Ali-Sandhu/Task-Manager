package me.zubair.taskmanager.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * BroadcastReceiver for handling alarm dismissal actions.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    
    // Use the same constant as in NotificationService
    public static final String ACTION_DISMISS_ALARM = "me.zubair.taskmanager.DISMISS_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmReceiver received action: " + (intent != null ? intent.getAction() : "null"));

        // Acquire temporary wake lock to ensure we complete processing
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "TaskManager:AlarmReceiverWakeLock");
        wakeLock.acquire(30*1000); // 30 seconds timeout
        
        try {
            if (intent != null && ACTION_DISMISS_ALARM.equals(intent.getAction())) {
                long taskId = intent.getLongExtra("TASK_ID", -1);

                if (taskId != -1) {
                    Log.d(TAG, "Dismissing alarm for task ID: " + taskId);

                    // Forward the dismiss action to the service
                    Intent serviceIntent = new Intent(context, NotificationService.class);
                    serviceIntent.setAction(ACTION_DISMISS_ALARM);
                    serviceIntent.putExtra("TASK_ID", taskId);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                }
            }
        } finally {
            // Release wake lock
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }
}
