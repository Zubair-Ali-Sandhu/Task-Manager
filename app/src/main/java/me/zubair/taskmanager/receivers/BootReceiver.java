package me.zubair.taskmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.zubair.taskmanager.preferences.UserPreferencesManager;
import me.zubair.taskmanager.services.NotificationService;

/**
 * Receiver for starting services when device boots
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            UserPreferencesManager preferencesManager = new UserPreferencesManager(context);

            // Start notification service if enabled
            if (preferencesManager.areNotificationsEnabled()) {
                Intent serviceIntent = new Intent(context, NotificationService.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}