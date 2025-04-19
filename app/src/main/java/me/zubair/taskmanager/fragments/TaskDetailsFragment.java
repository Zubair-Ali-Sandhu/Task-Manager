package me.zubair.taskmanager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    private static final int REQUEST_CODE_PICK_FILE = 1001;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvDueDate;
    private TextView tvPriority;
    private TextView tvStatus;
    private Button btnMarkComplete;
    private Button btnEditTask;
    private Button btnAttachFile;
    private Button btnViewFiles;

    private TaskRepository taskRepository;
    private FileHelper fileHelper;
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
        btnAttachFile = view.findViewById(R.id.btn_attach_file);
        btnViewFiles = view.findViewById(R.id.btn_view_files);

        // Initialize repository and helper
        taskRepository = new TaskRepository(requireContext());
        fileHelper = new FileHelper();

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
        btnAttachFile.setOnClickListener(v -> pickFile());
        btnViewFiles.setOnClickListener(v -> viewAttachedFiles());

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
        tvTitle.setText(task.getTitle());

        // Handle description
        String description = task.getDescription();
        if (description != null && !description.isEmpty()) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        // Format and display due date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm", Locale.getDefault());
        String dueDateText = sdf.format(new Date(task.getDueDate()));
        tvDueDate.setText(dueDateText);

        // Display priority
        String priorityText;
        switch (task.getPriority()) {
            case 3:
                priorityText = "High";
                break;
            case 2:
                priorityText = "Medium";
                break;
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

        // Check for attached files
        File[] attachedFiles = fileHelper.getTaskFiles(requireContext(), taskId);
        btnViewFiles.setEnabled(attachedFiles.length > 0);
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

    /**
     * Open file picker to attach a file to the task
     */
    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    /**
     * View files attached to the task
     */
    private void viewAttachedFiles() {
        File[] files = fileHelper.getTaskFiles(requireContext(), taskId);

        if (files.length == 0) {
            Toast.makeText(requireContext(), "No files attached", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a dialog to display file list
        CharSequence[] fileNames = new CharSequence[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Attached Files")
                .setItems(fileNames, (dialog, which) -> {
                    openFile(files[which]);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Open a file using an appropriate app
     * @param file The file to open
     */
    private void openFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
        );

        // Try to determine MIME type
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String mimeType;

        switch (extension) {
            case "pdf":
                mimeType = "application/pdf";
                break;
            case "jpg":
            case "jpeg":
            case "png":
                mimeType = "image/*";
                break;
            case "mp4":
                mimeType = "video/*";
                break;
            case "txt":
                mimeType = "text/plain";
                break;
            default:
                mimeType = "*/*";
                break;
        }

        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No app available to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                // Take persistent permissions on the URI if needed
                if (uri.toString().startsWith("content://")) {
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    requireActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                }
                
                // Save the file
                fileHelper.saveFile(requireContext(), uri, taskId, new FileHelper.FileOperationCallback() {
                    @Override
                    public void onSuccess(String filePath) {
                        Toast.makeText(requireContext(), "File attached successfully", Toast.LENGTH_SHORT).show();

                        // Update UI
                        btnViewFiles.setEnabled(true);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
