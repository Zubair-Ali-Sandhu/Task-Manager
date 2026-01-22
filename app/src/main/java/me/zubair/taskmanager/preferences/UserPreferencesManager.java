package me.zubair.taskmanager.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manager class for user preferences
 */
public class UserPreferencesManager {

    private static final String PREF_NAME = "task_manager_preferences";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";

    private final SharedPreferences sharedPreferences;

    public UserPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Check if notifications are enabled
     * @return true if notifications are enabled, false otherwise
     */
    public boolean areNotificationsEnabled() {
        // By default, notifications are enabled
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    /**
     * Set whether notifications should be enabled
     * @param enabled true to enable notifications, false to disable
     */
    public void setNotificationsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled);
        editor.apply();
    }

    /**
     * Clear all user preferences
     */
    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}