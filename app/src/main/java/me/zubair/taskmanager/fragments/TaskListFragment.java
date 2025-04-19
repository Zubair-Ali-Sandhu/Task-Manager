package me.zubair.taskmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.adapters.TaskAdapter;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;
import me.zubair.taskmanager.utils.FileHelper;

public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TaskRepository taskRepository;
    private FileHelper fileHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Initialize components
        recyclerView = view.findViewById(R.id.rv_tasks); // This ID should match the XML
        FloatingActionButton fabAddTask = view.findViewById(R.id.fab_add_task);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskRepository = new TaskRepository(requireContext());
        fileHelper = new FileHelper(); // Fixed constructor call
        loadTasks();

        // Set up FAB click listener
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the task list when returning to this fragment
        loadTasks();
    }

    private void loadTasks() {
        List<Task> tasks = taskRepository.getAllTasks();
        taskAdapter = new TaskAdapter(requireContext(), tasks, this);
        recyclerView.setAdapter(taskAdapter);
    }

    private void showAddTaskDialog() {
        // Using TaskDialogFragment instead of AddTaskDialogFragment
        TaskDialogFragment dialog = new TaskDialogFragment();
        dialog.show(getChildFragmentManager(), "AddTaskDialog");
    }

    @Override
    public void onTaskClick(Task task) {
        // Navigate to task details fragment
        TaskDetailsFragment detailsFragment = new TaskDetailsFragment();
        
        // Pass the task ID to the details fragment
        Bundle args = new Bundle();
        args.putLong("TASK_ID", task.getId());
        detailsFragment.setArguments(args);
        
        // Replace the current fragment with the details fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onTaskLongClick(Task task, View view) {
        showTaskOptionsMenu(task, view);
    }

    private void showTaskOptionsMenu(final Task task, View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.inflate(R.menu.menu_task_options);
        
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                navigateToEditTask(task.getId());
                return true;
            } else if (itemId == R.id.action_delete) {
                deleteTask(task);
                return true;
            }
            return false;
        });
        
        popup.show();
    }

    private void navigateToEditTask(long taskId) {
        EditTaskFragment editFragment = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putLong("TASK_ID", taskId);
        editFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteTask(Task task) {
        try {
            // Delete associated files first
            if (fileHelper != null) {
                fileHelper.deleteTaskFiles(requireContext(), task.getId()); // Fixed method call
            }
            
            // Then delete the task from database
            int result = taskRepository.deleteTask(task.getId());
            if (result > 0) {
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                loadTasks(); // Refresh the list
            } else {
                Toast.makeText(requireContext(), "Error deleting task", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
