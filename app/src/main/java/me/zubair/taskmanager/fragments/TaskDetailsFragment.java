package me.zubair.taskmanager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;
import me.zubair.taskmanager.utils.FileHelper;

/**
 * Fragment for displaying task details
 */
public class TaskDetailsFragment extends Fragment {

    private static final String TAG = "TaskDetailsFragment";
    private static final int REQUEST_CODE_PICK_FILE = 1001;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDueDate;
    private TextView tvPriority;
    private TextView tvStatus;
    private Button btnMarkComplete;
    private Button btnEditTask;

    private TaskRepository taskRepository;
    private Task task;
    private long taskId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        // Initialize views
        tvTitle = view.findViewById(R.id.tv_task_title);
        tvDescription = view.findViewById(R.id.tv_task_description);
        tvDueDate = view.findViewById(R.id.tv_due_date);
        tvPriority = view.findViewById(R.id.tv_priority);
        tvStatus = view.findViewById(R.id.tv_status);
        btnMarkComplete = view.findViewById(R.id.btn_mark_complete);
        btnEditTask = view.findViewById(R.id.btn_edit_task);

        // Initialize repository and helper
        taskRepository = new TaskRepository(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get task ID from arguments
        if (getArguments() != null && getArguments().containsKey("TASK_ID")) {
            taskId = getArguments().getLong("TASK_ID");
            loadTask();
        }

        // Set up button click listeners
        btnMarkComplete.setOnClickListener(v -> toggleTaskCompletion());
        btnEditTask.setOnClickListener(v -> navigateToEditTask());

        // Add proper back button handling
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    navigateBackToTaskList();
                }
            }
        );
    }

    /**
     * Navigate back to the task list fragment
     */
    private void navigateBackToTaskList() {
        // Pop the fragment from the back stack to return to the previous fragment
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * Load task details from the database
     */
    private void loadTask() {
        task = taskRepository.getTaskById(taskId);
        if (task != null) {
            displayTaskDetails();
        } else {
            Toast.makeText(requireContext(), "Task not found", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Display task details in the UI
     */
    private void displayTaskDetails() {
        // Set title with null check
        if (task.getTitle() != null) {
            tvTitle.setText(task.getTitle());
        } else {
            tvTitle.setText("Untitled Task");
        }

        // Handle description
        String description = task.getDescription();
        if (description != null && !description.trim().isEmpty()) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setText("No description");
            tvDescription.setVisibility(View.VISIBLE);
        }

        // Format and display due date with validation
        if (task.getDueDate() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm", Locale.getDefault());
            String dueDateText = sdf.format(new Date(task.getDueDate()));
            tvDueDate.setText(dueDateText);
            tvDueDate.setVisibility(View.VISIBLE);
        } else {
            tvDueDate.setText("No due date set");
            tvDueDate.setVisibility(View.VISIBLE);
        }

        // Display priority
        String priorityText;
        switch (task.getPriority()) {
            case Task.PRIORITY_HIGH:
                priorityText = "High";
                break;
            case Task.PRIORITY_MEDIUM:
                priorityText = "Medium";
                break;
            case Task.PRIORITY_LOW:
            default:
                priorityText = "Low";
                break;
        }
        tvPriority.setText(priorityText);

        // Display status
        if (task.isCompleted()) {
            tvStatus.setText("Completed");
            btnMarkComplete.setText("Mark Incomplete");
        } else {
            tvStatus.setText("Pending");
            btnMarkComplete.setText("Mark Complete");
        }
    }

    /**
     * Toggle task completion status
     */
    private void toggleTaskCompletion() {
        task.setCompleted(!task.isCompleted());
        int rowsAffected = taskRepository.updateTask(task);

        if (rowsAffected > 0) {
            displayTaskDetails();
            String message = task.isCompleted() ? "Task marked as complete" : "Task marked as incomplete";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate to the edit task fragment
     */
    private void navigateToEditTask() {
        EditTaskFragment editFragment = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putLong("TASK_ID", taskId);
        editFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }
}
