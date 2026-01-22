package me.zubair.taskmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.zubair.taskmanager.R;
import me.zubair.taskmanager.database.TaskRepository;
import me.zubair.taskmanager.models.Task;

/**
 * Adapter for displaying tasks in a RecyclerView
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final Context context;
    private final OnTaskClickListener listener;
    private final TaskRepository taskRepository;

    // Interface for handling item clicks
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskLongClick(Task task, View itemView);
    }

    public TaskAdapter(Context context, List<Task> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList != null ? new ArrayList<>(taskList) : new ArrayList<>();
        this.listener = listener;
        this.taskRepository = new TaskRepository(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        final List<Task> oldList = new ArrayList<>(this.taskList);
        final List<Task> newList = new ArrayList<>(newTasks);
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Task oldTask = oldList.get(oldItemPosition);
                Task newTask = newList.get(newItemPosition);
                
                // Safe null-aware comparisons
                boolean titleEquals = (oldTask.getTitle() == null && newTask.getTitle() == null) ||
                                     (oldTask.getTitle() != null && oldTask.getTitle().equals(newTask.getTitle()));

                boolean descEquals = (oldTask.getDescription() == null && newTask.getDescription() == null) ||
                                    (oldTask.getDescription() != null && oldTask.getDescription().equals(newTask.getDescription()));

                return titleEquals &&
                       descEquals &&
                       oldTask.getDueDate() == newTask.getDueDate() &&
                       oldTask.getPriority() == newTask.getPriority() &&
                       oldTask.isCompleted() == newTask.isCompleted();
            }
        });
        
        this.taskList = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView dueDateTextView;
        private final CheckBox completedCheckBox;
        private final CardView taskCardView;
        private final ImageView priorityIndicator;
        private final View priorityBar;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_task_title);
            descriptionTextView = itemView.findViewById(R.id.text_view_task_description);
            dueDateTextView = itemView.findViewById(R.id.text_view_task_due_date);
            completedCheckBox = itemView.findViewById(R.id.checkbox_task_completed);
            taskCardView = itemView.findViewById(R.id.card_view_task);
            priorityIndicator = itemView.findViewById(R.id.image_view_priority_indicator);
            priorityBar = itemView.findViewById(R.id.view_priority_bar);
        }

        public void bind(final Task task) {
            // Set title with null check
            if (task.getTitle() != null && !task.getTitle().trim().isEmpty()) {
                titleTextView.setText(task.getTitle());
            } else {
                titleTextView.setText("Untitled Task");
            }

            // Set description with null check
            if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
                descriptionTextView.setText(task.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            // Format and set due date if available
            if (task.getDueDate() != 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                String formattedDate = sdf.format(new Date(task.getDueDate()));
                dueDateTextView.setText("Due: " + formattedDate);
                dueDateTextView.setVisibility(View.VISIBLE);
            } else {
                dueDateTextView.setVisibility(View.GONE);
            }

            // Set completion status
            completedCheckBox.setChecked(task.isCompleted());
            
            // Set card background based on completion status
            if (task.isCompleted()) {
                taskCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.task_completed_background));
                titleTextView.setAlpha(0.6f);
                descriptionTextView.setAlpha(0.6f);
                dueDateTextView.setAlpha(0.6f);
            } else {
                taskCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.task_card_background));
                titleTextView.setAlpha(1.0f);
                descriptionTextView.setAlpha(1.0f);
                dueDateTextView.setAlpha(1.0f);
            }
            
            // Set priority indicator
            setPriorityIndicator(task.getPriority(), priorityIndicator);

            // Setup listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onTaskLongClick(task, itemView);
                    return true;
                }
                return false;
            });
            
            completedCheckBox.setOnClickListener(v -> {
                boolean isChecked = completedCheckBox.isChecked();
                task.setCompleted(isChecked);
                taskRepository.updateTask(task);
                
                // Refresh the card appearance
                if (isChecked) {
                    taskCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.task_completed_background));
                    titleTextView.setAlpha(0.6f);
                    descriptionTextView.setAlpha(0.6f);
                    dueDateTextView.setAlpha(0.6f);
                } else {
                    taskCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.task_card_background));
                    titleTextView.setAlpha(1.0f);
                    descriptionTextView.setAlpha(1.0f);
                    dueDateTextView.setAlpha(1.0f);
                }
            });
        }
        
        private void setPriorityIndicator(int priority, ImageView indicator) {
            indicator.setVisibility(View.VISIBLE);
            int color;
            switch (priority) {
                case Task.PRIORITY_HIGH:
                    color = ContextCompat.getColor(context, R.color.priority_high);
                    break;
                case Task.PRIORITY_MEDIUM:
                    color = ContextCompat.getColor(context, R.color.priority_medium);
                    break;
                case Task.PRIORITY_LOW:
                    color = ContextCompat.getColor(context, R.color.priority_low);
                    break;
                default:
                    indicator.setVisibility(View.GONE);
                    if (priorityBar != null) {
                        priorityBar.setVisibility(View.GONE);
                    }
                    return;
            }

            // Set both indicator icon and priority bar color
            indicator.setColorFilter(color);
            if (priorityBar != null) {
                priorityBar.setBackgroundColor(color);
                priorityBar.setVisibility(View.VISIBLE);
            }
        }
    }
}
