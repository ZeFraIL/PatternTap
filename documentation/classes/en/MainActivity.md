# Class Description: MainActivity

## 1. General Information
*   **Class Name:** `MainActivity`
*   **Type:** `Activity`
*   **Assigning a class in an application:** This is the heart of the "PatternTap" application. It acts as the "Director" or "Controller" of the game. It handles everything from setting up the visual grid and buttons to managing the game logic (generating patterns, checking user input, and tracking the score).
*   **How it interacts with other components:** As the only Activity in this project, it interacts with the Android System to manage its lifecycle and uses internal components like `Handler` for timing and `Random` for game unpredictability.

---

## 2. Variables (Class Fields)

| Name | Type | Purpose | Where is it used |
| :--- | :--- | :--- | :--- |
| `gridLayout` | `GridLayout` | Container for the game buttons. | `setupGrid()`, `onCreate()` |
| `scoreTextView` | `TextView` | Displays the current score to the user. | `updateScore()`, `onCreate()` |
| `hintButton`, `restartButton`, `stopButton` | `Button` | Controls to manage game state. | `onCreate()` |
| `speedSeekBar` | `SeekBar` | Slider to adjust game speed. | `onCreate()` |
| `buttons` | `List<Button>` | Stores references to all buttons in the grid. | `setupGrid()`, `waveRunnable`, `stopGame()` |
| `pattern` | `List<Integer>` | Stores the sequence of button indices to follow. | `generateNewPattern()`, `waveRunnable`, `onClick()`, `showHint()` |
| `waveIndex` | `int` | Tracks the current step in the pattern wave. | `waveRunnable`, `onClick()`, `showHint()`, `startNewRound()` |
| `score` | `int` | Holds the user's current points. | `onClick()`, `restartGame()`, `generateNewPattern()`, `waveRunnable` |
| `gridSize` | `int` | Determines the dimensions of the grid (e.g., 3 means 3x3). | `setupGrid()`, `generateNewPattern()`, `waveRunnable`, `showHint()` |
| `handler` | `Handler` | Used to schedule delayed actions (like highlighting buttons). | `waveRunnable`, `startNewRound()`, `stopGame()` |
| `random` | `Random` | Generates random numbers for the pattern. | `generateNewPattern()` |
| `waveSpeed` | `long` | Delay in milliseconds between pattern steps. | `waveRunnable`, `onCreate()`, `onProgressChanged()` |
| `playerHasGuessedForCurrentStep` | `boolean` | Flag to check if the player clicked the right button in time. | `waveRunnable`, `onClick()`, `startNewRound()` |
| `isGameActive` | `boolean` | Flag to check if the game is currently running. | `restartGame()`, `stopGame()`, `waveRunnable`, `onClick()`, `showHint()` |

---

## 3. Classroom Methods

### Method Name: `onCreate(Bundle savedInstanceState)`
*   **Type:** `protected`
*   **Return value:** `void`
*   **Parameters:** `savedInstanceState` (`Bundle`) - Saved state for reconstruction.
*   **What it does:** 
    1.  Sets up the visual layout.
    2.  Initializes UI elements (buttons, seekbar, labels).
    3.  Sets up listeners for buttons and the speed slider.
    4.  Calls `setupGrid()` to prepare the game board.
*   **When called:** Automatically by the Android system when the app starts.
*   **Importance:** This is the entry point. If a component isn't initialized here, the app will crash with a `NullPointerException`.

### Method Name: `setupGrid()`
*   **Type:** `private`
*   **Return value:** `void`
*   **Parameters:** None
*   **What it does:** 
    1.  Clears existing buttons from `gridLayout`.
    2.  Sets the row and column count based on `gridSize`.
    3.  Creates new `Button` objects in a loop, sets their size, margins, and tags (0 to N).
    4.  Adds each button to the `buttons` list and the layout.
*   **When called:** When the app starts, when the game is restarted, or when the player levels up.
*   **Importance:** It dynamically builds the UI. Using tags is crucial because it helps identify which button was clicked later.

### Method Name: `startNewRound()`
*   **Type:** `private`
*   **Return value:** `void`
*   **Parameters:** None
*   **What it does:** 
    1.  Generates a new random pattern.
    2.  Resets the `waveIndex`.
    3.  Triggers the first step of the animation wave using `handler`.
*   **When called:** When the "Restart" button is clicked or a previous pattern sequence finishes.

### Method Name: `waveRunnable` (Logic in `run()`)
*   **Type:** `private final Runnable`
*   **Return value:** `void`
*   **What it does:** 
    1.  Checks if the player missed the previous step ("Too slow!").
    2.  Resets all button colors to light gray.
    3.  Checks for level-up (every 5 points) or win condition (25 points).
    4.  Highlights the next button in the `pattern` with yellow.
    5.  Schedules itself to run again after `waveSpeed` milliseconds.
*   **When called:** Repeatedly via `handler.postDelayed` while the game is active.
*   **Importance:** This is the "Heartbeat" of the game. It controls the tempo.

### Method Name: `onClick(View v)`
*   **Type:** `public`
*   **Return value:** `void`
*   **Parameters:** `v` (`View`) - The specific button that was clicked.
*   **What it does:** 
    1.  Validates if a click is allowed (game must be active and wave must be in progress).
    2.  Compares the clicked button's tag with the expected index from `pattern`.
    3.  If correct: Increases score, updates UI, and marks the step as guessed (turns button Green).
    4.  If wrong: Turns button Red and shows a "Wrong!" message.
*   **When called:** Whenever a grid button is tapped.

---

## 4. Lifecycle (Activity)
*   **`onCreate()`**: Called when the Activity is first created. It initializes everything.
*   **Note**: Other methods like `onPause()` or `onDestroy()` are not explicitly overridden, meaning the game doesn't automatically stop if you minimize the app (this could be a point for improvement!).

---

## 5. Interface Interaction (UI)
*   **Elements:** `GridLayout` (board), `TextView` (score/labels), `Button` (controls), `SeekBar` (speed).
*   **Relation:** Initialized via `findViewById(R.id.id_name)`.
*   **Events:** 
    *   `setOnClickListener`: Used for game control buttons and the grid buttons.
    *   `OnSeekBarChangeListener`: Used to update `waveSpeed` in real-time as the slider moves.

---

## 6. Interaction with other components
*   **Intents:** Not used (single screen app).
*   **Data Transfer:** All data stays within the `MainActivity` instance.

---

## 7. General Logic of the Class
The class follows a loop: **Generate -> Show -> Check -> Repeat**.
1.  **Start**: Player hits Restart. `setupGrid()` builds the UI.
2.  **Wave**: A `Runnable` runs on a timer. It picks a button from a random list and highlights it yellow.
3.  **Interaction**: The player must tap that button before the `Runnable` moves to the next step.
4.  **Feedback**: The app gives visual (colors) and text (Toast) feedback.
5.  **Progression**: Every 5 correct answers, the grid gets bigger, making it harder.

---

## 8. Simplified Explanation
**Imagine a lighthouse.** 
A lighthouse keeper (the `Runnable`) flashes a light on different rocks in a specific order. You are a sailor who must point to the same rock the keeper just lit up. If you are too slow, the keeper moves to the next rock and you miss your chance. As you get better, the keeper flashes the light faster, and eventually, more rocks are added to the sea!

**Real-world analogy:** 
It's like the classic game "Simon Says," but instead of remembering a long sequence and repeating it at the end, you have to "catch" the signal while it's happening.

---
**Advice for defense:**
*   Be ready to explain how `Handler` works (it's like a kitchen timer for code).
*   Explain why we use `v.getTag()` in `onClick` (it's a way to give each button an "ID number" so we know which one is which).
*   **Improvement Suggestion**: Point out that the `waveRunnable` keeps running even if the app is in the background. A better practice would be to stop the handler in `onStop()`.
