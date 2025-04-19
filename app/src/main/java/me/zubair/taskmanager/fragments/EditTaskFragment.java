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
    private long taskId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        // Initialize views
        etTitle = view.findViewById(R.id.et_task_title);
        etDescription = view.findViewById(R.id.et_task_description);
        btnSelectDate = view.findViewById(R.id.btn_pick_date);
        btnSelectTime = view.findViewById(R.id.btn_pick_time);
        rgPriority = view.findViewById(R.id.rg_priority);
        btnUpdateTask = view.findViewById(R.id.btn_save_task);
        btnCancel = view.findViewById(R.id.btn_cancel_edit);

        // Update button text for edit mode
        btnUpdateTask.setText(R.string.update_task);

        // Initialize other components
        taskRepository = new TaskRepository(requireContext());
        calendar = Calendar.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get task ID from arguments
        if (getArguments() != null && getArguments().containsKey("TASK_ID")) {
            taskId = getArguments().getLong("TASK_ID");
            loadTask();
        } else {
            Toast.makeText(requireContext(), R.string.error_task_not_found, Toast.LENGTH_SHORT).show();
            navigateBack();
        }

        // Set up button click listeners
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnUpdateTask.setOnClickListener(v -> updateTask());
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
        calendar.setTimeInMillis(currentTask.getDueDate());
        updateDateTimeButtons();
        
        // Set priority
        switch (currentTask.getPriority()) {
            case 3:
                ((RadioButton) rgPriority.findViewById(R.id.rb_high)).setChecked(true);
                break;
            case 2:
                ((RadioButton) rgPriority.findViewById(R.id.rb_medium)).setChecked(true);
                break;
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

    private void updateTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        
        // Validate input
        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.title_required));
            etTitle.requestFocus();
            return;
        }

        // Get selected priority
        int priority;
        int selectedId = rgPriority.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_high) {
            priority = 3;
        } else if (selectedId == R.id.rb_medium) {
            priority = 2;
        } else {
            priority = 1;
        }

        // Update task object
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

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
