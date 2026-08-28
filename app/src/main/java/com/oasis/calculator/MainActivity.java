package com.oasis.calculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * MainActivity handles all calculator operations, user interactions,
 * expression building, parsing, and arithmetic calculations for the application.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Views for display
    private TextView tvExpression;
    private TextView tvResult;

    // String builder to maintain current expression state
    private StringBuilder currentExpression = new StringBuilder();

    // Flag to track if calculation was performed (after '=' is pressed)
    private boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initializeViews();
    }

    /**
     * Finds and assigns views, and sets click listeners for all calculator buttons.
     */
    private void initializeViews() {
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        // Array of all button IDs to attach click listeners cleanly
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply,
                R.id.btnDivide, R.id.btnPercent, R.id.btnClear, R.id.btnBackspace,
                R.id.btnEquals
        };

        for (int id : buttonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnClear) {
            onClearPressed();
        } else if (id == R.id.btnBackspace) {
            onBackspacePressed();
        } else if (id == R.id.btnEquals) {
            onEqualsPressed();
        } else if (id == R.id.btnDot) {
            onDotPressed();
        } else if (id == R.id.btnAdd || id == R.id.btnSubtract ||
                   id == R.id.btnMultiply || id == R.id.btnDivide ||
                   id == R.id.btnPercent) {
            Button btn = (Button) v;
            onOperatorPressed(btn.getText().toString());
        } else {
            // Number buttons 0-9
            Button btn = (Button) v;
            onNumberPressed(btn.getText().toString());
        }
    }

    /**
     * Handles number button taps (0 to 9).
     */
    private void onNumberPressed(String number) {
        // If a final result was just displayed, start a fresh calculation
        if (isResultDisplayed) {
            currentExpression.setLength(0);
            isResultDisplayed = false;
        }

        currentExpression.append(number);
        updateDisplay();
    }

    /**
     * Handles decimal point (.) taps with validation to prevent multiple dots in one number.
     */
    private void onDotPressed() {
        if (isResultDisplayed) {
            currentExpression.setLength(0);
            currentExpression.append("0");
            isResultDisplayed = false;
        }

        if (currentExpression.length() == 0) {
            currentExpression.append("0.");
            updateDisplay();
            return;
        }

        // Get the current operand (text after the last operator)
        String currentOperand = getCurrentOperand();

        // Prevent adding multiple decimal points in a single number
        if (!currentOperand.contains(".")) {
            char lastChar = currentExpression.charAt(currentExpression.length() - 1);
            if (isOperatorChar(lastChar)) {
                currentExpression.append("0.");
            } else {
                currentExpression.append(".");
            }
            updateDisplay();
        }
    }

    /**
     * Handles operator taps (+, −, ×, ÷, %).
     */
    private void onOperatorPressed(String operator) {
        if (currentExpression.length() == 0) {
            // Allow leading minus for negative numbers
            if (operator.equals("−") || operator.equals("-")) {
                currentExpression.append("−");
                isResultDisplayed = false;
                updateDisplay();
            }
            return;
        }

        // If equal was pressed previously, continue calculations using current result
        if (isResultDisplayed) {
            String lastResultText = tvResult.getText().toString();
            if (!lastResultText.equals(getString(R.string.error_message))) {
                currentExpression.setLength(0);
                currentExpression.append(lastResultText);
            } else {
                currentExpression.setLength(0);
            }
            isResultDisplayed = false;
        }

        if (currentExpression.length() == 0) {
            return;
        }

        char lastChar = currentExpression.charAt(currentExpression.length() - 1);

        // If the last entered character is already an operator, replace it
        if (isOperatorChar(lastChar)) {
            currentExpression.setCharAt(currentExpression.length() - 1, operator.charAt(0));
        } else {
            currentExpression.append(operator);
        }

        updateDisplay();
    }

    /**
     * Handles Clear (C) button: resets calculator expression and output display.
     */
    private void onClearPressed() {
        currentExpression.setLength(0);
        isResultDisplayed = false;
        tvExpression.setText("");
        tvResult.setText("0");
    }

    /**
     * Handles Backspace (⌫) button: removes the last entered character.
     */
    private void onBackspacePressed() {
        if (isResultDisplayed) {
            onClearPressed();
            return;
        }

        if (currentExpression.length() > 0) {
            currentExpression.deleteCharAt(currentExpression.length() - 1);
            updateDisplay();
        }
    }

    /**
     * Handles Equals (=) button: evaluates the mathematical expression.
     */
    private void onEqualsPressed() {
        if (currentExpression.length() == 0) {
            return;
        }

        String expression = currentExpression.toString();

        // If the expression ends with an operator, strip it before evaluating
        while (expression.length() > 0 && isOperatorChar(expression.charAt(expression.length() - 1))) {
            expression = expression.substring(0, expression.length() - 1);
        }

        if (TextUtils.isEmpty(expression)) {
            return;
        }

        tvExpression.setText(currentExpression.toString());

        // Evaluate expression
        String result = evaluateExpression(expression);
        tvResult.setText(result);

        isResultDisplayed = true;
    }

    /**
     * Updates the top display TextView with current expression content.
     */
    private void updateDisplay() {
        if (currentExpression.length() == 0) {
            tvExpression.setText("");
            tvResult.setText("0");
        } else {
            tvExpression.setText(currentExpression.toString());
        }
    }

    /**
     * Helper to extract the last operand from current expression string.
     */
    private String getCurrentOperand() {
        String expr = currentExpression.toString();
        int lastOpIndex = -1;

        for (int i = expr.length() - 1; i >= 0; i--) {
            if (isOperatorChar(expr.charAt(i))) {
                lastOpIndex = i;
                break;
            }
        }

        if (lastOpIndex != -1) {
            return expr.substring(lastOpIndex + 1);
        }
        return expr;
    }

    /**
     * Checks if a character is one of the arithmetic operator symbols.
     */
    private boolean isOperatorChar(char c) {
        return c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '%';
    }

    /**
     * Evaluates math expression string and returns formatted result or "Error".
     * Zero-dependency robust mathematical evaluator using Dijkstra's Shunting-yard algorithm.
     */
    public String evaluateExpression(String expression) {
        try {
            // Tokenize expression into numbers and operators
            List<String> tokens = tokenize(expression);

            if (tokens.isEmpty()) {
                return "0";
            }

            // Convert infix tokens to postfix (RPN) using Shunting-yard algorithm
            List<String> postfix = infixToPostfix(tokens);

            // Evaluate postfix expression
            double resultValue = evaluatePostfix(postfix);

            // Handle NaN or Infinity (e.g. Division by zero)
            if (Double.isNaN(resultValue) || Double.isInfinite(resultValue)) {
                return getString(R.string.error_message);
            }

            // Format output: omit trailing .0 for whole numbers
            if (resultValue == (long) resultValue) {
                return String.valueOf((long) resultValue);
            } else {
                return String.valueOf(resultValue);
            }

        } catch (ArithmeticException e) {
            return getString(R.string.error_message);
        } catch (Exception e) {
            // Catch any unexpected expression syntax errors safely without crashing
            return getString(R.string.error_message);
        }
    }

    /**
     * Tokenizes raw mathematical string into numbers and operator tokens.
     */
    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else if (isOperatorChar(c)) {
                // Check if minus is unary (negative number symbol)
                if ((c == '−' || c == '-') && (i == 0 || isOperatorChar(expr.charAt(i - 1)))) {
                    numberBuffer.append('-');
                } else {
                    if (numberBuffer.length() > 0) {
                        tokens.add(numberBuffer.toString());
                        numberBuffer.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                }
            }
        }

        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }

        return tokens;
    }

    /**
     * Converts Infix notation tokens to Postfix (RPN) tokens based on operator precedence.
     */
    private List<String> infixToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() &&
                        getPrecedence(operatorStack.peek()) >= getPrecedence(token)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        return output;
    }

    /**
     * Evaluates Postfix (RPN) tokens and returns final double result.
     * Throws ArithmeticException if division by zero occurs.
     */
    private double evaluatePostfix(List<String> postfix) throws ArithmeticException {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    // Handle unary percent or single operator edge case
                    if (token.equals("%") && stack.size() == 1) {
                        double val = stack.pop();
                        stack.push(val / 100.0);
                        continue;
                    }
                    throw new IllegalArgumentException("Invalid expression");
                }

                double b = stack.pop();
                double a = stack.pop();

                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "−":
                    case "-":
                        stack.push(a - b);
                        break;
                    case "×":
                    case "*":
                        stack.push(a * b);
                        break;
                    case "÷":
                    case "/":
                        if (Math.abs(b) < 1e-12) {
                            throw new ArithmeticException("Division by zero");
                        }
                        stack.push(a / b);
                        break;
                    case "%":
                        if (Math.abs(b) < 1e-12) {
                            throw new ArithmeticException("Modulo by zero");
                        }
                        stack.push(a % b);
                        break;
                }
            }
        }

        return stack.isEmpty() ? 0.0 : stack.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return token.length() == 1 && isOperatorChar(token.charAt(0));
    }

    private int getPrecedence(String op) {
        switch (op) {
            case "+":
            case "−":
            case "-":
                return 1;
            case "×":
            case "*":
            case "÷":
            case "/":
            case "%":
                return 2;
            default:
                return 0;
        }
    }
}
