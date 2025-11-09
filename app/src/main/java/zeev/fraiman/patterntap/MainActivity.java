package zeev.fraiman.patterntap;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GridLayout gridLayout;
    private TextView scoreTextView;
    private Button hintButton, restartButton, stopButton;
    private SeekBar speedSeekBar;
    private TextView speedLabelTextView;
    private final List<Button> buttons = new ArrayList<>();
    private final List<Integer> pattern = new ArrayList<>();
    private int waveIndex = 0; // Tracks the wave's position in the pattern
    private int score = 0;
    private int gridSize = 3;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private long waveSpeed = 1000; // milliseconds
    private boolean playerHasGuessedForCurrentStep = false;
    private boolean isGameActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scoreTextView = findViewById(R.id.scoreTextView);
        gridLayout = findViewById(R.id.gridLayout);
        hintButton = findViewById(R.id.hintButton);
        restartButton = findViewById(R.id.restartButton);
        stopButton = findViewById(R.id.stopButton);
        speedSeekBar = findViewById(R.id.speedSeekBar);
        speedLabelTextView = findViewById(R.id.speedLabelTextView);

        waveSpeed = 1750 - speedSeekBar.getProgress();
        speedLabelTextView.setText("Speed: " + speedSeekBar.getProgress());


        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveSpeed = 1750 - progress; // Higher progress -> lower delay -> faster wave
                speedLabelTextView.setText("Speed: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });


        hintButton.setOnClickListener(v -> showHint());
        restartButton.setOnClickListener(v -> restartGame());
        stopButton.setOnClickListener(v -> stopGame());

        setupGrid();
        Toast.makeText(this, "Press Restart to begin!", Toast.LENGTH_LONG).show();
    }

    private void stopGame() {
        if (!isGameActive) return;
        isGameActive = false;
        handler.removeCallbacks(waveRunnable);
        for (Button btn : buttons) {
            btn.setBackgroundColor(Color.LTGRAY);
        }
        Toast.makeText(this, "Game stopped. Press Restart to play again.", Toast.LENGTH_SHORT).show();
    }

    private void restartGame() {
        isGameActive = true;
        score = 0;
        gridSize = 3;
        updateScore();
        setupGrid();
        startNewRound();
    }

    private void startNewRound() {
        if (!isGameActive) return;
        generateNewPattern();
        waveIndex = 0;
        playerHasGuessedForCurrentStep = true; // No guess needed for the first step
        handler.removeCallbacks(waveRunnable);
        handler.post(waveRunnable);
    }

    private void setupGrid() {
        gridLayout.removeAllViews();
        buttons.clear();
        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        for (int i = 0; i < gridSize * gridSize; i++) {
            Button button = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);
            button.setTag(i);
            button.setOnClickListener(this);
            button.setBackgroundColor(Color.LTGRAY);
            buttons.add(button);
            gridLayout.addView(button);
        }
    }

    private void generateNewPattern() {
        pattern.clear();
        int patternLength = 3 + score / 5; // Pattern length increases with score
        // Ensure pattern doesn't have consecutive duplicates for clarity
        int lastButton = -1;
        for (int i = 0; i < patternLength; i++) {
            int nextButton;
            do {
                nextButton = random.nextInt(gridSize * gridSize);
            } while (nextButton == lastButton);
            pattern.add(nextButton);
            lastButton = nextButton;
        }
    }

    private final Runnable waveRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameActive) return;

            // 1. Check if player was too slow for the previous step
            if (!playerHasGuessedForCurrentStep) {
                Toast.makeText(MainActivity.this, "Too slow!", Toast.LENGTH_SHORT).show();
            }
            playerHasGuessedForCurrentStep = false; // Reset for the new step

            // 2. Un-highlight all buttons
            for (Button btn : buttons) {
                btn.setBackgroundColor(Color.LTGRAY);
            }

            // 3. Check for level up/win conditions
            if (score > 0 && score % 5 == 0 && gridSize < 5) {
                gridSize++;
                Toast.makeText(MainActivity.this, "Level up! Grid is now " + gridSize + "x" + gridSize, Toast.LENGTH_SHORT).show();
                setupGrid();
                startNewRound();
                return;
            } else if (score >= 25) { // Win condition (5 for each grid size 3,4,5)
                Toast.makeText(MainActivity.this, "You win! Starting over.", Toast.LENGTH_LONG).show();
                restartGame();
                return;
            }

            // 4. If pattern is complete, start a new round
            if (waveIndex >= pattern.size()) {
                startNewRound();
                return;
            }

            // 5. Highlight the current button in the wave
            int currentButtonIndex = pattern.get(waveIndex);
            buttons.get(currentButtonIndex).setBackgroundColor(Color.YELLOW);

            waveIndex++;
            handler.postDelayed(this, waveSpeed);
        }
    };

    @Override
    public void onClick(View v) {
        // A guess is invalid if the game is stopped, the wave hasn't started,
        // is over, or if the player has already correctly guessed the current step.
        if (!isGameActive || waveIndex == 0 || waveIndex > pattern.size() || playerHasGuessedForCurrentStep) {
            return; // Not a valid time to guess
        }

        int clickedIndex = (int) v.getTag();
        int targetIndex = pattern.get(waveIndex - 1);

        if (clickedIndex == targetIndex) {
            // Correct guess
            score++;
            updateScore();
            playerHasGuessedForCurrentStep = true;
            v.setBackgroundColor(Color.GREEN);
        } else {
            // Wrong guess
            v.setBackgroundColor(Color.RED);
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHint() {
        if (!isGameActive || waveIndex >= pattern.size() || waveIndex == 0) return;

        int currentButtonIndex = pattern.get(waveIndex - 1);
        int nextButtonIndex = pattern.get(waveIndex);

        int prevRow = currentButtonIndex / gridSize;
        int prevCol = currentButtonIndex % gridSize;
        int nextRow = nextButtonIndex / gridSize;
        int nextCol = nextButtonIndex % gridSize;

        int rowDiff = nextRow - prevRow;
        int colDiff = nextCol - prevCol;

        StringBuilder sb = new StringBuilder();
        if (rowDiff != 0) {
            sb.append(Math.abs(rowDiff)).append(rowDiff > 0 ? " step(s) down" : " step(s) up");
        }
        if (colDiff != 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(Math.abs(colDiff)).append(colDiff > 0 ? " step(s) right" : " step(s) left");
        }

        Toast.makeText(this, sb.length() > 0 ? sb.toString() : "It's the same button again!", Toast.LENGTH_LONG).show();
    }

    private void updateScore() {
        scoreTextView.setText("Correct Answers: " + score);
    }
}
