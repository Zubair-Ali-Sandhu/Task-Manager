package me.zubair.taskmanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;

public class TaskDialogFragment extends DialogFragment {

    private TaskRepository taskRepository;
    private EditText etTitle;
    private EditText etDescription;
    private Button btnSelectDate;
    private Button btnSelectTime;
    private RadioGroup rgPriority;
    private Calendar calendar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        taskRepository = new TaskRepository(requireContext());
        calendar = Calendar.getInstance();
        // Set default to 1 day from now
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        
        // Initialize views
        etTitle = dialogView.findViewById(R.id.et_task_title);
        etDescription = dialogView.findViewById(R.id.et_task_description);
        btnSelectDate = dialogView.findViewById(R.id.btn_pick_date);
        btnSelectTime = dialogView.findViewById(R.id.btn_pick_time);
        rgPriority = dialogView.findViewById(R.id.rg_priority);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        
        // Set default priority to Low
        ((RadioButton) dialogView.findViewById(R.id.rb_low)).setChecked(true);

        // Update date/time buttons with initial values
        updateDateTimeButtons();

        builder.setView(dialogView).setTitle("Add New Task");
        
        AlertDialog dialog = builder.create();
        
        // Set up date picker button
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Set up time picker button
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            if (title.isEmpty()) {
                etTitle.setError("Title cannot be empty");
                etTitle.requestFocus();
                return;
            }
            
            // Get selected priority
            int priority;
            int selectedId = rgPriority.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_high) {
                priority = Task.PRIORITY_HIGH;
            } else if (selectedId == R.id.rb_medium) {
                priority = Task.PRIORITY_MEDIUM;
            } else {
                priority = Task.PRIORITY_LOW;
            }

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
                Toast.makeText(getContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // Refresh the task list
                if (getParentFragment() instanceof TaskListFragment) {
                    ((TaskListFragment) getParentFragment()).onResume();
                }
            } else {
                Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        return dialog;
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

        // Don't allow selecting past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
                    calendar.set(Calendar.SECOND, 0);
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
}
