package me.zubair.taskmanager.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;

public class HomeFragment extends Fragment {

    private TextView tvCurrentDate;
    private TextView tvCurrentTime;
    private TextView tvQuote;
    private TextView tvQuoteAuthor;
    private TextView tvTaskCount;
    private Button btnViewTasks;
    private TaskRepository taskRepository;
    private Handler timeHandler;
    private Runnable timeRunnable;

    private final String[][] quotes = {
            {"The secret of getting ahead is getting started.", "Mark Twain"},
            {"Don't watch the clock; do what it does. Keep going.", "Sam Levenson"},
            {"The way to get started is to quit talking and begin doing.", "Walt Disney"},
            {"The future depends on what you do today.", "Mahatma Gandhi"},
            {"You don't have to see the whole staircase, just take the first step.", "Martin Luther King, Jr."},
            {"It always seems impossible until it's done.", "Nelson Mandela"}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        tvCurrentDate = view.findViewById(R.id.text_view_current_date);
        tvCurrentTime = view.findViewById(R.id.text_view_current_time);
        tvQuote = view.findViewById(R.id.text_view_quote);
        tvQuoteAuthor = view.findViewById(R.id.text_view_quote_author);
        tvTaskCount = view.findViewById(R.id.text_view_task_count);
        btnViewTasks = view.findViewById(R.id.button_view_tasks);

        // Initialize repository
        taskRepository = new TaskRepository(requireContext());

        // Set random motivational quote
        setRandomQuote();

        // Set task count
        updateTaskCount();

        // Setup clock updates
        setupClock();

        // Set button click listener
        btnViewTasks.setOnClickListener(v -> navigateToTaskList());

        return view;
    }

    private void setupClock() {
        timeHandler = new Handler(Looper.getMainLooper());
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                timeHandler.postDelayed(this, 1000);
            }
        };
    }

    private void updateDateTime() {
        Date currentDate = new Date();
        
        // Format and set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        tvCurrentDate.setText(formattedDate);
        
        // Format and set current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String formattedTime = timeFormat.format(currentDate);
        tvCurrentTime.setText(formattedTime);
    }

    private void setRandomQuote() {
        int randomIndex = new Random().nextInt(quotes.length);
        tvQuote.setText(quotes[randomIndex][0]);
        tvQuoteAuthor.setText("- " + quotes[randomIndex][1]);
    }

    private void updateTaskCount() {
        List<Task> tasks = taskRepository.getAllTasks();
        int pendingTasks = 0;
        
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                pendingTasks++;
            }
        }
        
        String taskText = pendingTasks == 1 
            ? "You have 1 pending task" 
            : "You have " + pendingTasks + " pending tasks";
        
        tvTaskCount.setText(taskText);
    }

    private void navigateToTaskList() {
        TaskListFragment taskListFragment = new TaskListFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, taskListFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTaskCount();
        updateDateTime();
        timeHandler.post(timeRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(timeRunnable);
    }
}
