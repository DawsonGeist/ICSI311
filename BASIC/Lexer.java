import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {

	// This enum will be the state machine that will process input one character at
	// a time.
	// It only needs 4 variables to run, an input character, the start index,
	// current index,
	// and end index of the input string

	public static int startIndex = 0;
	public static int currentIndex = 0;
	public static int lastIndex = 0;
	public static ArrayList<Token> tokens = new ArrayList<Token>();

	public enum StateMachine {
		START {
			public StateMachine transition(String input) {
				StateMachine returner = ENDPARSING;
				// CHECK FOR EMPTY INPUT
				if (startIndex == lastIndex) {
					returner = EOL;
					return returner;
				}
				char currentSymbol = input.charAt(currentIndex);
				// Number Transition
				if (Character.isDigit(currentSymbol) || currentSymbol == '.') {
					returner = NUMBER;
				}
				// Comparator Transition
				else if (currentSymbol == '=' || currentSymbol == '<' || currentSymbol == '>') {
					returner = COMPARATOR;
				}
				// Parenthesis Transition
				else if (currentSymbol == '(' || currentSymbol == ')') {
					returner = PAREN;
				}
				// comma transition
				else if (currentSymbol == ',') {
					returner = COMMA;
				}
				// String Transition
				else if (currentSymbol == '"') {
					returner = STRING;
				}
				// Word Transition
				else if (Character.isLetter(currentSymbol)) {
					returner = WORD;
				}
				// Operation Transition
				else if (currentSymbol == '+' || currentSymbol == '/' || currentSymbol == '*') {
					returner = OTHEROPERATORS;
				}
				// Minus Transition
				else if (currentSymbol == '-') {
					/*
					 * This is a special case if we start with a negative number... The logic
					 * required to handle this Scenario is Similar to MULTIPLEMINUSHANDLER except
					 * that for number we DON'T create a minus token, and we DON'T modify startIndex
					 * before the transition back to START
					 */
					returner = MINUSATSTART;
				}
				// Ignore Spaces
				else if (currentSymbol == ' ' || currentSymbol == '\t') {
					// EOL has been reached, last character is a space so ignore and proceed to EOL
					if (currentIndex == lastIndex - 1) {
						startIndex = lastIndex;
						returner = START;
					} else {
						currentIndex++;
						startIndex = currentIndex;
						returner = START;
					}
				}
				// Comma Transition
				// Error Transition
				else {
					// Handle Error
					returner = ERROR;
				}

				return returner;
			}
		},
		COMMA {
			public StateMachine transition(String input) {
				Token comma = new Token(",", "");
				tokens.add(comma);
				currentIndex++;
				startIndex = currentIndex;
				return START;
			}
		},
		WORD {
			public StateMachine transition(String input) {
				StateMachine returner = START;
				// Check for EOL
				if (currentIndex == lastIndex - 1) {
					// Its one letter, playing it safe and creating a unknown word token. I will
					// fight this to the end
					// if this gets me points off lol x
					String value = input.substring(startIndex);
					Token identifier = new Token("identifier", value);
					tokens.add(identifier);
					startIndex = lastIndex;
				} else {
					char currentCharacter = input.charAt(currentIndex);
					// Loop index until symbol isnt alphabetic, then check for label/optional $/%
					while (Character.isLetter(currentCharacter) && currentIndex != lastIndex - 1) {
						currentIndex++;
						currentCharacter = input.charAt(currentIndex);
					}
					// Check for EOL, if yes, check if value is equal to that of our hashmap keys,
					// generate the appropriate token
					if (currentIndex == lastIndex - 1) {
						// Check the current Character for :, if yes create label token, if not proceed
						// to check for $/%, then EOL
						if (currentCharacter == ':') {
							Token label = new Token("label", input.substring(startIndex, currentIndex));
							tokens.add(label);
							startIndex = lastIndex;
							returner = START;

						} else if (currentCharacter == '$' || currentCharacter == '%') {
							String value = input.substring(startIndex, currentIndex);
							// Check if the Word is Known, else create unknown token
							if (knownWords.containsKey(value.toLowerCase())) {
								String tokenID = knownWords.get(value.toLowerCase());
								Token known = new Token(tokenID, value);
								tokens.add(known);
								startIndex = lastIndex;
								returner = START;
							} else {
								Token unknown = new Token("identifier", input.substring(startIndex));
								tokens.add(unknown);
								startIndex = lastIndex;
								returner = START;
							}
						} else {
							String value = input.substring(startIndex, currentIndex + 1);
							// Check if the Word is Known, else create unknown token
							if (knownWords.containsKey(value.toLowerCase())) {
								String tokenID = knownWords.get(value.toLowerCase());
								Token known = new Token(tokenID, value);
								tokens.add(known);
								startIndex = lastIndex;
								returner = START;
							} else {
								Token unknown = new Token("identifier", value);
								tokens.add(unknown);
								startIndex = lastIndex;
								returner = START;
							}
						}
					}
					// We are not at EOL, so Check current symbol for label, $ or %
					// Check the current Character for :, if yes create label token, if not proceed
					// to check for $/%, then EOL
					else if (currentCharacter == ':') {
						Token label = new Token("label", input.substring(startIndex, currentIndex));
						tokens.add(label);
						currentIndex++;
						startIndex = currentIndex;
						returner = START;

					} else {
						String value = input.substring(startIndex, currentIndex);
						// Check if the Word is Known, else create unknown token
						if (knownWords.containsKey(value.toLowerCase())) {
							String tokenID = knownWords.get(value.toLowerCase());
							Token known = new Token(tokenID, value);
							tokens.add(known);
							// Check current Symbol for ' ', $, %... if present increment currentIndex else
							// Leave current index and go to start to check for error
							if (currentCharacter == ' ' || currentCharacter == '$' || currentCharacter == '%') {
								currentIndex++;
								startIndex = currentIndex;
							}
							returner = START;
						} else {
							Token unknown = new Token("identifier", input.substring(startIndex, currentIndex));
							tokens.add(unknown);
							// Check current Symbol for ' ', $, %... if present increment currentIndex else
							// Leave current index and go to start to check for error
							if (currentCharacter == ' ' || currentCharacter == '$' || currentCharacter == '%') {
								currentIndex++;
								startIndex = currentIndex;
							}
							startIndex = currentIndex;
							returner = START;
						}
					}

				}
				return returner;
			}
		},
		STRING {
			public StateMachine transition(String input) {
				StateMachine returner = START;
				// Check for EOL, if So, Throw Error because string was never closed
				if (currentIndex == lastIndex - 1) {
					System.out.println("ERROR - STRING WAS NEVER CLOSED");
					returner = ENDPARSING;
				} else {
					// increment index
					currentIndex++;
					char currentSymbol = input.charAt(currentIndex);
					// Loop until EOL or " is found
					while (currentSymbol != '"' && currentIndex != lastIndex - 1) {
						currentIndex++;
						currentSymbol = input.charAt(currentIndex);
					}
					// check for EOL -> Throw error because string was never closed
					if (currentIndex == lastIndex - 1) {
						// Check if last character is string
						if (currentSymbol == '"') {
							String value = input.substring(startIndex);
							Token str = new Token("string", value);
							tokens.add(str);
							startIndex = lastIndex;
							returner = START;
						} else {
							System.out.println("ERROR - STRING WAS NEVER CLOSED");
							returner = ENDPARSING;
						}
					}
					// Generate String Token
					else {
						// We add one to the end so that the value includes the last "
						String value = input.substring(startIndex, currentIndex + 1);
						Token str = new Token("string", value);
						tokens.add(str);
						currentIndex++;
						startIndex = currentIndex;
						returner = START;
					}
				}

				return returner;
			}
		},
		PAREN {
			public StateMachine transition(String input) {
				StateMachine returner = START;
				char currentCharacter = input.charAt(currentIndex);
				if (currentCharacter == '(') {
					Token lparen = new Token("(", "");
					tokens.add(lparen);
					// Check for EOL
					if (currentIndex == lastIndex - 1) {
						startIndex = lastIndex;
						returner = START;
					} else {
						// increment to the next symbol
						currentIndex++;
						startIndex = currentIndex;
						returner = START;
					}
				} else if (currentCharacter == ')') {
					Token rparen = new Token(")", "");
					tokens.add(rparen);
					// Check for EOL
					if (currentIndex == lastIndex - 1) {
						startIndex = lastIndex;
						returner = START;
					} else {
						// increment to the next symbol
						currentIndex++;
						startIndex = currentIndex;
						returner = START;
					}
				}
				return returner;
			}
		},
		COMPARATOR {
			public StateMachine transition(String input) {
				StateMachine returner = START;
				char currentCharacter = input.charAt(currentIndex);
				// =, <, <=, <>, >, >=
				if (currentCharacter == '=') {
					Token equals = new Token("=", "");
					tokens.add(equals);
					// Check for EOL
					if (currentIndex == lastIndex - 1) {
						startIndex = lastIndex;
						returner = START;
					} else {
						// increment to the next symbol
						currentIndex++;
						startIndex = currentIndex;
						returner = START;
					}
				} else if (currentCharacter == '<') {
					// Check for EOL
					if (currentIndex == lastIndex - 1) {
						Token lessThan = new Token("<", "");
						tokens.add(lessThan);
						startIndex = lastIndex;
						returner = START;
					} else {
						// Check the next symbol, Looking for = or >, else generate lessThan Token,
						// return to start
						currentIndex++;
						currentCharacter = input.charAt(currentIndex);
						if (currentCharacter == '=') {
							Token lessThanOrEqual = new Token("<=", "");
							tokens.add(lessThanOrEqual);
							// Check for EOL
							if (currentIndex == lastIndex - 1) {
								startIndex = lastIndex;
								returner = START;
							} else {
								// Increment currentIndex and return to start
								currentIndex++;
								startIndex = currentIndex;
								returner = START;
							}
						} else if (currentCharacter == '>') {
							Token notEqual = new Token("<>", "");
							tokens.add(notEqual);
							// Check for EOL
							if (currentIndex == lastIndex - 1) {
								startIndex = lastIndex;
								returner = START;
							} else {
								// Increment currentIndex and return to start
								currentIndex++;
								startIndex = currentIndex;
								returner = START;
							}
						} else {
							Token lessThan = new Token("<", "");
							tokens.add(lessThan);
							// Check for EOL
							if (currentIndex == lastIndex - 1) {
								startIndex = lastIndex;
								returner = START;
							} else {
								// return to start
								startIndex = currentIndex;
								returner = START;
							}
						}
					}

				} else if (currentCharacter == '>') {
					// Check for EOL
					if (currentIndex == lastIndex - 1) {
						Token greaterThan = new Token(">", "");
						tokens.add(greaterThan);
						startIndex = lastIndex;
						returner = START;
					} else {
						// Check the next symbol, Looking for =, else generate GreaterThan Token, return
						// to start
						currentIndex++;
						currentCharacter = input.charAt(currentIndex);
						if (currentCharacter == '=') {
							Token greaterThanOrEqual = new Token(">=", "");
							tokens.add(greaterThanOrEqual);
							// Check for EOL
							if (currentIndex == lastIndex - 1) {
								startIndex = lastIndex;
								returner = START;
							} else {
								// Increment currentIndex and return to start
								currentIndex++;
								startIndex = currentIndex;
								returner = START;
							}
						} else {
							Token greaterThan = new Token(">", "");
							tokens.add(greaterThan);
							// Check for EOL
							if (currentIndex == lastIndex - 1) {
								startIndex = lastIndex;
								returner = START;
							} else {
								// return to start
								startIndex = currentIndex;
								returner = START;
							}
						}
					}
				}
				return returner;
			}
		},
		EOL {
			public StateMachine transition(String input) {
				Token eol = new Token("eol", "");
				tokens.add(eol);
				return ENDPARSING;
			}
		},
		ERROR {
			public StateMachine transition(String input) {
				char currentSymbol = input.charAt(currentIndex);
				System.out.println("ERROR - INVALID SYMBOL: " + currentSymbol);
				return ENDPARSING;
			}
		},
		NUMBER {
			public StateMachine transition(String input) {
				// Our return variable
				StateMachine returner = ENDPARSING;
				// Error flag for if we encounter 2 or more decimals in our number
				boolean multipleDecimals = false;
				int decimalCounter = 0;
				// Get Current Symbol
				char currentSymbol = input.charAt(currentIndex);

				/*
				 * While the current character is a Digit or Decimal AND current index != Last
				 * Index -1 Continue to increment current index When the loop is broken ->
				 * Create and add new token with value = substring input(startIndex,
				 * currentIndex); set start index = to current index Jump to next State
				 */

				while ((Character.isDigit(currentSymbol) || currentSymbol == '.') && currentIndex != lastIndex - 1) {
					if (currentSymbol == '.') {
						decimalCounter++;
					}
					if (decimalCounter > 1) {
						multipleDecimals = true;
					}
					if (multipleDecimals) {
						System.out.println("Error - Invalid Character - Incorrect Decimal Format");
						returner = ENDPARSING;
						return returner;
					}
					currentIndex++;
					currentSymbol = input.charAt(currentIndex);
				}

				// if EOL AND is a number or decimal
				if ((currentIndex == lastIndex - 1) && (Character.isDigit(currentSymbol) || currentSymbol == '.')) {
					Token num = new Token("number", input.substring(startIndex));
					tokens.add(num);
					startIndex = lastIndex;
					returner = START;
				}
				// if EOL AND is not a number
				else if (currentIndex == lastIndex - 1) {
					Token num = new Token("number", input.substring(startIndex, currentIndex));
					tokens.add(num);
					startIndex = currentIndex;
					returner = START;
				}
				// if we have a negative sign
				else if (currentSymbol == '-') {
					Token num = new Token("number", input.substring(startIndex, currentIndex));
					tokens.add(num);
					startIndex = currentIndex;
					returner = MINUSHANDLER;
				}
				// Either we have another operator or an invalid symbol, regardless we go back
				// to start after making a number Token
				else {
					Token num = new Token("number", input.substring(startIndex, currentIndex));
					tokens.add(num);
					startIndex = currentIndex;
					returner = START;
				}

				return returner;
			}
		},

		/*
		 * Special case if we start with a minus sign, if the next character is a: - -->
		 * Create Minus Token and transition to MULTIPLEMINUSHANDLER \*+ --> Create
		 * Minus Token and Transition to start NUM --> Transition to start eol -->
		 * Generate Minus Token and Go to Start
		 */
		MINUSATSTART {
			public StateMachine transition(String input) {
				// Our return variable
				StateMachine returner = ENDPARSING;
				char currentSymbol = ' ';

				// Get Current Symbol
				if (currentIndex == lastIndex - 1) {
					// Last Symbol
					Token minus = new Token("minus", "");
					tokens.add(minus);
					startIndex = lastIndex;
					returner = START;
				} else {
					currentIndex++;
					currentSymbol = input.charAt(currentIndex);
					if (currentSymbol == '-') {
						Token minus = new Token("minus", "");
						tokens.add(minus);
						returner = MULTIPLEMINUSHANDLER;
					} else if (Character.isDigit(currentSymbol) || currentSymbol == '.') {
						returner = START;
					} else {
						Token minus = new Token("minus", "");
						tokens.add(minus);
						returner = START;
					}
				}
				return returner;
			}
		},
		MINUSHANDLER {
			public StateMachine transition(String input) {
				// Our return variable
				StateMachine returner = ENDPARSING;
				char currentSymbol = ' ';
				/*
				 * At this point in the design of the machine, currentIndex is at the - sign. we
				 * want to get the NEXT character so that we can decide on what this - sign
				 * actually represents (negative number v minus operator). So we will increment
				 * current index by 1 at the beginning of this state
				 * 
				 * IF IT IS THE LAST SYMBOL THEN IT IS AN OPERATOR AND WE NEED TO GO TO EOL
				 * STATE SO SET START = LAST AND GO TO START STATE
				 */

				// Get Current Symbol
				if (currentIndex == lastIndex - 1) {
					// Last Symbol
					Token minus = new Token("minus", "");
					tokens.add(minus);
					startIndex = lastIndex;
					returner = START;
				} else {
					currentIndex++;
					currentSymbol = input.charAt(currentIndex);

					/*
					 * if the current symbol is a: '-' --> then we know that the first symbol
					 * actually was a minus operator; '+/*' --> then we treat the minus sign as an
					 * operator A digit or Decimal --> then we know it is an operator
					 */

					if (currentSymbol == '-') {
						// generate token
						Token minus = new Token("minus", "");
						tokens.add(minus);
						// Transition to next state
						returner = MULTIPLEMINUSHANDLER;
					} else if (Character.isDigit(currentSymbol)) {
						// generate token
						Token minus = new Token("minus", "");
						tokens.add(minus);
						// Set start to current
						startIndex = currentIndex;
						// Transition to next state
						returner = START;
					} else {
						// generate token
						Token minus = new Token("minus", "");
						tokens.add(minus);
						// Set start to current
						startIndex = currentIndex;
						// Transition to next state
						returner = START;
					}
				}

				return returner;
			}
		},
		MULTIPLEMINUSHANDLER {
			public StateMachine transition(String input) {
				// Our return variable
				StateMachine returner = ENDPARSING;
				char currentSymbol = ' ';

				/*
				 * THIS STATE HAS THE SAME DESIGN AS ORIGINAL MINUS HANDLER EXCEPT IF CHARACTER
				 * IS NUMBER IN THAT CASE WE SET START EQUAL TO CURRENT INDEX - 1 SO THAT NUMBER
				 * VALUE WILL INCLUDE THE NEGATIVE SIGN
				 * 
				 * At this point in the design of the machine, currentIndex is at the - sign. we
				 * want to get the NEXT character so that we can decide on what this - sign
				 * actually represents (negative number v minus operator). So we will increment
				 * current index by 1 at the beginning of this state
				 * 
				 * IF IT IS THE LAST SYMBOL THEN IT IS AN OPERATOR AND WE NEED TO GO TO EOL
				 * STATE SO SET START = LAST AND GO TO START STATE
				 */

				// Get Current Symbol
				if (currentIndex == lastIndex - 1) {
					// Last Symbol
					Token minus = new Token("minus", "");
					tokens.add(minus);
					startIndex = lastIndex;
					returner = START;
				} else {
					currentIndex++;
					currentSymbol = input.charAt(currentIndex);

					/*
					 * if the current symbol is a: '-' --> then we know that the first symbol
					 * actually was a minus operator; '+/*' --> then we treat the minus sign as an
					 * operator A digit or Decimal --> then we know it is an operator
					 */

					if (currentSymbol == '-') {
						// generate token
						Token minus = new Token("minus", "");
						tokens.add(minus);
						// Transition to next state
						returner = MULTIPLEMINUSHANDLER;
					} else if (Character.isDigit(currentSymbol)) {
						// Set start to current
						startIndex = currentIndex - 1;
						// Transition to next state
						returner = START;
					} else {
						// generate token
						Token minus = new Token("minus", "");
						tokens.add(minus);
						// Set start to current
						startIndex = currentIndex;
						// Transition to next state
						returner = START;
					}
				}
				return returner;
			}
		},
		OTHEROPERATORS {
			public StateMachine transition(String input) {
				// CurrentSymbol
				char currentSymbol = input.charAt(currentIndex);

				// Generate Operator Token
				if (currentSymbol == '*') {
					Token times = new Token("times", "");
					tokens.add(times);
				} else if (currentSymbol == '/') {
					Token divide = new Token("divide", "");
					tokens.add(divide);
				} else if (currentSymbol == '+') {
					Token plus = new Token("plus", "");
					tokens.add(plus);
				} else
					System.out.println("STATE MACHINE ERROR - OTHEROPERATORS - INVALID CHAR: " + currentSymbol);

				// Increment both current index and set new start index
				currentIndex++;
				startIndex = currentIndex;
				// Go back to start
				return START;
			}
		},
		ENDPARSING {
			public StateMachine transition(String input) {
				return null;
			}

		};

		// Declare the abstract "Transition" function that will be implemented by every
		// State in our enum object
		public abstract StateMachine transition(String input);
	}

	private ArrayList<String> returner = new ArrayList<String>();
	private static HashMap<String, String> knownWords = new HashMap<String, String>();

	public Lexer() {
		// Instantiate the knownWords HashMap
		knownWords.put("print", "print");
		knownWords.put("read", "read");
		knownWords.put("data", "data");
		knownWords.put("input", "input");
		knownWords.put("for", "for");
		knownWords.put("gosub", "gosub");
		knownWords.put("return", "return");
		knownWords.put("next", "next");
		knownWords.put("if", "if");
	}

	public ArrayList<String> lex(String input) {
		// Clear out old tokens
		returner.clear();
		tokens.clear();

		// Start the lexing process
		StateMachine s = StateMachine.START;
		startIndex = 0;
		currentIndex = 0;
		lastIndex = input.length();

		while (s != StateMachine.ENDPARSING && s != StateMachine.ERROR) {
			s = s.transition(input);
		}
		// Error Handler
		if (s == StateMachine.ERROR) {
			System.out.println("ERROR IN LINE BELOW - INVALID SYMBOL: " + input.charAt(currentIndex));
		}
		// Add tokens leading up to error (if there was any) to returner
		for (Token token : tokens) {
			returner.add(token.toString());
		}
		return returner;
	}

	public ArrayList<Token> getTokens() {
		return this.tokens;
	}

}
