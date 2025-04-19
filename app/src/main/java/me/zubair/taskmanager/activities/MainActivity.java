package me.zubair.taskmanager.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.fragments.HomeFragment;
import me.zubair.taskmanager.fragments.SettingsFragment;
import me.zubair.taskmanager.fragments.TaskListFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        
        // Set up the navigation listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_tasks) {
                    selectedFragment = new TaskListFragment();
                } else if (itemId == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                
                return false;
            }
        });

        // Check if this is initial startup
        if (savedInstanceState == null) {
            // Load HomeFragment as the default fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            
            // Set home as selected in bottom navigation
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        // Check if there are fragments in the back stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Pop the fragment from the back stack
            fragmentManager.popBackStack();
            
            // When returning from TaskDetailsFragment, make sure Tasks tab is selected
            bottomNavigationView.setSelectedItemId(R.id.nav_tasks);
            return;
        }

        // If no fragments in back stack, handle bottom navigation
        int selectedItemId = bottomNavigationView.getSelectedItemId();

        // If we're not on the home tab, go back to home
        if (selectedItemId != R.id.nav_home) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            // If we're already on home, exit the app
            super.onBackPressed();
        }
    }
}
