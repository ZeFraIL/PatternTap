# 📱 Android Application Documentation: PatternTap

## 🧾 General Information
**Project Name:** PatternTap  
**Author(s):** Zeev Fraiman  
**Date:** February 2025  
**Language:** Java  
**Development Environment:** Android Studio  
**Android Version (minSdk / targetSdk):** 28 / 36  

---

## 🎯 Project Goal
• **What problem does the app solve:** Enhances visual memory and reaction time through an interactive game.  
• **Why is this task important:** Gamified cognitive training helps maintain brain activity and focus.  
• **Target Audience:** Users of all ages looking to improve their concentration and memory skills.  

---

## 📌 Application Requirements
### Functional Requirements
• Generate random sequences (patterns) on a grid.  
• Dynamically change grid size (from 3x3 to 5x5) based on score.  
• Adjustable game speed via a SeekBar.  
• Hint system and real-time score tracking.  

### Non-Functional Requirements
• **Performance:** Smooth button highlighting with no UI lag.  
• **Usability:** Simple, clean interface with intuitive controls.  
• **Reliability:** Stable game loop and prevention of invalid inputs.  

---

## 🧠 General Architecture
• **Approach:** MVC (Model-View-Controller).  
• **Why this was chosen:** For a small-scale game, this approach allows for rapid development where MainActivity manages the state (Model) and updates the UI (View).  
• **Main Components:**  
  – `MainActivity`: Handles lifecycle and core logic.  
  – `GridLayout`: Dynamically displays the game board.  
  – `Handler`: Manages game timing and intervals.  

---

## 🧩 UML Diagram
`[MainActivity] –> [Handler] (Timing control)`  
`[MainActivity] –> [GridLayout] (Button rendering)`  
`[MainActivity] –> [Random] (Pattern generation)`  

**Package Description:**  
The project uses a flat package structure (`zeev.fraiman.patterntap`), simplifying navigation for a compact application. For future scaling, packages like `.ui`, `.logic`, and `.data` can be introduced.

---

## 🧩 Detailed Class Description
### 📌 Class: MainActivity
**Role:** Main game controller.  
**Responsibility:** Initializing UI, handling touch events, managing the game loop, and score tracking.  
**Main Methods:**  
- `onCreate()` — Sets up UI and listeners.  
- `setupGrid()` — Dynamically creates buttons in the grid.  
- `startNewRound()` — Initiates a new wave of patterns.  
- `waveRunnable` — The main loop for animation and timing checks.  
- `onClick()` — Processes user input and compares it with the pattern.  
**Interaction with other classes:** Uses `Handler` for scheduling tasks and `Random` for logic.

---

## 🔄 App Workflow (Scenario)
1. User taps "Restart".  
2. The system generates a random sequence of buttons.  
3. The `Handler` highlights buttons yellow one by one.  
4. The user must tap the highlighted button before the next one appears.  
5. Correct taps increase the score; misses or slow reactions trigger a notification.  
6. Reaching score thresholds increases the difficulty (grid size).

---

## 🎨 UI/UX Analysis
• **Interface Design:** Focused on the game grid to minimize distractions.  
• **Principles Used:**  
  – **Simplicity:** No unnecessary UI elements.  
  – **Logic:** Color feedback (green for success, red for error).  
  – **Accessibility:** Large buttons for easy interaction.  
• **Improvements:** Add dark mode support and sound effects.

---

## ⚙️ Threading
• **Tools:** `Handler` (with `Looper.getMainLooper()`).  
• **Why this way:** Standard Android mechanism for UI-thread delays without freezing the interface.  
• **Prevention:**  
  – **ANR:** All logic is lightweight.  
  – **Memory Leaks:** `handler.removeCallbacks()` is called when the game stops.

---

## 💾 Data Management
• **Storage:** In-memory (class variables).  
• **Why this way:** Data is session-based and doesn't require persistence at this stage.  
• **Integrity:** State is reset upon restarting the game.

---

## 🌐 Networking
• No network interaction in the current version. The app is fully offline.

---

## 🔐 Security
• No sensitive data is handled. Uses standard Android permissions.

---

## 🧪 Testing
• **Unit Tests:** Logic for pattern generation (ensuring no duplicates).  
• **UI Tests:** Verifying grid layout changes during level-ups.

---

## 🐞 Error Handling
• Handles "Too slow" input via the `playerHasGuessedForCurrentStep` flag.  
• Validates `isGameActive` state before processing clicks.

---

## ⚡ Performance
• Optimized by using `removeAllViews()` and rebuilding the grid only when the level changes.  
• Very low resource footprint.

---

## 🚀 Expansion Opportunities
• Global leaderboards via Firebase.  
• Multiple game modes (Time Trial, Endurance).  
• Localization into more languages.
