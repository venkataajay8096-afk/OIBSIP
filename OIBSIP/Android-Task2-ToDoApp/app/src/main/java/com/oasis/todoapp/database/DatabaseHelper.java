package com.oasis.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oasis.todoapp.model.Task;
import com.oasis.todoapp.model.User;
import com.oasis.todoapp.utils.PasswordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages the SQLite database creation, upgrade,
 * user authentication tables, and user-isolated task CRUD operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_TASKS = "tasks";

    // Users Table Columns
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD_HASH = "password_hash";

    // Tasks Table Columns
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_USER_ID = "user_id";
    public static final String COLUMN_TASK_NAME = "task_name";
    public static final String COLUMN_TASK_NOTES = "notes";
    public static final String COLUMN_TASK_COMPLETED = "completed";
    public static final String COLUMN_TASK_CREATED_AT = "created_at";

    // Error Codes
    public static final long RESULT_SUCCESS = 1;
    public static final long RESULT_ERR_DUPLICATE_EMAIL = -2;
    public static final long RESULT_ERR_GENERIC = -1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT NOT NULL, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COLUMN_USER_PASSWORD_HASH + " TEXT NOT NULL"
                + ");";

        // Create Tasks Table linked to Users
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " ("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TASK_USER_ID + " INTEGER NOT NULL, "
                + COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + COLUMN_TASK_NOTES + " TEXT, "
                + COLUMN_TASK_COMPLETED + " INTEGER DEFAULT 0, "
                + COLUMN_TASK_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_TASK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE"
                + ");";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ==========================================
    // USER AUTHENTICATION METHODS
    // ==========================================

    /**
     * Register a new user into SQLite database with hashed password.
     *
     * @param user          User object containing name and email
     * @param plainPassword Plain-text password to hash
     * @return Row ID on success, RESULT_ERR_DUPLICATE_EMAIL (-2) if email exists, or RESULT_ERR_GENERIC (-1)
     */
    public long registerUser(User user, String plainPassword) {
        if (checkEmailExists(user.getEmail())) {
            return RESULT_ERR_DUPLICATE_EMAIL;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        values.put(COLUMN_USER_NAME, user.getName().trim());
        values.put(COLUMN_USER_EMAIL, user.getEmail().trim().toLowerCase());
        values.put(COLUMN_USER_PASSWORD_HASH, hashedPassword);

        long rowId = db.insert(TABLE_USERS, null, values);
        db.close();
        return rowId;
    }

    /**
     * Authenticate user credentials.
     *
     * @param email         User input email
     * @param plainPassword User input password
     * @return Authenticated User object if valid, or null if invalid
     */
    public User authenticateUser(String email, String plainPassword) {
        if (email == null || plainPassword == null) {
            return null;
        }

        String inputHash = PasswordUtils.hashPassword(plainPassword);
        String formattedEmail = email.trim().toLowerCase();

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD_HASH + " = ?";
        String[] selectionArgs = {formattedEmail, inputHash};

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        User authenticatedUser = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_USER_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_USER_NAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL);
            int hashIndex = cursor.getColumnIndex(COLUMN_USER_PASSWORD_HASH);

            authenticatedUser = new User(
                    cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(hashIndex)
            );
            cursor.close();
        }

        db.close();
        return authenticatedUser;
    }

    /**
     * Check whether an email already exists in the database.
     *
     * @param email Email to check
     * @return True if exists, false otherwise
     */
    public boolean checkEmailExists(String email) {
        if (email == null) return false;
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email.trim().toLowerCase()};

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, selection, selectionArgs, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    // ==========================================
    // USER-SPECIFIC TASK CRUD METHODS
    // ==========================================

    /**
     * Add a new task for the logged-in user.
     *
     * @param task Task object to insert
     * @return Row ID of created task, or -1 on error
     */
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TASK_USER_ID, task.getUserId());
        values.put(COLUMN_TASK_NAME, task.getTaskName().trim());
        values.put(COLUMN_TASK_NOTES, task.getNotes() != null ? task.getNotes().trim() : "");
        values.put(COLUMN_TASK_COMPLETED, task.isCompleted() ? 1 : 0);

        long rowId = db.insert(TABLE_TASKS, null, values);
        db.close();
        return rowId;
    }

    /**
     * Fetch all tasks belonging exclusively to the specified user ID.
     *
     * @param userId The ID of the logged-in user
     * @return List of Task objects belonging to the user
     */
    public List<Task> getTasksForUser(int userId) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_TASK_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        // Order by non-completed tasks first, then newest first
        String orderBy = COLUMN_TASK_COMPLETED + " ASC, " + COLUMN_TASK_ID + " DESC";

        Cursor cursor = db.query(TABLE_TASKS, null, selection, selectionArgs, null, null, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndex(COLUMN_TASK_ID);
            int userIdIdx = cursor.getColumnIndex(COLUMN_TASK_USER_ID);
            int nameIdx = cursor.getColumnIndex(COLUMN_TASK_NAME);
            int notesIdx = cursor.getColumnIndex(COLUMN_TASK_NOTES);
            int completedIdx = cursor.getColumnIndex(COLUMN_TASK_COMPLETED);
            int createdAtIdx = cursor.getColumnIndex(COLUMN_TASK_CREATED_AT);

            do {
                Task task = new Task(
                        cursor.getInt(idIdx),
                        cursor.getInt(userIdIdx),
                        cursor.getString(nameIdx),
                        cursor.getString(notesIdx),
                        cursor.getInt(completedIdx) == 1,
                        cursor.getString(createdAtIdx)
                );
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return taskList;
    }

    /**
     * Toggle or update completion status of a task for a specific user.
     *
     * @param taskId      ID of the task to update
     * @param userId      ID of the logged-in user (ensures user isolation)
     * @param isCompleted True if task is completed, false otherwise
     * @return True if updated successfully, false otherwise
     */
    public boolean updateTaskCompletion(int taskId, int userId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_COMPLETED, isCompleted ? 1 : 0);

        String whereClause = COLUMN_TASK_ID + " = ? AND " + COLUMN_TASK_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(taskId), String.valueOf(userId)};

        int rowsAffected = db.update(TABLE_TASKS, values, whereClause, whereArgs);
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Permanently delete a task belonging to a specific user.
     *
     * @param taskId ID of the task to delete
     * @param userId ID of the logged-in user
     * @return True if deleted successfully, false otherwise
     */
    public boolean deleteTask(int taskId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_TASK_ID + " = ? AND " + COLUMN_TASK_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(taskId), String.valueOf(userId)};

        int rowsDeleted = db.delete(TABLE_TASKS, whereClause, whereArgs);
        db.close();
        return rowsDeleted > 0;
    }
}
