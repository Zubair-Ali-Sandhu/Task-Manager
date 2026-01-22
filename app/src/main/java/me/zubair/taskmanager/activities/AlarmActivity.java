package me.zubair.taskmanager.activities;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.services.AlarmReceiver;
import me.zubair.taskmanager.services.NotificationService;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";
    private MediaPlayer mediaPlayer;
    private long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Keep screen on and show above lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            // Use deprecated flags for older Android versions
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        // Get task information from intent
        taskId = getIntent().getLongExtra("TASK_ID", -1);
        String title = getIntent().getStringExtra("TASK_TITLE");
        String description = getIntent().getStringExtra("TASK_DESCRIPTION");

        Log.d(TAG, "AlarmActivity created for task: " + taskId + ", title: " + title);

        // Set task information in UI
        TextView titleTextView = findViewById(R.id.textViewAlarmTitle);
        TextView descriptionTextView = findViewById(R.id.textViewAlarmDescription);

        if (titleTextView != null && title != null) {
            titleTextView.setText(title);
        }

        if (descriptionTextView != null && description != null) {
            descriptionTextView.setText(description);
        }

        // Set up dismiss button
        Button dismissButton = findViewById(R.id.buttonDismissAlarm);
        if (dismissButton != null) {
            dismissButton.setOnClickListener(v -> dismissAlarm());
        }

        // Play alarm sound
        playAlarmSound();
    }

    private void playAlarmSound() {
        try {
            // Set volume to maximum
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioManager != null) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            }

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            mediaPlayer = MediaPlayer.create(this, alarmSound);
            
            if (mediaPlayer != null) {
                // Set audio attributes for alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
                    mediaPlayer.setAudioAttributes(attributes);
                } else {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                }
                
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                Log.d(TAG, "Alarm sound started in AlarmActivity");
            } else {
                Log.e(TAG, "MediaPlayer could not be created");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing alarm sound", e);
        }
    }

    private void dismissAlarm() {
        // Stop the alarm sound
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Send broadcast to cancel notification
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(NotificationService.ACTION_DISMISS_ALARM);
        intent.putExtra("TASK_ID", taskId);
        sendBroadcast(intent);
        
        Log.d(TAG, "Alarm dismissed for task: " + taskId);

        // Close the activity
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
