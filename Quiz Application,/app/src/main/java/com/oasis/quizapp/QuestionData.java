package com.oasis.quizapp;

import java.util.ArrayList;

/**
 * This class provides the quiz questions data.
 * All 10 General Knowledge questions are stored here in a simple ArrayList.
 * The original data is never modified — a new copy is created each time.
 */
public class QuestionData {

    /**
     * Returns a new ArrayList containing all 10 quiz questions.
     * Each time this method is called, a fresh copy is returned,
     * so shuffling one list does not affect future calls.
     *
     * @return ArrayList of Question objects
     */
    public static ArrayList<Question> getQuestions() {
        ArrayList<Question> questions = new ArrayList<>();

        // Question 1
        questions.add(new Question(
                "What is the capital city of Japan?",
                "Beijing",
                "Seoul",
                "Tokyo",
                "Bangkok",
                2 // Tokyo
        ));

        // Question 2
        questions.add(new Question(
                "Which planet is known as the Red Planet?",
                "Venus",
                "Mars",
                "Jupiter",
                "Saturn",
                1 // Mars
        ));

        // Question 3
        questions.add(new Question(
                "What is the largest ocean on Earth?",
                "Atlantic Ocean",
                "Indian Ocean",
                "Arctic Ocean",
                "Pacific Ocean",
                3 // Pacific Ocean
        ));

        // Question 4
        questions.add(new Question(
                "Who wrote the play 'Romeo and Juliet'?",
                "William Shakespeare",
                "Charles Dickens",
                "Mark Twain",
                "Jane Austen",
                0 // William Shakespeare
        ));

        // Question 5
        questions.add(new Question(
                "What is the chemical symbol for Gold?",
                "Go",
                "Gd",
                "Au",
                "Ag",
                2 // Au
        ));

        // Question 6
        questions.add(new Question(
                "Which country is known as the Land of the Rising Sun?",
                "China",
                "Japan",
                "South Korea",
                "Thailand",
                1 // Japan
        ));

        // Question 7
        questions.add(new Question(
                "How many continents are there on Earth?",
                "5",
                "6",
                "7",
                "8",
                2 // 7
        ));

        // Question 8
        questions.add(new Question(
                "What is the hardest natural substance on Earth?",
                "Gold",
                "Iron",
                "Diamond",
                "Platinum",
                2 // Diamond
        ));

        // Question 9
        questions.add(new Question(
                "Which organ in the human body is responsible for pumping blood?",
                "Liver",
                "Lungs",
                "Brain",
                "Heart",
                3 // Heart
        ));

        // Question 10
        questions.add(new Question(
                "What is the tallest mountain in the world?",
                "K2",
                "Mount Kilimanjaro",
                "Mount Everest",
                "Mount Fuji",
                2 // Mount Everest
        ));

        return questions;
    }
}
