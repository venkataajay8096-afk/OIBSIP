package com.oasis.quizapp;

/**
 * Model class representing a single quiz question.
 * Each question has the question text, four answer options,
 * and the index (0-3) of the correct answer.
 */
public class Question {

    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int correctAnswerIndex; // 0 = option1, 1 = option2, 2 = option3, 3 = option4

    /**
     * Constructor to create a Question object.
     *
     * @param questionText     The question text
     * @param option1          First answer option
     * @param option2          Second answer option
     * @param option3          Third answer option
     * @param option4          Fourth answer option
     * @param correctAnswerIndex Index of the correct answer (0-3)
     */
    public Question(String questionText, String option1, String option2,
                    String option3, String option4, int correctAnswerIndex) {
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    // Getter methods
    public String getQuestionText() {
        return questionText;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    /**
     * Returns the correct answer text based on the correct answer index.
     */
    public String getCorrectAnswerText() {
        switch (correctAnswerIndex) {
            case 0: return option1;
            case 1: return option2;
            case 2: return option3;
            case 3: return option4;
            default: return "";
        }
    }
}
