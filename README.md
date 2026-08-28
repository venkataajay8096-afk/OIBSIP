# Task 3 - Calculator 📱

> **Oasis Infobyte Virtual Internship Program (OIBSIP)**  
> **Track**: Android App Development  
> **Task**: Task 3 - Calculator  
> **Author**: [Your Full Name]

---

## 📌 Project Overview

This repository contains a simple, clean, and professional **Mobile Calculator Application** developed as part of the **Oasis Infobyte (OIBSIP)** internship task. The application is built using native **Java** for core mathematical evaluation logic and **XML** for modern, responsive UI design in **Android Studio**.

The app includes advanced features like division-by-zero protection (`Error`), single-dot decimal validation, operator auto-replacement, and robust error handling to ensure zero application crashes even under rapid tapping.

---

## ✨ Features & Requirements Implemented

- 🔢 **Full Keypad**: Buttons for numbers `0` through `9`.
- ➕ **Basic Arithmetic**: Support for addition (`+`), subtraction (`−`), multiplication (`×`), division (`÷`), and percentage (`%`).
- 🧮 **Expression & Result Screen**: Dual display (`tvExpression` for active inputs, `tvResult` for final outputs).
- 🔴 **Division-by-Zero Handling**: Computations like `10 ÷ 0` yield `"Error"` safely without crashing the app.
- 🧹 **Clear & Backspace**:
  - `C` (Clear) resets the current calculation state and display.
  - `⌫` (Backspace) deletes the last entered character.
- 🎯 **Smart Input Validation**:
  - Blocks multiple decimal points in a single number operand (e.g. `5.5.5` is prevented).
  - Automatically replaces duplicate operators (e.g. pressing `+` then `×` updates to `×`).
  - Strips trailing operators before evaluating expressions upon pressing `=`.
- 🎨 **Responsive UI Design**: Built with `ConstraintLayout` and `GridLayout` using custom rounded ripple drawables (`btn_number.xml`, `btn_operator.xml`, `btn_action.xml`).

---

## 🛠️ Tech Stack & Tools

- **Language**: Java 8+
- **UI Framework**: Android XML (`ConstraintLayout`, `GridLayout`)
- **IDE**: Android Studio
- **Compile SDK**: 34 (Android 14)
- **Minimum SDK**: 21 (Android 5.0 Lollipop)
- **Architecture**: Single Activity (`MainActivity.java`) with zero-dependency Shunting-yard expression parsing.

---

## 📁 Repository Structure

```
Calculator/
├── OIBSIP/
│   └── Android-Task3-Calculator/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/oasis/calculator/
│           │       └── MainActivity.java
│           └── res/
│               ├── drawable/
│               │   ├── btn_action.xml
│               │   ├── btn_clear.xml
│               │   ├── btn_number.xml
│               │   ├── btn_operator.xml
│               │   └── display_background.xml
│               ├── layout/
│               │   └── activity_main.xml
│               └── values/
│                   ├── colors.xml
│                   ├── strings.xml
│                   └── themes.xml
├── web/                             # Web Preview Demo (HTML/CSS/JS)
│   ├── index.html
│   ├── styles.css
│   └── script.js
├── screenshots/                     # App Screenshots
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## 🧪 Verification & Test Cases

The calculator logic has been thoroughly verified against all required test scenarios:

| Test Case | Math Expression | Expected Result | Verified Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **Addition** | `5 + 3` | `8` | `8` | ✅ **PASS** |
| **Subtraction** | `10 − 4` | `6` | `6` | ✅ **PASS** |
| **Multiplication** | `6 × 7` | `42` | `42` | ✅ **PASS** |
| **Division** | `20 ÷ 5` | `4` | `4` | ✅ **PASS** |
| **Decimal Addition** | `5.5 + 2.5` | `8` | `8` | ✅ **PASS** |
| **Division by Zero** | `10 ÷ 0` | `Error` | `Error` | ✅ **PASS** |
| **Reset Display** | `Clear (C)` | Resets display | Resets to `0` | ✅ **PASS** |
| **Delete Last Char** | `Backspace (⌫)` | Removes last char | Removes last char | ✅ **PASS** |
| **Rapid Tapping** | Multiple fast clicks | No crashes | Stable | ✅ **PASS** |

---

## 🚀 How to Run in Android Studio

1. **Clone or Download** this repository:
   ```bash
   git clone <your-repository-url>
   ```
2. **Open in Android Studio**:
   - Open Android Studio -> Click **File** -> **Open...**
   - Select the `Calculator` folder directory.
3. **Sync Gradle**: Allow Android Studio to sync the Gradle build files.
4. **Run Application**: Connect an Android device or start an Emulator and click **Run (Shift + F10)**.

---

## 🌐 Web Interactive Preview

An interactive web version matching the Android app UI is hosted locally at:
- **Local Web Link**: [http://localhost:8080](http://localhost:8080)
- **Direct File**: [`web/index.html`](file:///d:/Calculator/web/index.html)

---

## 📜 License & Acknowledgements

Created for the **Oasis Infobyte Virtual Internship Program (OIBSIP)** under the **Android App Development** domain.
