package com.oasis.todoapp.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oasis.todoapp.R;
import com.oasis.todoapp.adapter.TaskAdapter;
import com.oasis.todoapp.database.DatabaseHelper;
import com.oasis.todoapp.model.Task;
import com.oasis.todoapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TodoActivity displays and manages personal tasks for the currently authenticated user.
 * Guarantees complete task isolation between distinct user accounts.
 */
public class TodoActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private TextView tvUserWelcome, tvUserEmailSub;
    private TextView tvTaskCounter, tvCompletedStats;
    private ImageButton btnLogout;
    private RecyclerView rvTasks;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddTask;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private int activeUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Security Check: Verify session state
        if (!sessionManager.checkLogin()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_todo);

        dbHelper = new DatabaseHelper(this);
        activeUserId = sessionManager.getUserId();

        initViews();
        setupUserHeader();
        setupRecyclerView();
        setupListeners();

        loadUserTasks();
    }

    private void initViews() {
        tvUserWelcome = findViewById(R.id.tvUserWelcome);
        tvUserEmailSub = findViewById(R.id.tvUserEmailSub);
        tvTaskCounter = findViewById(R.id.tvTaskCounter);
        tvCompletedStats = findViewById(R.id.tvCompletedStats);
        btnLogout = findViewById(R.id.btnLogout);
        rvTasks = findViewById(R.id.rvTasks);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddTask = findViewById(R.id.fabAddTask);
    }

    private void setupUserHeader() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        tvUserWelcome.setText("Welcome, " + userName + "!");
        tvUserEmailSub.setText(userEmail);
    }

    private void setupRecyclerView() {
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);
    }

    private void setupListeners() {
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        sessionManager.logoutUser();
                        Toast.makeText(TodoActivity.this, getString(R.string.msg_logout_success), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadUserTasks() {
        taskList.clear();
        List<Task> fetchedTasks = dbHelper.getTasksForUser(activeUserId);
        taskList.addAll(fetchedTasks);

        taskAdapter.notifyDataSetChanged();
        updateTaskStatistics();
    }

    private void updateTaskStatistics() {
        int totalCount = taskList.size();
        int completedCount = 0;

        for (Task task : taskList) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }

        tvTaskCounter.setText(totalCount + (totalCount == 1 ? " Task total" : " Tasks total"));
        tvCompletedStats.setText(completedCount + " Completed");

        if (totalCount == 0) {
            rvTasks.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvTasks.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskNotes = dialogView.findViewById(R.id.etTaskNotes);
        TextView tvError = dialogView.findViewById(R.id.tvErrorDialog);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelDialog);
        Button btnSave = dialogView.findViewById(R.id.btnSaveDialog);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String notes = etTaskNotes.getText().toString().trim();

            if (title.isEmpty()) {
                tvError.setText(getString(R.string.err_task_empty));
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            Task newTask = new Task(activeUserId, title, notes);
            long result = dbHelper.addTask(newTask);

            if (result > 0) {
                Toast.makeText(TodoActivity.this, getString(R.string.msg_task_added), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadUserTasks();
            } else {
                tvError.setText("Failed to save task into database.");
                tvError.setVisibility(View.VISIBLE);
            }
        });

        dialog.show();
    }

    @Override
    public void onTaskStatusChanged(Task task, boolean isCompleted) {
        dbHelper.updateTaskCompletion(task.getId(), activeUserId, isCompleted);
        task.setCompleted(isCompleted);
        loadUserTasks();
    }

    @Override
    public void onTaskDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage(getString(R.string.dialog_delete_message))
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteTask(task.getId(), activeUserId);
                    if (success) {
                        Toast.makeText(TodoActivity.this, getString(R.string.msg_task_deleted), Toast.LENGTH_SHORT).show();
                        loadUserTasks();
                    } else {
                        Toast.makeText(TodoActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            loadUserTasks();
        }
    }
}
