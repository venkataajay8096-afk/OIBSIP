package com.oasis.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity - Welcome Screen
 *
 * This is the first screen the user sees when opening the app.
 * It displays:
 * - Quiz title
 * - Short description
 * - "Start Quiz" button
 *
 * When the user taps "Start Quiz", it navigates to QuizActivity.
 */
public class MainActivity extends AppCompatActivity {

    private Button btnStartQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the Start Quiz button
        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        // Set click listener - navigate to QuizActivity when tapped
        btnStartQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
            finish(); // Close welcome screen so user can't go back to it
        });
    }
}
