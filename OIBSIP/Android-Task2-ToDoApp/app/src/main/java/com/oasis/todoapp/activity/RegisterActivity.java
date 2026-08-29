package com.oasis.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oasis.todoapp.R;
import com.oasis.todoapp.database.DatabaseHelper;
import com.oasis.todoapp.model.User;

/**
 * RegisterActivity handles user sign-up.
 * Validates inputs, verifies email uniqueness, hashes password, and saves user to SQLite database.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private TextView tvError;
    private Button btnRegister;
    private TextView tvGoToLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        tvError = findViewById(R.id.tvErrorRegister);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegistration());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        tvError.setVisibility(View.GONE);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation 1: Required non-empty fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.err_empty_fields));
            return;
        }

        // Validation 2: Email format check
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address.");
            return;
        }

        // Validation 3: Password length check
        if (password.length() < 4) {
            showError("Password must be at least 4 characters long.");
            return;
        }

        User newUser = new User(name, email, "");

        // Attempt registration in database
        long result = dbHelper.registerUser(newUser, password);

        if (result == DatabaseHelper.RESULT_ERR_DUPLICATE_EMAIL) {
            showError(getString(R.string.err_user_exists));
        } else if (result > 0) {
            Toast.makeText(this, getString(R.string.msg_register_success), Toast.LENGTH_LONG).show();

            // Redirect back to Login with pre-filled email
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("registered_email", email);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            showError("Registration failed due to a database error.");
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
