package me.zubair.taskmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;

public class TaskDialogFragment extends DialogFragment {

    private TaskRepository taskRepository;
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        taskRepository = new TaskRepository(requireContext());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        
        EditText etTitle = dialogView.findViewById(R.id.et_task_title);
        EditText etDescription = dialogView.findViewById(R.id.et_task_description);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        
        builder.setView(dialogView).setTitle("Add New Task");
        
        AlertDialog dialog = builder.create();
        
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setCreatedAt(System.currentTimeMillis());
            
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
}
