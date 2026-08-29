package com.example.stopwatch;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

/**
 * MainActivity - Stopwatch Application
 * Developed for OASIS INFOBYTE: Android App Development — Task 5
 *
 * Implements high-precision elapsed time calculation using SystemClock.elapsedRealtime(),
 * UI updates via Handler + Runnable, and robust Android Activity Lifecycle management.
 */
public class MainActivity extends AppCompatActivity {

    // UI View References
    private TextView tvTimerDisplay;
    private TextView tvStatus;
    private TextView tvElapsedDetails;
    private MaterialButton btnStart;
    private MaterialButton btnPause;
    private MaterialButton btnReset;

    // Timer State Variables
    private boolean isRunning = false;
    private long startTime = 0L;              // Timestamp when Start/Resume was pressed
    private long accumulatedPausedTime = 0L;  // Total elapsed time before the latest pause

    // Handler and Runnable for smooth 60fps UI updates
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    // Keys for Bundle instance state preservation across configuration changes
    private static final String KEY_IS_RUNNING = "key_is_running";
    private static final String KEY_START_TIME = "key_start_time";
    private static final String KEY_ACCUMULATED_TIME = "key_accumulated_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        initializeViews();

        // 2. Setup the Runnable for UI timer updates
        setupTimerRunnable();

        // 3. Attach click listeners
        setupClickListeners();

        // 4. Restore state if activity was recreated
        if (savedInstanceState != null) {
            isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING, false);
            startTime = savedInstanceState.getLong(KEY_START_TIME, 0L);
            accumulatedPausedTime = savedInstanceState.getLong(KEY_ACCUMULATED_TIME, 0L);

            // If it was running when destroyed, restart the Handler loop
            if (isRunning) {
                timerHandler.post(timerRunnable);
            } else {
                updateTimerDisplay(accumulatedPausedTime);
            }
        }

        // 5. Update button states and status indicator
        updateUIState();
    }

    /**
     * Finds and assigns view references from the XML layout.
     */
    private void initializeViews() {
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        tvStatus = findViewById(R.id.tvStatus);
        tvElapsedDetails = findViewById(R.id.tvElapsedDetails);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnReset = findViewById(R.id.btnReset);
    }

    /**
     * Initializes the Runnable that updates the timer UI every ~15 milliseconds.
     */
    private void setupTimerRunnable() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long currentElapsed = getCurrentElapsedTime();
                    updateTimerDisplay(currentElapsed);
                    // Schedule next update for smooth sub-second rendering
                    timerHandler.postDelayed(this, 15);
                }
            }
        };
    }

    /**
     * Sets up click listeners for the Start, Pause, and Reset buttons.
     */
    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> startStopwatch());
        btnPause.setOnClickListener(v -> pauseStopwatch());
        btnReset.setOnClickListener(v -> resetStopwatch());
    }

    /**
     * Starts or resumes the stopwatch.
     * Prevents multiple Handler loops if Start is clicked repeatedly.
     */
    private void startStopwatch() {
        if (isRunning) {
            return; // Safety guard: ignore if already running
        }

        isRunning = true;
        startTime = SystemClock.elapsedRealtime();

        // Remove any previous callbacks to guarantee only 1 loop runs
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.post(timerRunnable);

        updateUIState();
    }

    /**
     * Freezes the stopwatch at current elapsed time without resetting.
     */
    private void pauseStopwatch() {
        if (!isRunning) {
            return; // Safety guard: ignore if not running
        }

        // Calculate and accumulate total elapsed time up to this instant
        accumulatedPausedTime += (SystemClock.elapsedRealtime() - startTime);
        isRunning = false;

        // Stop Handler loop to save battery and memory
        timerHandler.removeCallbacks(timerRunnable);

        // Render frozen time
        updateTimerDisplay(accumulatedPausedTime);

        updateUIState();
    }

    /**
     * Stops the stopwatch and resets elapsed time back to 00:00:00.
     */
    private void resetStopwatch() {
        // Stop any running loop
        timerHandler.removeCallbacks(timerRunnable);

        isRunning = false;
        startTime = 0L;
        accumulatedPausedTime = 0L;

        // Reset display to initial 00:00:00
        updateTimerDisplay(0L);

        updateUIState();
    }

    /**
     * Calculates the exact total elapsed time in milliseconds.
     * Combines previous accumulated paused time + time elapsed in the current running session.
     */
    private long getCurrentElapsedTime() {
        if (isRunning) {
            return accumulatedPausedTime + (SystemClock.elapsedRealtime() - startTime);
        } else {
            return accumulatedPausedTime;
        }
    }

    /**
     * Formats milliseconds into MM:SS:ms (e.g. 01:23:45).
     *
     * @param elapsedMillis Total milliseconds
     */
    private void updateTimerDisplay(long elapsedMillis) {
        long minutes = (elapsedMillis / 60000);
        long seconds = (elapsedMillis % 60000) / 1000;
        long centiseconds = (elapsedMillis % 1000) / 10; // 2-digit hundredths of a second (00-99)

        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, centiseconds);
        tvTimerDisplay.setText(formattedTime);

        // Update detail badge with total milliseconds
        tvElapsedDetails.setText(String.format(Locale.getDefault(), "%,d ms", elapsedMillis));
    }

    /**
     * Updates button states and the status indicator based on current stopwatch state.
     * - RUNNING: Start disabled, Pause enabled, Reset enabled.
     * - PAUSED: Start enabled ("RESUME"), Pause disabled, Reset enabled.
     * - READY / INITIAL: Start enabled ("START"), Pause disabled, Reset disabled.
     */
    private void updateUIState() {
        if (isRunning) {
            // Stopwatch is running
            btnStart.setEnabled(false);
            btnStart.setText(R.string.btn_start);

            btnPause.setEnabled(true);
            btnReset.setEnabled(true); // Allow resetting while running or paused

            tvStatus.setText(R.string.status_running);
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_running));
        } else if (accumulatedPausedTime > 0) {
            // Stopwatch is paused
            btnStart.setEnabled(true);
            btnStart.setText(R.string.btn_resume);

            btnPause.setEnabled(false);
            btnReset.setEnabled(true);

            tvStatus.setText(R.string.status_paused);
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_paused));
        } else {
            // Stopwatch is at initial 00:00:00 / Reset state
            btnStart.setEnabled(true);
            btnStart.setText(R.string.btn_start);

            btnPause.setEnabled(false);
            btnReset.setEnabled(false);

            tvStatus.setText(R.string.status_ready);
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_ready));
        }
    }

    // ==========================================
    // ANDROID ACTIVITY LIFECYCLE MANAGEMENT
    // ==========================================

    @Override
    protected void onPause() {
        super.onPause();
        // Remove handler callbacks while activity is not visible to prevent memory leaks and UI overhead
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the stopwatch was running, resume the Handler updates seamlessly
        if (isRunning) {
            timerHandler.post(timerRunnable);
        } else {
            updateTimerDisplay(accumulatedPausedTime);
        }
        updateUIState();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Persist stopwatch state across orientation changes or system kills
        outState.putBoolean(KEY_IS_RUNNING, isRunning);
        outState.putLong(KEY_START_TIME, startTime);
        outState.putLong(KEY_ACCUMULATED_TIME, accumulatedPausedTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING, false);
        startTime = savedInstanceState.getLong(KEY_START_TIME, 0L);
        accumulatedPausedTime = savedInstanceState.getLong(KEY_ACCUMULATED_TIME, 0L);

        if (isRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.post(timerRunnable);
        } else {
            updateTimerDisplay(accumulatedPausedTime);
        }
        updateUIState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Always remove callbacks on destruction to prevent memory leaks
        timerHandler.removeCallbacks(timerRunnable);
    }
}
