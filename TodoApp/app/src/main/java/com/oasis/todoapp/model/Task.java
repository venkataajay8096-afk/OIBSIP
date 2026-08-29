package com.oasis.todoapp.model;

/**
 * Model class representing a To-Do task belonging to a specific user.
 */
public class Task {
    private int id;
    private int userId;
    private String taskName;
    private String notes;
    private boolean isCompleted;
    private String createdAt;

    public Task() {
    }

    public Task(int id, int userId, String taskName, String notes, boolean isCompleted, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.taskName = taskName;
        this.notes = notes;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }

    public Task(int userId, String taskName, String notes) {
        this.userId = userId;
        this.taskName = taskName;
        this.notes = notes;
        this.isCompleted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
