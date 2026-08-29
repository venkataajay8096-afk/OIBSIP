# Quiz Application

## Description

A clean and modern Android Quiz Application that tests users on General Knowledge with 10 multiple-choice questions. The app features immediate answer feedback (green for correct, red for wrong), score tracking, shuffled questions, and a detailed results screen.

## Task

**OASIS INFOBYTE** — Android App Development — **Task 4**

## Technologies Used

* Android Studio
* Java
* XML
* Android SDK (API 21 - 34)

## Features

* **Welcome screen** — Displays quiz title, description, and Start Quiz button
* **10+ multiple-choice questions** — General Knowledge topics
* **Four answer options** — Easy-to-select styled buttons
* **Immediate answer feedback** — Green for correct, red for wrong answers
* **Question counter** — Shows "Question X of 10" with progress bar
* **Score tracking** — +1 for correct, 0 for incorrect, no double-counting
* **Results screen** — Total score, correct/incorrect counts, motivational message
* **Restart quiz** — Resets everything and starts fresh
* **Shuffled questions** — Random question order on each start/restart
* **Error handling** — Handles rapid tapping, no-answer-selected, and screen rotation

## Project Structure

```
Quiz Application/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/oasis/quizapp/
│       │   ├── MainActivity.java      (Welcome Screen)
│       │   ├── QuizActivity.java      (Question Screen)
│       │   ├── ResultActivity.java    (Results Screen)
│       │   ├── Question.java          (Question Model)
│       │   └── QuestionData.java      (Question Data - 10 Questions)
│       └── res/
│           ├── drawable/              (Button & option backgrounds)
│           ├── layout/
│           │   ├── activity_main.xml   (Welcome layout)
│           │   ├── activity_quiz.xml   (Quiz layout)
│           │   └── activity_result.xml (Result layout)
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── themes.xml
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## How to Run

1. Open **Android Studio**.
2. Click **File → Open** and select the `Quiz Application` project folder.
3. Wait for Gradle to sync and build.
4. Connect an Android device or start an Android emulator.
5. Click the **Run** button (green play icon) or press `Shift + F10`.
6. The app will install and open on the device/emulator.

## App Flow

```
Welcome Screen → Start Quiz → Question 1 → Question 2 → ... → Question 10 → Results Screen → Restart Quiz → Question 1
```

## How Score Calculation Works

- Each correct answer adds **1 point** to the score.
- Wrong answers add **0 points**.
- Once an answer is selected, it is **locked** — tapping again does nothing.
- The `answered` boolean flag prevents double-counting on rapid taps.
- Final score is passed to the Results screen via **Android Intent extras**.

## How Answer Feedback Works

- **Correct answer selected**: The selected option turns **green** (background + text).
- **Wrong answer selected**: The selected option turns **red**, and the correct answer is highlighted in **green**.
- After feedback, all options are **disabled** until the user presses Next.

## How Question Shuffling Works

- `QuestionData.getQuestions()` always returns a **fresh ArrayList** (new copy).
- `Collections.shuffle(questionList)` is called in `QuizActivity.onCreate()`.
- The original question data is **never modified**.
- Each quiz start/restart produces a **different question order**.

## Testing

The following test cases were verified:

| Test | Description | Status |
|------|-------------|--------|
| 1 | Open app → Welcome screen appears | ✅ |
| 2 | Tap Start Quiz → Question 1 appears | ✅ |
| 3 | Question counter displays "Question 1 of 10" | ✅ |
| 4 | Select correct answer → green highlight | ✅ |
| 5 | Select wrong answer → red + correct shown in green | ✅ |
| 6 | Tap Next → next question, previous state cleared | ✅ |
| 7 | Complete all 10 questions → Results screen appears | ✅ |
| 8 | Score, correct count, incorrect count displayed | ✅ |
| 9 | Tap Restart → quiz starts from Question 1, score resets | ✅ |
| 10 | Rapid tapping → no crash, no incorrect score | ✅ |

## Screenshots

<!-- Add screenshots here after running the app -->

| Screen | Screenshot |
|--------|-----------|
| Welcome Screen | ![Welcome](screenshots/01_WelcomeScreen.png) |
| Question Screen | ![Question](screenshots/02_QuestionScreen.png) |
| Correct Answer | ![Correct](screenshots/03_CorrectAnswer.png) |
| Wrong Answer | ![Wrong](screenshots/04_WrongAnswer.png) |
| Next Question | ![Next](screenshots/05_NextQuestion.png) |
| Results Screen | ![Results](screenshots/06_ResultsScreen.png) |
| Restart Quiz | ![Restart](screenshots/07_RestartQuiz.png) |

## Author

OASIS INFOBYTE Internship — Android App Development
