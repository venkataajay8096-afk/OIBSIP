package com.oasis.todoapp.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.oasis.todoapp.R;
import com.oasis.todoapp.model.Task;

import java.util.List;

/**
 * TaskAdapter manages rendering individual Task items in a RecyclerView
 * with completion checkbox toggle, visual strikethrough, status badges, and delete actions.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onTaskStatusChanged(Task task, boolean isCompleted);
        void onTaskDelete(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskActionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox cbTaskCompleted;
        private final TextView tvTaskTitle;
        private final TextView tvTaskNotes;
        private final TextView tvStatusBadge;
        private final ImageButton btnDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTaskCompleted = itemView.findViewById(R.id.cbTaskCompleted);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskNotes = itemView.findViewById(R.id.tvTaskNotes);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }

        public void bind(final Task task, final OnTaskActionListener listener) {
            tvTaskTitle.setText(task.getTaskName());

            // Handle notes field visibility
            if (task.getNotes() != null && !task.getNotes().trim().isEmpty()) {
                tvTaskNotes.setText(task.getNotes().trim());
                tvTaskNotes.setVisibility(View.VISIBLE);
            } else {
                tvTaskNotes.setVisibility(View.GONE);
            }

            // Remove listener before setting checked status to prevent false triggers during scroll
            cbTaskCompleted.setOnCheckedChangeListener(null);
            cbTaskCompleted.setChecked(task.isCompleted());

            // Apply visual styling (Strikethrough text & badge colors)
            if (task.isCompleted()) {
                tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_completed));
                tvStatusBadge.setText("Completed");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_completed);
                tvStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.accent_dark));
            } else {
                tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTaskTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
                tvStatusBadge.setText("Pending");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending);
                tvStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            }

            // Checkbox Listener
            cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskStatusChanged(task, isChecked);
                }
            });

            // Delete Listener
            btnDeleteTask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDelete(task);
                }
            });
        }
    }
}
