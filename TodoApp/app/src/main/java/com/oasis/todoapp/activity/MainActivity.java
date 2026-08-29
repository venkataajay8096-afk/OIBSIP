package com.oasis.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.oasis.todoapp.utils.SessionManager;

/**
 * Launcher activity that checks user session status and directs the flow
 * to either TodoActivity (if authenticated) or LoginActivity (if logged out).
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, TodoActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        finish(); // Prevent returning to launcher on back button
    }
}
