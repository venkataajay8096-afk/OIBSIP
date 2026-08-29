// ===== QuizApp Web Preview - JavaScript =====
// This mirrors the Android QuizActivity logic exactly

// ===== Question Data (Same 10 questions as QuestionData.java) =====
const QUESTIONS = [
    {
        question: "What is the capital city of Japan?",
        options: ["Beijing", "Seoul", "Tokyo", "Bangkok"],
        correctIndex: 2
    },
    {
        question: "Which planet is known as the Red Planet?",
        options: ["Venus", "Mars", "Jupiter", "Saturn"],
        correctIndex: 1
    },
    {
        question: "What is the largest ocean on Earth?",
        options: ["Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"],
        correctIndex: 3
    },
    {
        question: "Who wrote the play 'Romeo and Juliet'?",
        options: ["William Shakespeare", "Charles Dickens", "Mark Twain", "Jane Austen"],
        correctIndex: 0
    },
    {
        question: "What is the chemical symbol for Gold?",
        options: ["Go", "Gd", "Au", "Ag"],
        correctIndex: 2
    },
    {
        question: "Which country is known as the Land of the Rising Sun?",
        options: ["China", "Japan", "South Korea", "Thailand"],
        correctIndex: 1
    },
    {
        question: "How many continents are there on Earth?",
        options: ["5", "6", "7", "8"],
        correctIndex: 2
    },
    {
        question: "What is the hardest natural substance on Earth?",
        options: ["Gold", "Iron", "Diamond", "Platinum"],
        correctIndex: 2
    },
    {
        question: "Which organ in the human body is responsible for pumping blood?",
        options: ["Liver", "Lungs", "Brain", "Heart"],
        correctIndex: 3
    },
    {
        question: "What is the tallest mountain in the world?",
        options: ["K2", "Mount Kilimanjaro", "Mount Everest", "Mount Fuji"],
        correctIndex: 2
    }
];

// ===== Quiz State =====
let questionList = [];       // Shuffled copy of questions
let currentQuestionIndex = 0;
let score = 0;
let answered = false;
let totalQuestions = 0;

// ===== Screen Management =====
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    document.getElementById(screenId).classList.add('active');
}

// ===== Shuffle Function (same as Collections.shuffle in Java) =====
function shuffleArray(array) {
    const shuffled = [...array]; // Create a copy (don't modify original)
    for (let i = shuffled.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
}

// ===== Start Quiz =====
function startQuiz() {
    // Reset state
    currentQuestionIndex = 0;
    score = 0;
    answered = false;

    // Shuffle questions (new copy each time)
    questionList = shuffleArray(QUESTIONS);
    totalQuestions = questionList.length;

    // Show quiz screen and display first question
    showScreen('quizScreen');
    displayQuestion();
}

// ===== Display Current Question =====
function displayQuestion() {
    // Reset answered flag
    answered = false;

    const question = questionList[currentQuestionIndex];

    // Set question text
    document.getElementById('questionText').textContent = question.question;

    // Set option texts
    const letters = ['A', 'B', 'C', 'D'];
    for (let i = 0; i < 4; i++) {
        const optionEl = document.getElementById('option' + i);
        const optionTextEl = document.getElementById('optionText' + i);

        // Reset classes
        optionEl.className = 'option';
        optionTextEl.textContent = question.options[i];
    }

    // Update counter
    document.getElementById('questionCounter').textContent =
        `Question ${currentQuestionIndex + 1} of ${totalQuestions}`;

    // Update progress bar
    const progress = ((currentQuestionIndex + 1) / totalQuestions) * 100;
    document.getElementById('progressBar').style.width = progress + '%';

    // Update button text for last question
    const btnNext = document.getElementById('btnNext');
    if (currentQuestionIndex === totalQuestions - 1) {
        btnNext.textContent = 'Finish';
    } else {
        btnNext.textContent = 'Next';
    }
}

// ===== Select Answer =====
function selectAnswer(selectedIndex) {
    // Prevent answering the same question multiple times
    if (answered) return;

    // Mark as answered
    answered = true;

    const question = questionList[currentQuestionIndex];
    const correctIndex = question.correctIndex;

    // Disable all options
    for (let i = 0; i < 4; i++) {
        document.getElementById('option' + i).classList.add('disabled');
    }

    if (selectedIndex === correctIndex) {
        // CORRECT: highlight in green
        document.getElementById('option' + selectedIndex).classList.add('correct');
        score++;
    } else {
        // WRONG: highlight selected in red, show correct in green
        document.getElementById('option' + selectedIndex).classList.add('wrong');
        document.getElementById('option' + correctIndex).classList.add('correct');
    }
}

// ===== Next Question =====
function nextQuestion() {
    // If not answered, show toast
    if (!answered) {
        showToast();
        return;
    }

    currentQuestionIndex++;

    if (currentQuestionIndex < totalQuestions) {
        displayQuestion();
    } else {
        showResults();
    }
}

// ===== Show Toast =====
function showToast() {
    const toast = document.getElementById('toast');
    toast.classList.remove('show');
    // Force reflow to restart animation
    void toast.offsetWidth;
    toast.classList.add('show');
}

// ===== Show Results =====
function showResults() {
    const incorrect = totalQuestions - score;
    const percentage = (score / totalQuestions) * 100;

    document.getElementById('scoreDisplay').textContent = `Score: ${score} / ${totalQuestions}`;
    document.getElementById('correctCount').textContent = score;
    document.getElementById('incorrectCount').textContent = incorrect;

    // Motivational message
    let message;
    if (percentage >= 80) {
        message = "🎉 Excellent! Outstanding performance!";
    } else if (percentage >= 60) {
        message = "👍 Good job! Well done!";
    } else if (percentage >= 40) {
        message = "📚 Keep learning! You can do better!";
    } else {
        message = "💪 Don't give up! Try again!";
    }
    document.getElementById('resultMessage').textContent = message;

    showScreen('resultScreen');
}

// ===== Restart Quiz =====
function restartQuiz() {
    startQuiz();
}
