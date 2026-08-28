document.addEventListener('DOMContentLoaded', () => {
    const tvExpression = document.getElementById('tvExpression');
    const tvResult = document.getElementById('tvResult');
    const currentTime = document.getElementById('currentTime');

    let currentExpression = '';
    let isResultDisplayed = false;

    // Update status bar time
    function updateClock() {
        const now = new Date();
        const hrs = String(now.getHours()).padStart(2, '0');
        const mins = String(now.getMinutes()).padStart(2, '0');
        if (currentTime) currentTime.textContent = `${hrs}:${mins}`;
    }
    updateClock();
    setInterval(updateClock, 10000);

    // Number buttons
    for (let i = 0; i <= 9; i++) {
        const btn = document.getElementById(`btn${i}`);
        if (btn) {
            btn.addEventListener('click', () => onNumberPressed(String(i)));
        }
    }

    // Dot button
    const btnDot = document.getElementById('btnDot');
    if (btnDot) btnDot.addEventListener('click', onDotPressed);

    // Operator buttons
    const operators = [
        { id: 'btnAdd', op: '+' },
        { id: 'btnSubtract', op: '−' },
        { id: 'btnMultiply', op: '×' },
        { id: 'btnDivide', op: '÷' },
        { id: 'btnPercent', op: '%' }
    ];

    operators.forEach(({ id, op }) => {
        const btn = document.getElementById(id);
        if (btn) btn.addEventListener('click', () => onOperatorPressed(op));
    });

    // Control buttons
    document.getElementById('btnClear').addEventListener('click', onClearPressed);
    document.getElementById('btnBackspace').addEventListener('click', onBackspacePressed);
    document.getElementById('btnEquals').addEventListener('click', onEqualsPressed);

    function onNumberPressed(num) {
        if (isResultDisplayed) {
            currentExpression = '';
            isResultDisplayed = false;
        }
        currentExpression += num;
        updateDisplay();
    }

    function onDotPressed() {
        if (isResultDisplayed) {
            currentExpression = '0';
            isResultDisplayed = false;
        }
        if (currentExpression === '') {
            currentExpression = '0.';
            updateDisplay();
            return;
        }

        const currentOperand = getCurrentOperand();
        if (!currentOperand.includes('.')) {
            const lastChar = currentExpression.slice(-1);
            if (isOperatorChar(lastChar)) {
                currentExpression += '0.';
            } else {
                currentExpression += '.';
            }
            updateDisplay();
        }
    }

    function onOperatorPressed(op) {
        if (currentExpression === '') {
            if (op === '−' || op === '-') {
                currentExpression = '−';
                isResultDisplayed = false;
                updateDisplay();
            }
            return;
        }

        if (isResultDisplayed) {
            const lastRes = tvResult.textContent;
            if (lastRes !== 'Error') {
                currentExpression = lastRes;
            } else {
                currentExpression = '';
            }
            isResultDisplayed = false;
        }

        if (currentExpression === '') return;

        const lastChar = currentExpression.slice(-1);
        if (isOperatorChar(lastChar)) {
            currentExpression = currentExpression.slice(0, -1) + op;
        } else {
            currentExpression += op;
        }
        updateDisplay();
    }

    function onClearPressed() {
        currentExpression = '';
        isResultDisplayed = false;
        tvExpression.textContent = '';
        tvResult.textContent = '0';
    }

    function onBackspacePressed() {
        if (isResultDisplayed) {
            onClearPressed();
            return;
        }
        if (currentExpression.length > 0) {
            currentExpression = currentExpression.slice(0, -1);
            updateDisplay();
        }
    }

    function onEqualsPressed() {
        if (currentExpression === '') return;

        let expr = currentExpression;
        while (expr.length > 0 && isOperatorChar(expr.slice(-1))) {
            expr = expr.slice(0, -1);
        }

        if (!expr) return;

        tvExpression.textContent = currentExpression;
        const result = evaluateExpression(expr);
        tvResult.textContent = result;
        isResultDisplayed = true;
    }

    function updateDisplay() {
        if (currentExpression === '') {
            tvExpression.textContent = '';
            tvResult.textContent = '0';
        } else {
            tvExpression.textContent = currentExpression;
        }
    }

    function getCurrentOperand() {
        let lastOpIdx = -1;
        for (let i = currentExpression.length - 1; i >= 0; i--) {
            if (isOperatorChar(currentExpression[i])) {
                lastOpIdx = i;
                break;
            }
        }
        return lastOpIdx !== -1 ? currentExpression.slice(lastOpIdx + 1) : currentExpression;
    }

    function isOperatorChar(c) {
        return ['+', '−', '-', '×', '*', '÷', '/', '%'].includes(c);
    }

    // Mathematical Evaluator (Shunting-yard algorithm matching MainActivity.java)
    function evaluateExpression(expr) {
        try {
            const tokens = tokenize(expr);
            if (tokens.length === 0) return '0';

            const postfix = infixToPostfix(tokens);
            const val = evaluatePostfix(postfix);

            if (isNaN(val) || !isFinite(val)) return 'Error';

            return Number.isInteger(val) ? String(val) : String(parseFloat(val.toFixed(8)));
        } catch (e) {
            return 'Error';
        }
    }

    function tokenize(expr) {
        const tokens = [];
        let buf = '';

        for (let i = 0; i < expr.length; i++) {
            const c = expr[i];
            if ((c >= '0' && c <= '9') || c === '.') {
                buf += c;
            } else if (isOperatorChar(c)) {
                if ((c === '−' || c === '-') && (i === 0 || isOperatorChar(expr[i - 1]))) {
                    buf += '-';
                } else {
                    if (buf.length > 0) {
                        tokens.push(buf);
                        buf = '';
                    }
                    tokens.push(c);
                }
            }
        }

        if (buf.length > 0) tokens.push(buf);
        return tokens;
    }

    function infixToPostfix(tokens) {
        const output = [];
        const stack = [];

        tokens.forEach(token => {
            if (!isNaN(parseFloat(token))) {
                output.push(token);
            } else if (isOperatorChar(token[0])) {
                while (stack.length > 0 && getPrecedence(stack[stack.length - 1]) >= getPrecedence(token)) {
                    output.push(stack.pop());
                }
                stack.push(token);
            }
        });

        while (stack.length > 0) output.push(stack.pop());
        return output;
    }

    function evaluatePostfix(postfix) {
        const stack = [];

        postfix.forEach(token => {
            if (!isNaN(parseFloat(token))) {
                stack.push(parseFloat(token));
            } else if (isOperatorChar(token[0])) {
                if (stack.length < 2) {
                    if (token === '%' && stack.length === 1) {
                        const val = stack.pop();
                        stack.push(val / 100);
                        return;
                    }
                    throw new Error('Invalid');
                }
                const b = stack.pop();
                const a = stack.pop();

                switch (token) {
                    case '+': stack.push(a + b); break;
                    case '−':
                    case '-': stack.push(a - b); break;
                    case '×':
                    case '*': stack.push(a * b); break;
                    case '÷':
                    case '/':
                        if (Math.abs(b) < 1e-12) throw new Error('DivByZero');
                        stack.push(a / b);
                        break;
                    case '%':
                        if (Math.abs(b) < 1e-12) throw new Error('ModByZero');
                        stack.push(a % b);
                        break;
                }
            }
        });

        return stack.length > 0 ? stack.pop() : 0;
    }

    function getPrecedence(op) {
        if (['+', '−', '-'].includes(op)) return 1;
        if (['×', '*', '÷', '/', '%'].includes(op)) return 2;
        return 0;
    }

    // Keyboard support
    document.addEventListener('keydown', (e) => {
        if (e.key >= '0' && e.key <= '9') onNumberPressed(e.key);
        else if (e.key === '.') onDotPressed();
        else if (e.key === '+') onOperatorPressed('+');
        else if (e.key === '-') onOperatorPressed('−');
        else if (e.key === '*') onOperatorPressed('×');
        else if (e.key === '/') { e.preventDefault(); onOperatorPressed('÷'); }
        else if (e.key === '%') onOperatorPressed('%');
        else if (e.key === 'Enter' || e.key === '=') { e.preventDefault(); onEqualsPressed(); }
        else if (e.key === 'Backspace') onBackspacePressed();
        else if (e.key === 'Escape' || e.key.toLowerCase() === 'c') onClearPressed();
    });
});
