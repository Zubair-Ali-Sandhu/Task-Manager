package me.zubair.taskmanager.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.activities.MainActivity;
import me.zubair.taskmanager.activities.AlarmActivity;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "TaskManagerChannel";
    private static final String ALARM_CHANNEL_ID = "TaskAlarmChannel";
    private static final int NOTIFICATION_ID = 1;
    public static final String ACTION_CHECK_TASKS = "me.zubair.taskmanager.CHECK_TASKS";
    public static final String ACTION_DISMISS_ALARM = "me.zubair.taskmanager.DISMISS_ALARM";

    private ScheduledExecutorService scheduler;
    private TaskRepository taskRepository;
    private Map<Long, MediaPlayer> activeAlarms = new HashMap<>();
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service created");
        createNotificationChannels();
        taskRepository = new TaskRepository(this);

        // Acquire wake lock to keep CPU running for alarms
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskManager:AlarmWakeLock");
        }

        // Start as foreground service
        startForeground(NOTIFICATION_ID, createServiceNotification());

        scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started with intent: " + (intent != null ? intent.getAction() : "null"));
        
        // Even if no intent, start checking tasks
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
        }
        
        if (intent != null && ACTION_DISMISS_ALARM.equals(intent.getAction())) {
            long taskId = intent.getLongExtra("TASK_ID", -1);
            if (taskId != -1) {
                Log.d(TAG, "Dismissing alarm for task: " + taskId);
                stopAlarmSound(taskId);

                // Cancel the notification
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancel((int) taskId + 100);
                }
            }
        } else if (intent != null && ACTION_CHECK_TASKS.equals(intent.getAction())) {
            // Force check tasks immediately when receiving check action
            scheduler.submit(this::checkUpcomingTasks);
            scheduler.submit(this::checkDueTasks);  // Check for tasks that are due right now
        } else {
            // Schedule immediate check and periodic checks
            scheduler.submit(this::checkUpcomingTasks);
            scheduler.submit(this::checkDueTasks);  // Also check for already due tasks
            scheduler.scheduleAtFixedRate(this::checkUpcomingTasks, 1, 1, TimeUnit.MINUTES);  // Check more frequently
            scheduler.scheduleAtFixedRate(this::checkDueTasks, 1, 1, TimeUnit.MINUTES);  // Also periodically check due tasks
            scheduleAlarm();
        }

        return START_STICKY;
    }

    // Check for tasks due within 30 minutes
    private void checkUpcomingTasks() {
        Log.d(TAG, "Checking upcoming tasks");
        long currentTime = System.currentTimeMillis();
        long thirtyMinutesLater = currentTime + (30 * 60 * 1000);
        
        try {
            for (Task task : taskRepository.getTasksDueBetween(currentTime, thirtyMinutesLater)) {
                if (!task.isCompleted()) {
                    Log.d(TAG, "Found upcoming task: " + task.getTitle() + " due at: " + task.getDueDate());
                    // Instead of sending full alarm, send a reminder notification for upcoming tasks
                    sendUpcomingTaskNotification(task);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking upcoming tasks", e);
        }
    }
    
    // New method: Check for tasks that are already due or overdue
    private void checkDueTasks() {
        Log.d(TAG, "Checking due/overdue tasks");
        long currentTime = System.currentTimeMillis();
        
        try {
            // Get tasks that are due now or in the past (up to 1 hour ago to catch any missed alarms)
            long oneHourAgo = currentTime - (60 * 60 * 1000);
            for (Task task : taskRepository.getTasksDueBetween(oneHourAgo, currentTime)) {
                if (!task.isCompleted()) {
                    Log.d(TAG, "Found due task: " + task.getTitle() + " that was due at: " + task.getDueDate());
                    // This is a task that's actually due now, send full alarm
                    sendTaskAlarmNotification(task);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking due tasks", e);
        }
    }
    
    // New method: Create a more subtle notification for upcoming tasks
    private void sendUpcomingTaskNotification(Task task) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, (int)task.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Calculate minutes until due
        long minutesUntilDue = (task.getDueDate() - System.currentTimeMillis()) / (60 * 1000);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Upcoming Task: " + task.getTitle())
                .setContentText("Due in about " + minutesUntilDue + " minutes")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(task.getDescription()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
                
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                
        if (notificationManager != null) {
            int notificationId = (int) task.getId() + 200; // Different ID range from alarms
            notificationManager.notify(notificationId, builder.build());
        }
    }

    // Create a persistent alarm notification with sound for a specific task
    private void sendTaskAlarmNotification(Task task) {
        Log.d(TAG, "Sending alarm notification for task: " + task.getTitle());
        
        // Start playing alarm sound
        playAlarmSound(task.getId());

        // Create and show the notification
        Notification notification = createAlarmNotification(task);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            int notificationId = (int) task.getId() + 100;
            Log.d(TAG, "Showing notification with ID: " + notificationId);
            notificationManager.notify(notificationId, notification);
        } else {
            Log.e(TAG, "NotificationManager is null");
        }

        // Show full screen activity (will wake device)
        try {
            Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
            fullScreenIntent.putExtra("TASK_ID", task.getId());
            fullScreenIntent.putExtra("TASK_TITLE", task.getTitle());
            fullScreenIntent.putExtra("TASK_DESCRIPTION", task.getDescription());
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(fullScreenIntent);
            Log.d(TAG, "Started alarm activity for task: " + task.getId());
        } catch (Exception e) {
            Log.e(TAG, "Failed to start alarm activity", e);
        }
    }

    // Play a continuous alarm sound
    private void playAlarmSound(long taskId) {
        if (activeAlarms.containsKey(taskId)) {
            // Alarm already playing for this task
            Log.d(TAG, "Alarm already playing for task: " + taskId);
            return;
        }

        try {
            // Acquire wake lock if not held
            if (wakeLock != null && !wakeLock.isHeld()) {
                wakeLock.acquire(10*60*1000L); // 10 minutes max
                Log.d(TAG, "Wake lock acquired");
            }

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, alarmSound);
            if (mediaPlayer == null) {
                Log.e(TAG, "Failed to create MediaPlayer for alarm sound");
                return;
            }
            
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            activeAlarms.put(taskId, mediaPlayer);
            Log.d(TAG, "Started alarm sound for task: " + taskId);
        } catch (Exception e) {
            Log.e(TAG, "Error playing alarm sound", e);
        }
    }

    // Stop alarm sound for a specific task
    private void stopAlarmSound(long taskId) {
        MediaPlayer mediaPlayer = activeAlarms.get(taskId);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            activeAlarms.remove(taskId);
            Log.d(TAG, "Stopped alarm sound for task: " + taskId);
        }

        // Release wake lock if no more active alarms
        if (activeAlarms.isEmpty() && wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            Log.d(TAG, "Wake lock released");
        }
    }

    // Create an alarm-style notification
    private Notification createAlarmNotification(Task task) {
        // Intent for opening the app
        Intent contentIntent = new Intent(this, MainActivity.class);
        contentIntent.putExtra("TASK_ID", task.getId());
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                this, (int)task.getId(), contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for dismissing the alarm - using standalone AlarmReceiver
        Intent dismissIntent = new Intent(this, AlarmReceiver.class);
        dismissIntent.setAction(ACTION_DISMISS_ALARM);
        dismissIntent.putExtra("TASK_ID", task.getId());
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                this, (int)task.getId() + 1000, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for showing full screen alarm
        Intent fullScreenIntent = new Intent(this, AlarmActivity.class);
        fullScreenIntent.putExtra("TASK_ID", task.getId());
        fullScreenIntent.putExtra("TASK_TITLE", task.getTitle());
        fullScreenIntent.putExtra("TASK_DESCRIPTION", task.getDescription());
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                this, (int)task.getId() + 2000, fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Using system icon to ensure it exists
                .setContentTitle("Task Reminder: " + task.getTitle())
                .setContentText(task.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(task.getDescription()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)  // Make notification persistent
                .setAutoCancel(false)
                .setContentIntent(contentPendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)
                .build();
    }

    // Create a standard notification for the service itself
    private Notification createServiceNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Task Manager")
                .setContentText("Monitoring for upcoming tasks")
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Using system icon to ensure it exists
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    // Create notification channels - one for service, one for alarms
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                // Regular notification channel
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID,
                        "Task Manager Service",
                        NotificationManager.IMPORTANCE_LOW);
                serviceChannel.setDescription("Background task monitoring service");
                notificationManager.createNotificationChannel(serviceChannel);

                // Alarm notification channel with sound and vibration
                NotificationChannel alarmChannel = new NotificationChannel(
                        ALARM_CHANNEL_ID,
                        "Task Alarms",
                        NotificationManager.IMPORTANCE_HIGH);
                alarmChannel.setDescription("High priority alarms for due tasks");
                alarmChannel.enableLights(true);
                alarmChannel.enableVibration(true);
                alarmChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                alarmChannel.setBypassDnd(true); // Bypass Do Not Disturb mode
                alarmChannel.setShowBadge(true);

                // Set sound for the alarm channel
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                alarmChannel.setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                        attributes);

                notificationManager.createNotificationChannel(alarmChannel);
                Log.d(TAG, "Notification channels created");
            }
        }
    }

    // Schedule repeating alarm checks
    private void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction(ACTION_CHECK_TASKS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule more frequent checks (every minute for testing, can be increased later)
        long interval = 60 * 1000; // 1 minute
        long triggerTime = SystemClock.elapsedRealtime() + interval;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    scheduleExactAlarm(alarmManager, triggerTime, pendingIntent);
                    Log.d(TAG, "Scheduled exact alarm");
                    
                    // Schedule repeating alarm every minute
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
                        triggerTime, interval, pendingIntent);
                } else {
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
                        triggerTime, interval, pendingIntent);
                    Log.d(TAG, "Scheduled inexact repeating alarm (Android 12+ without permission)");
                }
            } else {
                scheduleExactAlarm(alarmManager, triggerTime, pendingIntent);
                // Also set repeating alarm as backup
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
                    triggerTime, interval, pendingIntent);
                Log.d(TAG, "Scheduled exact and repeating alarms (pre-Android 12)");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException when scheduling alarm: " + e.getMessage());
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
                triggerTime, interval, pendingIntent);
        }
    }

    private void scheduleExactAlarm(AlarmManager alarmManager, long triggerTime, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Service being destroyed");
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        // Clean up all active alarms
        for (Map.Entry<Long, MediaPlayer> entry : activeAlarms.entrySet()) {
            MediaPlayer player = entry.getValue();
            if (player != null) {
                player.stop();
                player.release();
            }
        }
        activeAlarms.clear();

        // Release wake lock if held
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // BroadcastReceiver for receiving alarm checks
    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "NotificationReceiver: received action: " + (intent != null ? intent.getAction() : "null"));
            if (intent != null && ACTION_CHECK_TASKS.equals(intent.getAction())) {
                Intent serviceIntent = new Intent(context, NotificationService.class);
                serviceIntent.setAction(ACTION_CHECK_TASKS);
                
                // Ensure the service is started
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
                
                // Also directly acquire wake lock to ensure task checking happens
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock tempWakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "TaskManager:TempCheckWakeLock");
                tempWakeLock.acquire(60*1000); // 1 minute max
                
                // Wake lock will be released after service is started or timeout occurs
                new Thread(() -> {
                    try {
                        Thread.sleep(10000); // 10 seconds
                        if (tempWakeLock.isHeld()) {
                            tempWakeLock.release();
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Interrupted while waiting to release wake lock", e);
                    }
                }).start();
            }
        }
    }
}
