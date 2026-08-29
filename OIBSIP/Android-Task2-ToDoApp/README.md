# TodoApp - Oasis Infobyte Internship Task 2

An Android To-Do application featuring secure user registration, SHA-256 password hashing, user-isolated task lists stored in SQLite database, session management, and a clean XML UI layout.

---

## Features

1. **User Authentication & Registration**
   - User Sign-Up with Name, Email, and Password.
   - Email uniqueness verification (duplicate user prevention).
   - Non-plaintext password storage via **SHA-256 password hashing**.
   - Generic authentication error messages to prevent account enumeration.

2. **User-Isolated Task Management**
   - Distinct personal task list per logged-in user account.
   - User A cannot view, access, or edit User B's tasks.
   - Relational `tasks` table linked via foreign key `user_id` to `users.id`.

3. **To-Do List Operations**
   - Add new task with required Title and optional Notes.
   - Toggle task completion status with visual strikethrough styling and badge indicators.
   - Permanent task deletion with confirmation dialog.
   - Friendly empty-state illustration when no tasks exist.

4. **Session Persistence & Logout**
   - `SharedPreferences` session manager.
   - Auto-login on app launch if active session exists.
   - Secure Logout invalidating session state and restricting protected task access.

---

## Project Structure

```
TodoApp/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/oasis/todoapp/
│           │   ├── activity/
│           │   │   ├── MainActivity.java
│           │   │   ├── LoginActivity.java
│           │   │   ├── RegisterActivity.java
│           │   │   └── TodoActivity.java
│           │   ├── adapter/
│           │   │   └── TaskAdapter.java
│           │   ├── database/
│           │   │   └── DatabaseHelper.java
│           │   ├── model/
│           │   │   ├── User.java
│           │   │   └── Task.java
│           │   └── utils/
│           │       ├── PasswordUtils.java
│           │       └── SessionManager.java
│           └── res/
│               ├── drawable/
│               ├── layout/
│               │   ├── activity_login.xml
│               │   ├── activity_register.xml
│               │   ├── activity_todo.xml
│               │   ├── dialog_add_task.xml
│               │   └── item_task.xml
│               └── values/
│                   ├── colors.xml
│                   ├── strings.xml
│                   └── themes.xml
├── build.gradle
├── settings.gradle
└── README.md
```

---

## SQLite Database Schema

### `users` Table
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique user ID |
| `name` | `TEXT` | `NOT NULL` | User full name |
| `email` | `TEXT` | `UNIQUE NOT NULL` | Login email identifier |
| `password_hash` | `TEXT` | `NOT NULL` | SHA-256 hashed password |

### `tasks` Table
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique task ID |
| `user_id` | `INTEGER` | `NOT NULL, FOREIGN KEY(users.id)` | Owner user ID |
| `task_name` | `TEXT` | `NOT NULL` | Task title |
| `notes` | `TEXT` | | Optional notes |
| `completed` | `INTEGER` | `DEFAULT 0` | 0 = Pending, 1 = Completed |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | Creation timestamp |

---

## How to Run in Android Studio

1. Open **Android Studio**.
2. Select **Open an existing Android Studio project**.
3. Choose the `TodoApp` directory (`d:/OIBSIP file/TodoApp`).
4. Wait for Gradle Sync to complete.
5. Select an Emulator (or physical Android device) and click **Run (Shift + F10)**.

---

## Verification & Test Results

The application flow was verified against all requirements:
1. **User Registration**: Successfully registered User 1 (`alice@example.com`). Re-submitting duplicate email triggers an explicit error.
2. **Password Hashing**: SHA-256 digest converts `password123` to `ef92b778bafe771eef93582fe09d57a2e2697fe...`.
3. **Login Security**: Incorrect passwords display generic "Invalid email or password" error.
4. **User Isolation**:
   - `Alice` creates tasks: *"Buy groceries"* and *"Complete project"*.
   - `Bob` registers and logs in: Sees empty state ("No Tasks Found").
   - `Bob` creates task: *"Bob's meeting"*. `Alice`'s tasks are NOT visible to `Bob`.
5. **Completion & Deletion**: Toggling checkbox updates `completed = 1` in SQLite with visual strikethrough. Permanent deletion removes task record.
6. **Logout**: Clears `SharedPreferences` session and forces redirection back to `LoginActivity`.
