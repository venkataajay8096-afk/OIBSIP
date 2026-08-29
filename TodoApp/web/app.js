/**
 * TodoApp Web Application Engine
 * Replicates Android App logic: SHA-256 Hashing, SQLite-like LocalStorage, User Isolation, Task CRUD & Session Management.
 */

// ==========================================
// 1. UTILITY FUNCTIONS & HASHING
// ==========================================

// SHA-256 Password Hashing via Web Crypto API
async function hashPassword(password) {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

// Toast Notification
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icon = type === 'success' ? 'fa-circle-check' : 'fa-circle-info';
    toast.innerHTML = `<i class="fa-solid ${icon}"></i><span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Password Visibility Toggle
function togglePasswordVisibility(inputId, iconEl) {
    const input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
        iconEl.classList.replace('fa-eye', 'fa-eye-slash');
    } else {
        input.type = 'password';
        iconEl.classList.replace('fa-eye-slash', 'fa-eye');
    }
}

// Screen Switcher
function switchScreen(screenId) {
    document.querySelectorAll('.screen').forEach(s => {
        s.classList.remove('active');
        s.classList.add('hidden');
    });
    const target = document.getElementById(screenId);
    if (target) {
        target.classList.remove('hidden');
        target.classList.add('active');
    }
}

// ==========================================
// 2. LOCALSTORAGE DATABASE EMULATION
// ==========================================

function getUsersDB() {
    return JSON.parse(localStorage.getItem('todo_users_db') || '[]');
}

function saveUsersDB(users) {
    localStorage.setItem('todo_users_db', JSON.stringify(users));
}

function getTasksDB() {
    return JSON.parse(localStorage.getItem('todo_tasks_db') || '[]');
}

function saveTasksDB(tasks) {
    localStorage.setItem('todo_tasks_db', JSON.stringify(tasks));
}

function getActiveSession() {
    return JSON.parse(sessionStorage.getItem('todo_user_session') || localStorage.getItem('todo_user_session') || 'null');
}

function setActiveSession(user) {
    sessionStorage.setItem('todo_user_session', JSON.stringify(user));
    localStorage.setItem('todo_user_session', JSON.stringify(user));
}

function clearActiveSession() {
    sessionStorage.removeItem('todo_user_session');
    localStorage.removeItem('todo_user_session');
}

// Initial Seed User if DB is empty
(function seedInitialData() {
    let users = getUsersDB();
    if (users.length === 0) {
        hashPassword('password123').then(hash => {
            users.push({
                id: 1,
                name: 'Alice Smith',
                email: 'alice@example.com',
                passwordHash: hash
            });
            saveUsersDB(users);

            // Initial tasks for Alice
            let tasks = [
                { id: 101, userId: 1, title: 'Complete Oasis Infobyte Task 2', notes: 'Android To-Do app with SQLite & Web link', completed: true },
                { id: 102, userId: 1, title: 'Review Code Structure', notes: 'Verify clean architecture & viva questions', completed: false }
            ];
            saveTasksDB(tasks);
        });
    }
})();

// ==========================================
// 3. AUTHENTICATION CONTROLLER
// ==========================================

async function handleLogin(event) {
    event.preventDefault();
    const alertEl = document.getElementById('loginAlert');
    alertEl.classList.add('hidden');

    const email = document.getElementById('loginEmail').value.trim().toLowerCase();
    const password = document.getElementById('loginPassword').value.trim();

    if (!email || !password) {
        alertEl.textContent = 'Please fill in all required fields';
        alertEl.classList.remove('hidden');
        return;
    }

    const inputHash = await hashPassword(password);
    const users = getUsersDB();
    const matchedUser = users.find(u => u.email === email && u.passwordHash === inputHash);

    if (matchedUser) {
        setActiveSession({ id: matchedUser.id, name: matchedUser.name, email: matchedUser.email });
        showToast(`Welcome back, ${matchedUser.name}!`, 'success');
        document.getElementById('formLogin').reset();
        initDashboard();
    } else {
        // Generic security error message (does not reveal if email or password was wrong)
        alertEl.textContent = 'Invalid email or password. Please try again.';
        alertEl.classList.remove('hidden');
    }
}

async function handleRegister(event) {
    event.preventDefault();
    const alertEl = document.getElementById('registerAlert');
    alertEl.classList.add('hidden');

    const name = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim().toLowerCase();
    const password = document.getElementById('regPassword').value.trim();

    if (!name || !email || !password) {
        alertEl.textContent = 'Please fill in all required fields';
        alertEl.classList.remove('hidden');
        return;
    }

    const users = getUsersDB();
    if (users.some(u => u.email === email)) {
        alertEl.textContent = 'An account with this email already exists';
        alertEl.classList.remove('hidden');
        return;
    }

    const passwordHash = await hashPassword(password);
    const newUser = {
        id: Date.now(),
        name: name,
        email: email,
        passwordHash: passwordHash
    };

    users.push(newUser);
    saveUsersDB(users);

    showToast('Account registered successfully! Please login.', 'success');
    document.getElementById('formRegister').reset();
    document.getElementById('loginEmail').value = email;
    switchScreen('screenLogin');
}

function promptLogout() {
    openConfirmModal(
        'Logout',
        'Are you sure you want to log out of your session?',
        () => {
            clearActiveSession();
            showToast('Logged out successfully');
            switchScreen('screenLogin');
        }
    );
}

// ==========================================
// 4. TASK DASHBOARD CONTROLLER (USER ISOLATED)
// ==========================================

function initDashboard() {
    const session = getActiveSession();
    if (!session) {
        switchScreen('screenLogin');
        return;
    }

    // Populate user profile info in app bar
    document.getElementById('headerUserName').textContent = `Welcome, ${session.name}!`;
    document.getElementById('headerUserEmail').textContent = session.email;
    document.getElementById('headerAvatar').textContent = session.name.charAt(0).toUpperCase();

    renderUserTasks();
    switchScreen('screenDashboard');
}

function renderUserTasks() {
    const session = getActiveSession();
    if (!session) return;

    const allTasks = getTasksDB();
    // Filter tasks belonging EXCLUSIVELY to logged-in user
    const userTasks = allTasks.filter(t => t.userId === session.id);

    const taskListEl = document.getElementById('taskList');
    const emptyStateEl = document.getElementById('emptyState');
    const totalCounterEl = document.getElementById('taskTotalCounter');
    const completedBadgeEl = document.getElementById('taskCompletedBadge');

    taskListEl.innerHTML = '';

    const completedCount = userTasks.filter(t => t.completed).length;
    totalCounterEl.textContent = `${userTasks.length} ${userTasks.length === 1 ? 'Task total' : 'Tasks total'}`;
    completedBadgeEl.textContent = `${completedCount} Completed`;

    if (userTasks.length === 0) {
        taskListEl.style.display = 'none';
        emptyStateEl.classList.remove('hidden');
    } else {
        taskListEl.style.display = 'flex';
        emptyStateEl.classList.add('hidden');

        // Sort: pending first, newest first
        userTasks.sort((a, b) => a.completed - b.completed || b.id - a.id);

        userTasks.forEach(task => {
            const card = document.createElement('div');
            card.className = `task-item ${task.completed ? 'completed' : ''}`;
            card.innerHTML = `
                <input type="checkbox" class="task-checkbox" ${task.completed ? 'checked' : ''} onchange="toggleTaskStatus(${task.id}, this.checked)">
                <div class="task-content">
                    <div class="task-title">${escapeHTML(task.title)}</div>
                    ${task.notes ? `<div class="task-notes">${escapeHTML(task.notes)}</div>` : ''}
                </div>
                <span class="task-badge ${task.completed ? 'done' : 'pending'}">${task.completed ? 'Done' : 'Pending'}</span>
                <button class="btn-task-delete" onclick="promptDeleteTask(${task.id})" title="Delete Task">
                    <i class="fa-regular fa-trash-can"></i>
                </button>
            `;
            taskListEl.appendChild(card);
        });
    }
}

function toggleTaskStatus(taskId, isChecked) {
    const session = getActiveSession();
    if (!session) return;

    let tasks = getTasksDB();
    const taskIndex = tasks.findIndex(t => t.id === taskId && t.userId === session.id);
    if (taskIndex !== -1) {
        tasks[taskIndex].completed = isChecked;
        saveTasksDB(tasks);
        renderUserTasks();
    }
}

function promptDeleteTask(taskId) {
    openConfirmModal(
        'Delete Task',
        'Are you sure you want to permanently delete this task?',
        () => {
            const session = getActiveSession();
            if (!session) return;

            let tasks = getTasksDB();
            tasks = tasks.filter(t => !(t.id === taskId && t.userId === session.id));
            saveTasksDB(tasks);
            showToast('Task deleted permanently', 'info');
            renderUserTasks();
        }
    );
}

// ==========================================
// 5. MODAL DIALOG CONTROLLERS
// ==========================================

function openAddTaskModal() {
    document.getElementById('formAddTask').reset();
    document.getElementById('modalAlert').classList.add('hidden');
    document.getElementById('addTaskModal').classList.remove('hidden');
    document.getElementById('taskTitle').focus();
}

function closeAddTaskModal() {
    document.getElementById('addTaskModal').classList.add('hidden');
}

function handleAddTask(event) {
    event.preventDefault();
    const session = getActiveSession();
    if (!session) return;

    const alertEl = document.getElementById('modalAlert');
    alertEl.classList.add('hidden');

    const title = document.getElementById('taskTitle').value.trim();
    const notes = document.getElementById('taskNotes').value.trim();

    if (!title) {
        alertEl.textContent = 'Task name cannot be empty';
        alertEl.classList.remove('hidden');
        return;
    }

    const newTask = {
        id: Date.now(),
        userId: session.id,
        title: title,
        notes: notes,
        completed: false
    };

    const tasks = getTasksDB();
    tasks.push(newTask);
    saveTasksDB(tasks);

    closeAddTaskModal();
    showToast('Task saved successfully', 'success');
    renderUserTasks();
}

let pendingConfirmAction = null;

function openConfirmModal(title, message, onConfirm) {
    document.getElementById('confirmTitle').textContent = title;
    document.getElementById('confirmMessage').textContent = message;
    pendingConfirmAction = onConfirm;
    document.getElementById('confirmModal').classList.remove('hidden');

    document.getElementById('btnConfirmAction').onclick = () => {
        if (pendingConfirmAction) pendingConfirmAction();
        closeConfirmModal();
    };
}

function closeConfirmModal() {
    document.getElementById('confirmModal').classList.add('hidden');
    pendingConfirmAction = null;
}

function escapeHTML(str) {
    return str.replace(/[&<>'"]/g, 
        tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
    );
}

// Check session on page load
window.addEventListener('DOMContentLoaded', () => {
    const session = getActiveSession();
    if (session) {
        initDashboard();
    } else {
        switchScreen('screenLogin');
    }
});
