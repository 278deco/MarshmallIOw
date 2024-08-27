package marshmalliow.core.algorithm;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;

import marshmalliow.core.exceptions.JSONParseException;
import marshmalliow.core.json.objects.JSONToken;
import marshmalliow.core.json.utils.JSONTokenEnum;

/**
 * This class is an implementation of the Shunting Yard algorithm.
 * <p>
 * The Shunting Yard algorithm is used to parse mathematical expressions
 * specified in infix notation. The algorithm can produce either a postfix
 * notation expression or a prefix notation expression. For now, this
 * implementation only produces a postfix notation expression. It can also
 * convert the postfix notation back to infix notation.<br/>
 * In addition to the conversion, this implementation can evaluate the postfix 
 * and provide the result of the formula.
 * <p>
 * A simple explaination of the algorithm is as follows:
 * <ol>
 * <li>Read the tokens in infix notation</li>
 * <li>While there are tokens to be read:
 * <ol>
 *	 <li>Read a token</li>
 *	 <li>If the token is a number, then add it to the output queue</li>
 * 	 <li>If the token is an operator, then:
 * 	 <ol>
 *		<li>While the stack is not empty and the top of the stack is an operator with
 * 		greater precedence than the token, pop the operator from the stack and add it
 * 		to the output queue</li>
 *		<li>Push the token onto the stack</li>
 *	 </ol>
 *	 </li>
 *	 <li>If the token is a left parenthesis, then push it onto the stack</li>
 *	 <li>If the token is a right parenthesis, then:
 *	 <ol>
 *		<li>While the top of the stack is not a left parenthesis, pop the operator
 *		from the stack and add it to the output queue</li>
 *		<li>Pop the left parenthesis from the stack</li>
 *		<li>If the token at the top of the stack is a function, pop it and add it to
 *		the output queue</li>
 *	 </ol>
 * 	 </li>
 * 	</ol>
 * 	</li>
 * </ol>
 * <p>
 * This implementation supports the following operators and functions:
 * <ul>
 * <li><em>Basic operators:</em> +, -, *, /, ^</li>
 * <li><em>Trigonometric functions:</em> sin, cos, tan</li>
 * <li><em>Exponential function:</em> exp</li>
 * <li><em>Logarithmic functions:</em> log, ln</li>
 * <li><em>Maximum and minimum functions:</em> max, min</li>
 * <li><em>Absolute value:</em> abs</li>
 * <li><em>Square root:</em> sqrt</li>
 * <li><em>Round:</em> round</li>
 * </ul>
 * <p>
 * The algorithm is implemented in a way that it can handle multiple variables in
 * the formula. The variables are defined by the characters <code>'x', 'y' and 'z'</code>.
 * When evaluating the formula, the user can provide a mapping of the variable to
 * a number and the algorithm will do the replacement on the fly.
 * <p>
 * Instances of this class are not safe for use by multiple concurrent threads.
 * If multiple threads access an instance concurrently, it must be synchronized
 * externally.
 * 
 * @author 278deco
 * @version 1.0
 * @since 0.3.1
 * @see <a href="https://en.wikipedia.org/wiki/Shunting-yard_algorithm">
 *      Wikepedia - Shunting Yard algorithm </a>
 * @see <a href="https://en.wikipedia.org/wiki/Reverse_Polish_notation">
 *      Wikepedia - Reverse Polish notation </a>
 */
public class ShuntingYardImpl {

	// Running modes available
	private static final int FRESH_INSTANCE = 0;
	private static final int PROCESSING = 1;
	private static final int INFIX_TO_POSTFIX = 2;
	private static final int POSTFIX_TO_INFIX = 3;
	
	private static final Map<Integer, String> ID_TO_OPERATOR = Map.ofEntries(Map.entry(0, "("), Map.entry(1, "+"),
			Map.entry(2, "-"), Map.entry(3, "*"), Map.entry(4, "/"), Map.entry(5, "^"), Map.entry(6, "sin"),
			Map.entry(7, "cos"), Map.entry(8, "tan"), Map.entry(9, "exp"), Map.entry(10, "log"), Map.entry(11, "ln"),
			Map.entry(12, "max"), Map.entry(13, "min"), Map.entry(14, "abs"), Map.entry(15, "sqrt"), Map.entry(16, "round"));

	private static final Map<String, Integer> OPERATOR_TO_ID = Map.ofEntries(Map.entry("(", 0), Map.entry("+", 1),
			Map.entry("-", 2), Map.entry("*", 3), Map.entry("/", 4), Map.entry("^", 5), Map.entry("sin", 6),
			Map.entry("cos", 7), Map.entry("tan", 8), Map.entry("exp", 9), Map.entry("log", 10), Map.entry("ln", 11),
			Map.entry("max", 12), Map.entry("min", 13), Map.entry("abs", 14), Map.entry("sqrt", 15), Map.entry("round", 16));
	
	private static final Map<String, Function<Number, Number>> FUNC_OPERATOR_MAPPING = Map.ofEntries(
			Map.entry("sin", (x) -> Math.sin(x.doubleValue())), Map.entry("cos", (x) -> Math.cos(x.doubleValue())),
			Map.entry("tan", (x) -> Math.tan(x.doubleValue())), Map.entry("exp", (x) -> Math.exp(x.doubleValue())),
			Map.entry("log", (x) -> Math.log10(x.doubleValue())), Map.entry("ln", (x) -> Math.log(x.doubleValue())),
			Map.entry("abs", (x) -> Math.abs(x.doubleValue())), Map.entry("sqrt", (x) -> Math.sqrt(x.doubleValue())),
			Map.entry("round", (x) -> Math.round(x.doubleValue())));
	
	private static final Map<String, BiFunction<Number, Number, Number>> BIFUNC_OPERATOR_MAPPING = Map.ofEntries(
			Map.entry("max", (a,b) -> Math.max(a.doubleValue(), b.doubleValue())), 
			Map.entry("min", (a, b) -> Math.min(a.doubleValue(), b.doubleValue())));
	
	private static final int SPECIAL_OPS_BUFFER_SIZE = 10;
	private static final int DEFAULT_NUMBER_BUFFER_SIZE = 10;
	private static final int HIGHEST_PRECEDENCE = 4;
	private static final int LOWEST_PRECEDENCE = 1;

	private Queue<Object> outputQueue;
	private Deque<Integer> operatorStack;

	private Deque<Object> infixStack;

	private char[] buffer;
	private int bufferIndex = 0;

	private int currentMode = FRESH_INSTANCE;

	public ShuntingYardImpl(String input) {
		this(input.toCharArray());
	}

	public ShuntingYardImpl(char[] input) {
		this.outputQueue = new ArrayDeque<>();
		this.operatorStack = new ArrayDeque<>();
		this.infixStack = new ArrayDeque<>();

		this.buffer = input;
	}

	/**
	 * Increase the buffer reading position (index).
	 * <p>
	 * The method increase the buffer index until the end is reached. Multiple calls
	 * of this method once the end is reached will not change the buffer index.
	 *
	 * @param number The number to be added to the buffer reading position
	 * @return If we reached the end of the buffer
	 * @throws IOException
	 */
	private boolean incBuffer(int number) {
		if (this.bufferIndex + number >= buffer.length) {
			return true;
		}

		this.bufferIndex += number;
		return false;
	}

	public void infixToPostfix() throws IllegalArgumentException {
		if (currentMode != FRESH_INSTANCE)
			clear();
		this.currentMode = PROCESSING;

		boolean isLastBasicOperator = false;
		do {
			final char readChar = buffer[this.bufferIndex];
			
			switch (readChar) {
			case ' ', '\n', '\r', '\t': // Remove unwanted characters
				break;
			case ',': // Function argument separator
			case 'x', 'y', 'z': // Common variables
				isLastBasicOperator = false;
				outputQueue.add(readChar);
				break;
			case '(':
				isLastBasicOperator = false;
				operatorStack.offerFirst(OPERATOR_TO_ID.get("("));
				break;
			case ')':
				isLastBasicOperator = false;
				boolean runDestack;
				do {
					runDestack = !operatorStack.isEmpty() && operatorStack.peek() != OPERATOR_TO_ID.get("(");

					if (runDestack)
						outputQueue.add(ID_TO_OPERATOR.get(operatorStack.poll()));
				} while (runDestack);

				operatorStack.poll(); // Remove the '('

				// We check if we have a basic operator after the parenthesis.
				// If not its a function and add it to the output
				if (!operatorStack.isEmpty() && !isBasicOperator(ID_TO_OPERATOR.get(operatorStack.peek()))) {
					outputQueue.add(ID_TO_OPERATOR.get(operatorStack.poll()));
				}

				break;
			case '+', '-', '^', '*', '/':
				if(isLastBasicOperator) throw new IllegalArgumentException("Two operators in a row");
				handleOperatorStackPostfix(readChar);
				isLastBasicOperator = true;
				break;
			default:
				isLastBasicOperator = false;
				if (isValidNumber(readChar, true)) {
					outputQueue.add(parseNumbers(readChar));
					break;
				}

				final String operator = parseSpecialOperator();
				if (operator != null)
					this.operatorStack.offerFirst(OPERATOR_TO_ID.get(operator));
				else
					throw new IllegalArgumentException("Unexpected value: " + buffer[this.bufferIndex]);
			}
		} while (!incBuffer(1));

		// Add the remaining operators to the output queue
		while (!operatorStack.isEmpty()) {
			outputQueue.add(ID_TO_OPERATOR.get(operatorStack.poll()));
		}
		
		this.currentMode = INFIX_TO_POSTFIX;
	}
	
	public Optional<Number> evaluate() {
		return evaluate(Map.of());
	}
	
	public Optional<Number> evaluate(Map<String, Number> variableMapping) {
		if (currentMode != INFIX_TO_POSTFIX || outputQueue.isEmpty()) 
			throw new IllegalStateException("Cannot evaluate in this without postfix conversion result");
		if(variableMapping == null) throw new IllegalArgumentException("Variable mapping cannot be null");
		if(variableMapping.size() > 3) throw new IllegalArgumentException("Too many variables in mapping. Only x, y and z are supported.");
		
		final Deque<Number> evalStack = new ArrayDeque<>();
		final Deque<Number> argumentStack = new ArrayDeque<>();
		final Deque<Object> outputStack = new ArrayDeque<>(outputQueue);
		
		while(!outputStack.isEmpty()) {
			final Object obj = outputStack.poll();

			if(obj instanceof Number) {
                evalStack.addFirst((Number) obj);
                continue;
            }
    			
			final String operator = obj.toString();
			final Number evalNumber = evalStack.poll();
			
			switch (operator) {
			case "+":
				if(evalStack.isEmpty()) throw new IllegalArgumentException("Not enough arguments for operator: +");
				evalStack.addFirst(evalStack.poll().doubleValue() + evalNumber.doubleValue());
				break;
			case "-":
				if(evalStack.isEmpty()) throw new IllegalArgumentException("Not enough arguments for operator: -");
				evalStack.addFirst(evalStack.poll().doubleValue() - evalNumber.doubleValue());
				break;
			case "*":
				if(evalStack.isEmpty()) throw new IllegalArgumentException("Not enough arguments for operator: *");
				evalStack.addFirst(evalStack.poll().doubleValue() * evalNumber.doubleValue());
				break;
			case "/":
				if(evalStack.isEmpty()) throw new IllegalArgumentException("Not enough arguments for operator: /");
				evalStack.addFirst(evalStack.poll().doubleValue() / evalNumber.doubleValue());
				break;
			case "^":
				if(evalStack.isEmpty()) throw new IllegalArgumentException("Not enough arguments for operator: ^");
				evalStack.addFirst(Math.pow(evalStack.poll().doubleValue(), evalNumber.doubleValue()));
				break;
			case ",":
				argumentStack.addFirst(evalNumber);
				break;
			case "x", "y", "z":
				if (variableMapping.containsKey(operator)) {
					evalStack.addFirst(evalNumber); //We re-insert the variable value because we don't need to evaluate it
					evalStack.addFirst(variableMapping.get(operator));
				} else {
					throw new IllegalArgumentException("Variable not found in mapping: " + operator);
				}
			    break;
			default:
				if(!OPERATOR_TO_ID.containsKey(operator)) throw new IllegalArgumentException("Unknown operator: " + operator);
				
				switch(argumentStack.size()) {
				case 0:
					evalStack.addFirst(FUNC_OPERATOR_MAPPING.get(operator).apply(evalNumber));
					break;
				case 1:
					evalStack.addFirst(
							BIFUNC_OPERATOR_MAPPING.get(operator).apply(argumentStack.poll(), evalNumber));
					break;
				default:
                    throw new IllegalArgumentException("Too many arguments for function: " + operator);
				}
				break;
			}
		}
		
		return evalStack.isEmpty() ? Optional.empty() : Optional.of(evalStack.poll());
	}

	public void postfixToInfix() {
		if (currentMode != FRESH_INSTANCE)
			clear();
		this.currentMode = PROCESSING;

		do {
			final char readChar = buffer[this.bufferIndex];
			
			switch (readChar) {
			case ' ', '\n', '\r', '\t': // Remove unwanted characters
				break;
			case ',': // Function argument separator
			case 'x', 'y', 'z': // Common variables
				infixStack.addFirst(readChar);
				break;
			case '+', '-', '^', '*', '/':
				handleOperatorStackInfix(String.valueOf(readChar));
				break;
			default:
				if (isValidNumber(readChar, true)) {
					infixStack.addFirst(parseNumbers(readChar));
					break;
				}

				final String operator = parseSpecialOperator();

				if (operator != null)
					handleOperatorStackInfix(operator);
				else
					throw new IllegalArgumentException("Unexpected value: " + buffer[this.bufferIndex]);
			}
		} while (!incBuffer(1));

		// Add the infix stack to the output queue
		while (!infixStack.isEmpty()) {
			outputQueue.add(infixStack.poll());
		}
		
		this.currentMode = POSTFIX_TO_INFIX;
	}

	public void clear() {
		this.outputQueue.clear();
		this.operatorStack.clear();
		this.infixStack.clear();
		this.bufferIndex = 0;
		this.currentMode = FRESH_INSTANCE;
	}

	public void clear(String newInput) {
		clear();
		this.buffer = newInput.toCharArray();
	}

	public void clear(char[] newInput) {
		clear();
		this.buffer = newInput;
	}

	public Collection<Object> getOutputQueue() {
		if (currentMode == FRESH_INSTANCE || currentMode == PROCESSING)
            throw new IllegalStateException("No output queue available");
		return Collections.unmodifiableCollection(outputQueue);
	}

	public String getOutputAsString() {
		if (currentMode == FRESH_INSTANCE || currentMode == PROCESSING)
            throw new IllegalStateException("No output queue available");
		
		final StringBuilder builder = new StringBuilder();
		outputQueue.forEach((value) -> builder.append(value).append(" "));
		builder.setLength(builder.length() - 1);

		return builder.toString();
	}

	private void handleOperatorStackInfix(String newOperator) throws IllegalStateException {
		if (infixStack.isEmpty())
			throw new IllegalStateException("Empty infix stack");

		if (isBasicOperator(newOperator)) {
			Object right = infixStack.poll();
			Object left = infixStack.poll();
			
			if(right == null || left == null) throw new IllegalStateException("Cannot have null values in infix stack");

			final int rightPrecedence = getLowestPrecedenceOperator(right.toString());
			final int leftPrecedence = getLowestPrecedenceOperator(left.toString());
			final int currentPrecedence = getOperatorPrecedence(newOperator.charAt(0));
			
			if (rightPrecedence < currentPrecedence) {
				right = "(" + right + ")";
			}
			
			if (leftPrecedence < currentPrecedence) {
				left = "(" + left + ")";
			}
			
			infixStack.addFirst(left + " " +  newOperator + " " + right);
		} else {
			final Deque<Object> argumentStack = new ArrayDeque<>();
			final StringBuilder function = new StringBuilder("(");
			argumentStack.addFirst(infixStack.poll());

			boolean runDestack;
			do {
				runDestack = !infixStack.isEmpty() && infixStack.peek().equals(',');

				if (runDestack) {
					argumentStack.addFirst(infixStack.poll()); // We get the comma
					argumentStack.addFirst(infixStack.poll()); // And the function argument
				}
			} while (runDestack);
			
			while (!argumentStack.isEmpty()) {
				final Object argument = argumentStack.poll();
				function.append(argument).append(argument.equals(',') ? " " : "");
				
			}

			infixStack.addFirst(newOperator + function.toString() + ")");
		}
	}

	private void handleOperatorStackPostfix(char newOperator) {
		final String newOperatorStr = String.valueOf(newOperator);

		final int opsPrecendence = getOperatorPrecedence(newOperator);
		int stackTopPrecendence = operatorStack.isEmpty() ? 0 : getOperatorPrecedence(operatorStack.peek());

		if (opsPrecendence <= stackTopPrecendence && isLeftAssociative(newOperatorStr)) {

			boolean runDestack = true;
			while (runDestack) {
				outputQueue.add(ID_TO_OPERATOR.get(operatorStack.poll()));

				if (operatorStack.isEmpty())
					runDestack = false;
				else {
					stackTopPrecendence = getOperatorPrecedence(operatorStack.peek());
					// If the new operator is right associative, the precedence must be strictly greater
					runDestack = opsPrecendence <= stackTopPrecendence;
				}
			}
		}

		operatorStack.offerFirst(OPERATOR_TO_ID.get(newOperatorStr));
	}

	/**
	 * This method is used to insert the string data of a
	 * {@link JSONTokenEnum#VALUE_STRING} into its {@link JSONToken} instance.<br/>
	 * It read the {@link #buffer} and take all characters between two quotations
	 * marks as the string data.
	 *
	 * @return A {@link JSONToken} containing string data
	 * @throws JSONParseException
	 */
	private String parseSpecialOperator() {

		char[] strbuff = new char[SPECIAL_OPS_BUFFER_SIZE];
		int strPos = 0;

		char c;
		boolean strEnd = false;
		while (!strEnd) {
			c = buffer[bufferIndex];
			strEnd = !Character.isLetter(c);

			if (!strEnd) {
				strbuff[strPos++] = c;

				strEnd = incBuffer(1);
			} else
				incBuffer(-1); // We need to go back one step to read the next character
		}

		final String ops = new String(strbuff, 0, strPos);

		return OPERATOR_TO_ID.containsKey(ops) ? ops : null;
	}

	/**
	 * This method is used to create parse a number and store it as a
	 * {@link Number}.
	 *
	 * @param readChar The read character, meaning the character read from the
	 *                 buffer how caused the invocation this method.
	 * @return A {@link Number} representing the string read
	 * @throws IOException
	 */
	private Number parseNumbers(char readChar) {
		// If the number is a special number, like PI, return the value
		if (readChar == 'π') {
			return Math.PI;
		}

		//We take a guess that the number's size will not exceed the buffer size divided by 2.
		char[] strbuff = new char[Math.min((int)this.buffer.length/2, DEFAULT_NUMBER_BUFFER_SIZE)];
		int strPos = 0;

		boolean doubleCast = false;

		char c;
		boolean numberEnd = false;
		while (!numberEnd) {
			c = buffer[bufferIndex];
			numberEnd = !isValidNumber(c, strPos == 0);

			if (!numberEnd) {
				strbuff[strPos++] = c;

				if (c == '.' || c == 'e' || c == 'E')
					doubleCast = true;
				
				//If the buffer is full, we need to increase. We multiply the size by 2 each time the buffer is full.
				if(strPos+1 >= strbuff.length) {
					char[] nstrbuff = new char[strbuff.length*2];
					System.arraycopy(strbuff, 0, nstrbuff, 0, strbuff.length);
					strbuff = nstrbuff;
				}

				numberEnd = incBuffer(1);
			} else
				incBuffer(-1); // We need to go back one step to read the next character
		}

		final String numberString = new String(strbuff, 0, strPos);
		if (doubleCast) {
			final Double value = Double.parseDouble(numberString);
			if (value.isNaN() || value.isInfinite())
				throw new NumberFormatException("NaN or Infinity value founded");

			return value;
		} else {
			final Long value = Long.parseLong(numberString);

			return value;
		}
	}

	private boolean isValidNumber(char c, boolean atStart) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case 'π':
			return true;
		case 'e':
		case 'E':
		case '.':
			return !atStart;
		default:
			return false;
		}
	}

	private int getLowestPrecedenceOperator(String expression) {
		int lowestPrecedence = HIGHEST_PRECEDENCE;

		boolean bypassParenthesis = false;
		for (int i = 0; i < expression.length(); i++) {
			final char c = expression.charAt(i);
			
			switch(c) {
			case '(':
				bypassParenthesis = true;
				break;
			case ')':
				bypassParenthesis = false;
				break;
			default:
				if (!bypassParenthesis && isBasicOperator(c)) {
					final int precedence = getOperatorPrecedence(c);

					if (precedence < lowestPrecedence) {
						lowestPrecedence = precedence;
					}
				}
			}
			
			if (lowestPrecedence == LOWEST_PRECEDENCE) {
				break;
			}
		}

		return lowestPrecedence;
	}

	private int getOperatorPrecedence(int opsId) {
		if (opsId > 5) { // Optimisation to avoid map call for functions operators
			return 0;
		} else {
			return getOperatorPrecedence(ID_TO_OPERATOR.get(opsId).charAt(0));
		}
	}

	private int getOperatorPrecedence(char ops) {
		switch (ops) {
		case '^':
			return 4;
		case '*', '/':
			return 3;
		case '+', '-':
			return 2;
		default:
			return 0;
		}
	}

	private boolean isBasicOperator(String ops) {
		return ops.length() > 0 ? isBasicOperator(ops.charAt(0)) : false;
	}

	private boolean isBasicOperator(char ops) {
		switch (ops) {
		case '+':
		case '-':
		case '*':
		case '/':
		case '^':
			return true;
		default:
			return false;
		}
	}

	private boolean isLeftAssociative(String ops) {
		switch (ops) {
		case "^":
			return false;
		default:
			return true;
		}
	}
}
