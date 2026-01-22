package me.zubair.taskmanager.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;

public class EditTaskFragment extends Fragment {

    private TextView tvFragmentTitle;
    private EditText etTitle;
    private EditText etDescription;
    private Button btnSelectDate;
    private Button btnSelectTime;
    private RadioGroup rgPriority;
    private Button btnUpdateTask;
    private Button btnCancel;

    private TaskRepository taskRepository;
    private Calendar calendar;
    private Task currentTask;
    private long taskId = -1; // -1 indicates new task (add mode)
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        // Initialize views
        tvFragmentTitle = view.findViewById(R.id.tv_fragment_title);
        etTitle = view.findViewById(R.id.et_task_title);
        etDescription = view.findViewById(R.id.et_task_description);
        btnSelectDate = view.findViewById(R.id.btn_pick_date);
        btnSelectTime = view.findViewById(R.id.btn_pick_time);
        rgPriority = view.findViewById(R.id.rg_priority);
        btnUpdateTask = view.findViewById(R.id.btn_save_task);
        btnCancel = view.findViewById(R.id.btn_cancel_edit);

        // Initialize other components
        taskRepository = new TaskRepository(requireContext());
        calendar = Calendar.getInstance();

        // Check if we're in edit mode or add mode
        if (getArguments() != null && getArguments().containsKey("TASK_ID")) {
            taskId = getArguments().getLong("TASK_ID");
            isEditMode = true;
            tvFragmentTitle.setText(R.string.edit_task);
            btnUpdateTask.setText(R.string.update_task);
        } else {
            // Add mode - set default date to 1 day from now
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            tvFragmentTitle.setText("Add Task");
            btnUpdateTask.setText("Save");
            isEditMode = false;
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load task if in edit mode
        if (isEditMode) {
            loadTask();
        } else {
            // Add mode - set default priority and update date/time buttons
            ((RadioButton) rgPriority.findViewById(R.id.rb_low)).setChecked(true);
            updateDateTimeButtons();
        }

        // Set up button click listeners
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnUpdateTask.setOnClickListener(v -> saveOrUpdateTask());
        btnCancel.setOnClickListener(v -> navigateBack());
    }

    private void loadTask() {
        currentTask = taskRepository.getTaskById(taskId);
        if (currentTask != null) {
            populateTaskData();
        } else {
            Toast.makeText(requireContext(), R.string.task_not_found, Toast.LENGTH_SHORT).show();
            navigateBack();
        }
    }

    private void populateTaskData() {
        // Set task data to views
        etTitle.setText(currentTask.getTitle());
        etDescription.setText(currentTask.getDescription());
        
        // Set the calendar to the task's due date
        if (currentTask.getDueDate() > 0) {
            calendar.setTimeInMillis(currentTask.getDueDate());
        } else {
            // Set to current time if no due date
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
        updateDateTimeButtons();
        
        // Set priority using Task constants
        switch (currentTask.getPriority()) {
            case Task.PRIORITY_HIGH:
                ((RadioButton) rgPriority.findViewById(R.id.rb_high)).setChecked(true);
                break;
            case Task.PRIORITY_MEDIUM:
                ((RadioButton) rgPriority.findViewById(R.id.rb_medium)).setChecked(true);
                break;
            case Task.PRIORITY_LOW:
            default:
                ((RadioButton) rgPriority.findViewById(R.id.rb_low)).setChecked(true);
                break;
        }
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateTimeButtons();
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    updateDateTimeButtons();
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    private void updateDateTimeButtons() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = calendar.getTime();

        btnSelectDate.setText(dateFormat.format(date));
        btnSelectTime.setText(timeFormat.format(date));
    }

    private void saveOrUpdateTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        
        // Validate input
        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.title_required));
            etTitle.requestFocus();
            return;
        }

        // Get selected priority using Task constants
        int priority;
        int selectedId = rgPriority.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_high) {
            priority = Task.PRIORITY_HIGH;
        } else if (selectedId == R.id.rb_medium) {
            priority = Task.PRIORITY_MEDIUM;
        } else {
            priority = Task.PRIORITY_LOW;
        }

        if (isEditMode) {
            // Update existing task
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setDueDate(calendar.getTimeInMillis());
            currentTask.setPriority(priority);

            // Save to database
            int rowsAffected = taskRepository.updateTask(currentTask);
            if (rowsAffected > 0) {
                Toast.makeText(requireContext(), R.string.task_updated_success, Toast.LENGTH_SHORT).show();
                navigateToTaskDetails();
            } else {
                Toast.makeText(requireContext(), R.string.failed_update_task, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new task
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDueDate(calendar.getTimeInMillis());
            newTask.setPriority(priority);
            newTask.setCreatedAt(System.currentTimeMillis());
            newTask.setCompleted(false);

            long result = taskRepository.insertTask(newTask);
            if (result > 0) {
                Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
                navigateBackToTaskList();
            } else {
                Toast.makeText(requireContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToTaskDetails() {
        // Navigate back to task details with the task ID
        TaskDetailsFragment detailsFragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("TASK_ID", taskId);
        detailsFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .commit();
    }

    private void navigateBackToTaskList() {
        // Navigate back to task list (clear back stack)
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
