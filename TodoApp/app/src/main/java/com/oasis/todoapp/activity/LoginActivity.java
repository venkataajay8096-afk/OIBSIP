package com.oasis.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oasis.todoapp.R;
import com.oasis.todoapp.database.DatabaseHelper;
import com.oasis.todoapp.model.User;
import com.oasis.todoapp.utils.SessionManager;

/**
 * LoginActivity handles user authentication.
 * Validates inputs, queries SQLite database for matching email and hashed password,
 * establishes session upon success, or displays clear generic error on failure.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvError;
    private Button btnLogin;
    private TextView tvGoToRegister;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Redirect immediately if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, TodoActivity.class));
            finish();
            return;
        }

        initViews();
        setupListeners();
        checkPassedEmail();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        tvError = findViewById(R.id.tvErrorLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void checkPassedEmail() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("registered_email")) {
            String email = intent.getStringExtra("registered_email");
            if (email != null) {
                etEmail.setText(email);
                etPassword.requestFocus();
            }
        }
    }

    private void performLogin() {
        tvError.setVisibility(View.GONE);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation 1: Required non-empty fields
        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.err_empty_fields));
            return;
        }

        // Authenticate against SQLite database (Password is hashed internally)
        User authenticatedUser = dbHelper.authenticateUser(email, password);

        if (authenticatedUser != null) {
            // Save login session
            sessionManager.createLoginSession(
                    authenticatedUser.getId(),
                    authenticatedUser.getName(),
                    authenticatedUser.getEmail()
            );

            Toast.makeText(this, "Welcome back, " + authenticatedUser.getName() + "!", Toast.LENGTH_SHORT).show();

            // Redirect to To-Do dashboard
            Intent intent = new Intent(LoginActivity.this, TodoActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Generic security error message (does not disclose if email or password was wrong)
            showError(getString(R.string.err_invalid_credentials));
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
