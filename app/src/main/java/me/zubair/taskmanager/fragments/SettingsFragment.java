package me.zubair.taskmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.preferences.UserPreferencesManager;
import me.zubair.taskmanager.services.NotificationService;

/**
 * Fragment for app settings
 */
public class SettingsFragment extends Fragment {

    private Switch switchNotifications;
    private Button btnClearData;
    private UserPreferencesManager preferencesManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        switchNotifications = view.findViewById(R.id.switch_notifications);
        btnClearData = view.findViewById(R.id.btn_clear_data);

        // Initialize preferences manager
        preferencesManager = new UserPreferencesManager(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set current notification preference
        switchNotifications.setChecked(preferencesManager.areNotificationsEnabled());

        // Handle notification switch changes
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNotificationsEnabled(isChecked);

            if (isChecked) {
                // Start notification service
                Intent serviceIntent = new Intent(requireContext(), NotificationService.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    requireContext().startForegroundService(serviceIntent);
                } else {
                    requireContext().startService(serviceIntent);
                }

                Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Stop notification service
                requireContext().stopService(new Intent(requireContext(), NotificationService.class));

                Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle clear data button click
        btnClearData.setOnClickListener(v -> showClearDataConfirmation());
        
        // Remove back button handling as it's now handled by MainActivity
    }

    /**
     * Show confirmation dialog for clearing app data
     */
    private void showClearDataConfirmation() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Clear App Data")
                .setMessage("This will reset all preferences. Are you sure you want to continue?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    // Clear preferences
                    preferencesManager.clearPreferences();

                    // Reset UI
                    switchNotifications.setChecked(preferencesManager.areNotificationsEnabled());

                    Toast.makeText(requireContext(), "App data cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
