package com.oasis.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * QuizActivity - Question Screen
 *
 * This activity handles the entire quiz flow:
 * - Displays one question at a time with 4 options
 * - Shows question counter and progress bar
 * - Highlights correct/wrong answers with green/red feedback
 * - Locks answer selection after first tap (prevents multiple scoring)
 * - Tracks score throughout the quiz
 * - Navigates to ResultActivity after the last question
 *
 * Question shuffling: Questions are shuffled using Collections.shuffle()
 * each time the quiz starts. The original data is never modified because
 * QuestionData.getQuestions() returns a new copy each time.
 */
public class QuizActivity extends AppCompatActivity {

    // UI Elements
    private TextView tvQuestionCounter;
    private ProgressBar progressBar;
    private TextView tvQuestion;
    private TextView tvOption1, tvOption2, tvOption3, tvOption4;
    private Button btnNext;

    // Quiz Data
    private ArrayList<Question> questionList;  // Shuffled list of questions
    private int currentQuestionIndex = 0;      // Current question number (0-based)
    private int score = 0;                      // User's score
    private boolean answered = false;           // Whether current question has been answered

    // Total number of questions
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize UI elements
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter);
        progressBar = findViewById(R.id.progressBar);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvOption1 = findViewById(R.id.tvOption1);
        tvOption2 = findViewById(R.id.tvOption2);
        tvOption3 = findViewById(R.id.tvOption3);
        tvOption4 = findViewById(R.id.tvOption4);
        btnNext = findViewById(R.id.btnNext);

        // Load questions and shuffle them
        questionList = QuestionData.getQuestions();
        Collections.shuffle(questionList); // Shuffle the question order
        totalQuestions = questionList.size();

        // Set progress bar max to total questions
        progressBar.setMax(totalQuestions);

        // Display the first question
        displayQuestion();

        // Set click listeners for all 4 options
        tvOption1.setOnClickListener(v -> checkAnswer(0, tvOption1));
        tvOption2.setOnClickListener(v -> checkAnswer(1, tvOption2));
        tvOption3.setOnClickListener(v -> checkAnswer(2, tvOption3));
        tvOption4.setOnClickListener(v -> checkAnswer(3, tvOption4));

        // Next button click listener
        btnNext.setOnClickListener(v -> {
            // If user hasn't selected an answer, show a message
            if (!answered) {
                Toast.makeText(QuizActivity.this,
                        getString(R.string.toast_select_answer),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Move to next question or finish quiz
            currentQuestionIndex++;

            if (currentQuestionIndex < totalQuestions) {
                // There are more questions — display the next one
                displayQuestion();
            } else {
                // All questions answered — go to Results screen
                openResultActivity();
            }
        });
    }

    /**
     * Displays the current question and its 4 options.
     * Resets all option backgrounds to default state.
     * Updates the question counter and progress bar.
     */
    private void displayQuestion() {
        // Reset the answered flag for the new question
        answered = false;

        // Reset all option backgrounds to default
        resetOptionBackgrounds();

        // Get the current question
        Question currentQuestion = questionList.get(currentQuestionIndex);

        // Set question text and options
        tvQuestion.setText(currentQuestion.getQuestionText());
        tvOption1.setText(currentQuestion.getOption1());
        tvOption2.setText(currentQuestion.getOption2());
        tvOption3.setText(currentQuestion.getOption3());
        tvOption4.setText(currentQuestion.getOption4());

        // Update question counter (display 1-based index)
        tvQuestionCounter.setText(getString(R.string.question_counter,
                currentQuestionIndex + 1, totalQuestions));

        // Update progress bar
        progressBar.setProgress(currentQuestionIndex + 1);

        // Update Next button text for the last question
        if (currentQuestionIndex == totalQuestions - 1) {
            btnNext.setText(getString(R.string.btn_finish));
        } else {
            btnNext.setText(getString(R.string.btn_next));
        }

        // Re-enable all options for clicking
        setOptionsClickable(true);
    }

    /**
     * Checks if the selected answer is correct and provides visual feedback.
     *
     * How it works:
     * 1. If already answered, do nothing (prevents multiple scoring)
     * 2. Mark as answered
     * 3. Disable all option clicks
     * 4. If correct: highlight selected option in green, increment score
     * 5. If wrong: highlight selected in red, show correct answer in green
     *
     * @param selectedIndex The index (0-3) of the option the user tapped
     * @param selectedView  The TextView of the selected option
     */
    private void checkAnswer(int selectedIndex, TextView selectedView) {
        // Prevent answering the same question multiple times
        if (answered) {
            return;
        }

        // Mark this question as answered
        answered = true;

        // Disable all options to prevent further clicks
        setOptionsClickable(false);

        // Get the correct answer index for the current question
        int correctIndex = questionList.get(currentQuestionIndex).getCorrectAnswerIndex();

        if (selectedIndex == correctIndex) {
            // CORRECT ANSWER: Highlight selected option in green
            selectedView.setBackgroundResource(R.drawable.bg_option_correct);
            selectedView.setTextColor(getResources().getColor(R.color.correct_green));
            score++; // Add 1 point
        } else {
            // WRONG ANSWER: Highlight selected option in red
            selectedView.setBackgroundResource(R.drawable.bg_option_wrong);
            selectedView.setTextColor(getResources().getColor(R.color.wrong_red));

            // Also show the correct answer in green
            TextView correctView = getOptionViewByIndex(correctIndex);
            if (correctView != null) {
                correctView.setBackgroundResource(R.drawable.bg_option_correct);
                correctView.setTextColor(getResources().getColor(R.color.correct_green));
            }
        }
    }

    /**
     * Returns the TextView for a given option index (0-3).
     */
    private TextView getOptionViewByIndex(int index) {
        switch (index) {
            case 0: return tvOption1;
            case 1: return tvOption2;
            case 2: return tvOption3;
            case 3: return tvOption4;
            default: return null;
        }
    }

    /**
     * Resets all option backgrounds to the default (unselected) state.
     * Called when displaying a new question.
     */
    private void resetOptionBackgrounds() {
        tvOption1.setBackgroundResource(R.drawable.bg_option_default);
        tvOption2.setBackgroundResource(R.drawable.bg_option_default);
        tvOption3.setBackgroundResource(R.drawable.bg_option_default);
        tvOption4.setBackgroundResource(R.drawable.bg_option_default);

        // Reset text colors to default
        int defaultColor = getResources().getColor(R.color.text_primary);
        tvOption1.setTextColor(defaultColor);
        tvOption2.setTextColor(defaultColor);
        tvOption3.setTextColor(defaultColor);
        tvOption4.setTextColor(defaultColor);
    }

    /**
     * Enables or disables clicking on all option TextViews.
     * Used to lock options after an answer is selected.
     */
    private void setOptionsClickable(boolean clickable) {
        tvOption1.setClickable(clickable);
        tvOption2.setClickable(clickable);
        tvOption3.setClickable(clickable);
        tvOption4.setClickable(clickable);
    }

    /**
     * Navigates to the ResultActivity, passing the score and total questions.
     */
    private void openResultActivity() {
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", totalQuestions);
        startActivity(intent);
        finish(); // Close quiz activity
    }
}
