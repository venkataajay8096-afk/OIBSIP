package com.oasis.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ResultActivity - Results Screen
 *
 * This activity displays the quiz results after all questions are answered:
 * - Total score (e.g., "Score: 8 / 10")
 * - Number of correct answers
 * - Number of incorrect answers
 * - A motivational message based on score
 * - "Restart Quiz" button to start over
 *
 * Score and total questions are received via Intent extras from QuizActivity.
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvResultTitle;
    private TextView tvResultMessage;
    private TextView tvScore;
    private TextView tvCorrectCount;
    private TextView tvIncorrectCount;
    private Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize UI elements
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultMessage = findViewById(R.id.tvResultMessage);
        tvScore = findViewById(R.id.tvScore);
        tvCorrectCount = findViewById(R.id.tvCorrectCount);
        tvIncorrectCount = findViewById(R.id.tvIncorrectCount);
        btnRestart = findViewById(R.id.btnRestart);

        // Get score and total questions from Intent
        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 10);
        int incorrect = totalQuestions - score;

        // Display the results
        tvScore.setText(getString(R.string.result_score, score, totalQuestions));
        tvCorrectCount.setText(String.valueOf(score));
        tvIncorrectCount.setText(String.valueOf(incorrect));

        // Display motivational message based on score percentage
        double percentage = (double) score / totalQuestions * 100;
        if (percentage >= 80) {
            tvResultMessage.setText(getString(R.string.result_great));
        } else if (percentage >= 60) {
            tvResultMessage.setText(getString(R.string.result_good));
        } else if (percentage >= 40) {
            tvResultMessage.setText(getString(R.string.result_average));
        } else {
            tvResultMessage.setText(getString(R.string.result_poor));
        }

        // Restart Quiz button - go back to Welcome screen
        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            // Clear the activity stack so pressing back doesn't go to results
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close results activity
        });
    }

    /**
     * Override back button to go to Welcome screen instead of QuizActivity.
     * This prevents going back to a completed quiz.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
