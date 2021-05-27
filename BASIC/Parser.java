import java.util.ArrayList;

public class Parser {

	private ArrayList<Token> input;

	public Parser(ArrayList<Token> input) {
		this.input = input;
	}

	/*
	 * Example inputs: (3 * (5 + (3 * (4 - 1))))
	 * 
	 * (3 + 5) * 7
	 */

	public Node parse() {
		// Edited this to call Statements in stead of expressions, logic below is for
		// expressions
		Node returner = this.statements();
		// If we happen to find ourselves with a satisfied expression function, but our
		// input Token list is not empty, then we recursively call parse again, save
		// that
		// node, and put them together into a giant AstTree via MathOpNode

		/*
		 * This on the assumption that this condition is met when we have a "Full"
		 * mathOpNode and the remaining input Array is longer than 1 and ASSUMES it has
		 * the next operation at index 0...
		 * 
		 * This has been tested pretty harshly and it works for inputs that dont throw
		 * syntax errors
		 * 
		 * So im assuming it covers border cases
		 */
		// if (this.input.size() != 1) {
		// There is more to be parsed!
		// There should be an Operation Below Us
		// Token op = this.input.remove(0);
		// Get the second operand
		// Node round2 = this.parse();
		// MathOpNode AstTree = new MathOpNode(op);
		// AstTree.setL(returner);
		// AstTree.setR(round2);
		// returner = AstTree;
		// }
		return returner;
	}

	public Node statements() {
		// Create my statement node list
		ArrayList<Node> states = new ArrayList<Node>();

		// Loop until statement returns null
		StatementNode temp = this.statement();
		while (temp != null) {
			states.add(temp);
			temp = this.statement();
		}

		/*
		 * FROM ASSIGNMENT 3 :/ This is a border case where the MathOpNode needs to be
		 * expanded
		 * 
		 * Since parse calls statements in this edition of the project, we have to call
		 * expressions explicitly in line 81
		 * 
		 * This on the assumption that this condition is met when we have a "Full"
		 * mathOpNode and the remaining input Array is longer than 1 and ASSUMES it has
		 * the next operation at index 0...
		 * 
		 * This has been tested pretty harshly and it works for inputs that dont throw
		 * syntax errors
		 * 
		 * So im assuming it covers border cases
		 */
		if (this.input.size() != 1 && this.input.get(0).getEntity() == Token.Entities.DIVIDE
				|| this.input.get(0).getEntity() == Token.Entities.PLUS
				|| this.input.get(0).getEntity() == Token.Entities.MINUS
				|| this.input.get(0).getEntity() == Token.Entities.TIMES) {
			// There is more to be parsed!
			// There should be an Operation Below Us
			Token op = this.input.remove(0);
			// Get the second operand
			Node round2 = this.expression();
			MathOpNode math = new MathOpNode(op);
			math.setL(((AssignmentNode) states.get(states.size() - 1)).getValue());
			math.setR(round2);
			VariableNode n = (VariableNode) ((AssignmentNode) states.get(states.size() - 1)).getVar();
			AssignmentNode leftOver = new AssignmentNode(n, math);

			// pop last assignmentNode
			states.remove(states.size() - 1);
			// add the updated node
			states.add(leftOver);
		}

		// Create the AST tree and return
		StatementsNode astTree = new StatementsNode(states);
		return astTree;
	}

	public StatementNode statement() {
		StatementNode returner = null;

		// Check for a print token
		Token print = new Token("print", "");
		// Check for identifier
		Token id = new Token("identifier", "");
		// Check for read
		Token read = new Token("read", "");
		// Check for input
		Token inputToken = new Token("input", "");
		// Check for data
		Token data = new Token("data", "");
		// Check for label
		Token label = new Token("label", "");
		// Check for gosub
		Token gosub = new Token("gosub", "");
		// Check for Return
		Token ret = new Token("return", "");
		// Check for for
		Token f = new Token("for", "");
		// Check for next
		Token next = new Token("next", "");
		// num token
		Token num = new Token("number", "0");
		// Check for IF
		Token ifStatement = new Token("if", "");
		// Check for <
		Token lessThan = new Token("<", "");
		// Check for <=
		Token lessThanEqual = new Token("<=", "");
		// Check for >
		Token greaterThan = new Token(">", "");
		// Check for >=
		Token greaterThanEqual = new Token(">=", "");
		// Check for =
		Token equal = new Token("=", "");
		// Check for <>
		Token notEqual = new Token("<>", "");

		// Check for if statement
		Token input = this.matchAndRemove(ifStatement);
		if (input != null) {
			// Create String op
			BooleanOperationNode op = null;
			// Get first expression
			Node exp1 = this.expression();
			// Check for our boolean operators
			input = this.matchAndRemove(lessThan);
			if (input != null) {
				op = new BooleanOperationNode(input.getEntity().toString());
			}
			input = this.matchAndRemove(lessThanEqual);
			if (input != null) {
				op = new BooleanOperationNode(input.getEntity().toString());
			}
			input = this.matchAndRemove(greaterThan);
			if (input != null) {
				op = new BooleanOperationNode(input.getEntity().toString());
			}
			input = this.matchAndRemove(greaterThanEqual);
			if (input != null) {
				op = new BooleanOperationNode(input.getEntity().toString());
			}
			input = this.matchAndRemove(equal);
			if (input != null) {
				op = new BooleanOperationNode(input.getEntity().toString());
			}
			input = this.matchAndRemove(notEqual);
			if (input != null) {
				op = new BooleanOperationNode(input.getEntity().toString());
			}
			// Check that operator was found
			if (op != null) {
				Node exp2 = this.expression();
				returner = this.ifStatement(exp1, exp2, op);
			} else {
				// No operator found
				System.out.println("Error in Statment Method, IF Statement Found, No Operator Found at current token: "
						+ this.input.get(0).toString());
			}
		}

		// Retrieve the label token from the token list
		input = this.matchAndRemove(label);
		// Label found, call statement
		if (input != null) {
			returner = new LabeledStatementNode(input.getValue());
			StatementNode labeledStatement = this.statement();
			((LabeledStatementNode) returner).setState(labeledStatement);
		}

		// Retrieve the gosub token from the token list
		input = this.matchAndRemove(gosub);
		// gosub found,
		if (input != null) {
			input = this.matchAndRemove(id);
			if (input != null) {
				returner = this.gosubStatement(input);
			} else {
				System.out.println("Gosub token found, following token was not identifier");
			}
		}

		// Retrieve the return token from the token list
		input = this.matchAndRemove(ret);
		// return found,
		if (input != null) {
			returner = this.returnStatement();
		}

		// Retrieve the for token from the token list
		input = this.matchAndRemove(f);
		// for found,
		if (input != null) {
			// Check for int node
			input = this.matchAndRemove(num);
			if (input != null) {
				int stepVal = 1;
				try {
					stepVal = Integer.parseInt(input.getValue());
				} catch (Exception e) {
					stepVal = (int) Float.parseFloat(input.getValue());
				}
				returner = this.forStatement(stepVal);
			} else {
				returner = this.forStatement(1);
			}
		}

		// Retrieve the next token from the token list
		input = this.matchAndRemove(next);
		// next found,
		if (input != null) {
			input = this.matchAndRemove(id);
			if (input != null) {
				returner = this.nextStatement(input.getValue());
			} else {
				System.out.println("Next token found, following token was not identifier.");
			}
		}

		// Retrieve the print token from the token list
		input = this.matchAndRemove(print);
		// Print found, call printStatement
		if (input != null) {
			returner = this.printStatement();
		}

		// Retrieve the read token from the token list
		input = this.matchAndRemove(read);
		// Read found, call readStatement
		if (input != null) {
			returner = this.readStatement();
		}

		// Retrieve the input token from the token list
		input = this.matchAndRemove(inputToken);
		// input found, call inputStatement
		if (input != null) {
			returner = this.inputStatement();
		}

		// Retrieve the data token from the token list
		input = this.matchAndRemove(data);
		// data found, call dataStatement
		if (input != null) {
			returner = this.dataStatement();
		}

		// Check for Assignment Node OR FUNCTION INVOCATION
		// Retrieve the print token from the token list
		input = this.matchAndRemove(id);
		// Identifier found, call assignment
		if (input != null) {
			// Check if the next token is =, if so call assignment
			if (this.input.get(0).getEntity() == equal.getEntity())
				// Since the identifier is removed from the tokens, pass the variable name to
				// the
				// Assignment method
				returner = this.assignment(input.getValue());
			// Check for L Parenthesis
			else if (this.input.get(0).getEntity() == Token.Entities.LPAREN) {
				returner = this.functionInvocation(input.getValue());
			} else {
				System.out.println("Error in Statement function, Identifier found, Next token was not ( or = : "
						+ this.input.get(0).getEntity().toString());
			}
		}
		return returner;
	}

	private StatementNode functionInvocation(String name) {
		StatementNode returner = null;
		// There is a left parenthesis at the beginning of our input, get rid of it,
		// then check for ) or acceptable tokens until ) is found
		this.input.remove(0);
		// Create our arraylist of Nodes
		ArrayList<Node> params = new ArrayList<Node>();
		// Loop through our tokens until we hit a )
		while (this.input.get(0).getEntity() != Token.Entities.RPAREN) {
			// Check for ,
			if (this.input.get(0).getEntity() == Token.Entities.COMMA) {
				// remove comma
				this.input.remove(0);
			}
			// add our parameters to params
			params.add(this.expression());
		}
		try {
			returner = new FunctionNode(name, params);
		} catch (Exception e) {
			System.out.println("Error in functionInvocation Method: Unable to make FunctionNode");
		}
		// Discard the current token, since it is a right parenthesis
		this.input.remove(0);
		return returner;
	}

	private StatementNode ifStatement(Node exp1, Node exp2, BooleanOperationNode op) {
		return new IfNode(exp1, exp2, op);
	}

	private StatementNode gosubStatement(Token id) {
		return new GosubNode(id.getValue());
	}

	private StatementNode returnStatement() {
		return new ReturnNode();
	}

	private StatementNode forStatement(int step) {
		return new ForNode(step);
	}

	private StatementNode nextStatement(String next) {
		VariableNode temp = new VariableNode(next);
		return new NextNode(temp);
	}

	private StatementNode dataStatement() {
		// DATA takes a list of String Float and Int

		StatementNode returner = null;
		// Check for a comma token
		Token comma = new Token(",", "");
		// print list
		ArrayList<Node> printlist = new ArrayList<Node>();
		// Since a print statement was found, we create a comma token so that we can
		// Enter the following loop. we are looking for one or more expressions
		Token input = new Token(",", "");

		// Loop until we are at the end of the input
		while (input != null) {
			// We gathered the expression
			Node exp = this.expression();
			// Try and get comma
			input = this.matchAndRemove(comma);
			// Comma was found
			if (input != null) {
				// Call exp again by looping
				// Add current expression to list if it is a variable, else throw error skip
				// element
				if (exp instanceof StringNode || exp instanceof FloatNode || exp instanceof IntegerNode) {
					printlist.add(exp);
				} else {
					System.out.println(
							"Error Within dataStatement(): expression returned a node that was not a Variable, Float, or Int\nExpression Value: "
									+ exp.toString());
				}
			}
			// End of the list... Caution, expression could be something that is not
			// variable and may be important!
			else {
				// Add current expression to list if it is a variable float or int, else throw
				// error skip
				// element
				if (exp instanceof StringNode || exp instanceof FloatNode || exp instanceof IntegerNode) {
					printlist.add(exp);
				} else {
					System.out.println(
							"Error Within dataStatement(): expression returned a node that was not aVariable, Float, or Int\nExpression Value: "
									+ exp.toString());
				}
				// Create the printNode
				returner = new PrintNode(printlist);
			}
		}
		returner = new DataNode(printlist);
		return returner;
	}

	private StatementNode inputStatement() {
		// input takes a list of variables, the first however can be a String or
		// Variable
		int counter = 0;
		StatementNode returner = null;
		// Check for a comma token
		Token comma = new Token(",", "");
		// print list
		ArrayList<Node> printlist = new ArrayList<Node>();
		// Since a print statement was found, we create a comma token so that we can
		// Enter the following loop. we are looking for one or more expressions
		Token input = new Token(",", "");

		// Loop until we are at the end of the input
		while (input != null) {
			// We gathered the expression
			Node exp = this.expression();
			// Try and get comma
			input = this.matchAndRemove(comma);
			// Comma was found
			if (input != null) {
				// Call exp again by looping
				// Add current expression to list if it is a variable, else throw error skip
				// element
				if (exp instanceof VariableNode && counter == 0 || exp instanceof StringNode && counter == 0) {
					printlist.add(exp);
					counter++;
				} else if (exp instanceof VariableNode) {
					printlist.add(exp);
					counter++;
				} else {
					System.out.println(
							"Error Within inputStatement(): expression returned a node that was not a Variable\nExpression Value: "
									+ exp.toString());
					counter++;
				}
			}
			// End of the list... Caution, expression could be something that is not
			// variable and may be important!
			else {
				// Add current expression to list if it is a variable, else throw error skip
				// element
				if (exp instanceof VariableNode) {
					printlist.add(exp);
				} else {
					System.out.println(
							"Error Within inputStatement(): expression returned a node that was not a Variable\nExpression Value: "
									+ exp.toString());
				}
				// Create the printNode
				returner = new PrintNode(printlist);
			}
		}
		returner = new InputNode(printlist);
		return returner;
	}

	private StatementNode readStatement() {
		// READ takes a list of variables

		StatementNode returner = null;
		// Check for a comma token
		Token comma = new Token(",", "");
		// print list
		ArrayList<Node> printlist = new ArrayList<Node>();
		// Since a print statement was found, we create a comma token so that we can
		// Enter the following loop. we are looking for one or more expressions
		Token input = new Token(",", "");

		// Loop until we are at the end of the input
		while (input != null) {
			// We gathered the expression
			Node exp = this.expression();
			// Try and get comma
			input = this.matchAndRemove(comma);
			// Comma was found
			if (input != null) {
				// Call exp again by looping
				// Add current expression to list if it is a variable, else throw error skip
				// element
				if (exp instanceof VariableNode) {
					printlist.add(exp);
				} else {
					System.out.println(
							"Error Within readStatement(): expression returned a node that was not a Variable\nExpression Value: "
									+ exp.toString());
				}
			}
			// End of the list... Caution, expression could be something that is not
			// variable and may be important!
			else {
				// Add current expression to list if it is a variable, else throw error skip
				// element
				if (exp instanceof VariableNode) {
					printlist.add(exp);
				} else {
					System.out.println(
							"Error Within readStatement(): expression returned a node that was not a Variable\nExpression Value: "
									+ exp.toString());
				}
				// Create the printNode
				returner = new PrintNode(printlist);
			}
		}
		returner = new ReadNode(printlist);
		return returner;
	}

	public StatementNode assignment(String varName) {
		VariableNode variable = new VariableNode(varName);
		// Our returning variable
		AssignmentNode returner = null;
		// Check for equals
		Token equals = new Token("=", "");
		Token input = this.matchAndRemove(equals);
		// Equals not found, throw error
		if (input == null) {
			System.out.println("Err in assignment method: No = token found immediately after identifier");
		}
		// Equals has been found and removed, expression should be left
		else {
			// We gathered the expression
			Node exp = this.expression();
			// Was the node returned by expression a variable?
			if (exp instanceof VariableNode) {
				// expression returned a variable
				Token var = new Token("identifier", ((VariableNode) exp).getName());
				this.input.add(0, var);
				// Create the Assignment node and return
				returner = new AssignmentNode(variable, exp);
			} else if (exp instanceof MathOpNode) {
				returner = new AssignmentNode(variable, exp);
			}
			// This is an edge case for expression function.. we first have to check for
			// Comma if it is present. If comma is not present, go do this
			else if (this.input.size() != 1) {
				// There is more to be parsed!
				// There should be an Operation Below Us
				Token op = this.input.remove(0);
				// Get the second operand
				Node round2 = this.parse();
				MathOpNode AstTree = new MathOpNode(op);
				AstTree.setL(returner);
				AstTree.setR(round2);
				exp = AstTree;
				// Create the Assignment node and return
				returner = new AssignmentNode(variable, exp);
			}
			// input size = 1, add exp and return
			else {
				// Create the Assignment node and return
				returner = new AssignmentNode(variable, exp);
			}
		}
		return returner;
	}

	public StatementNode printStatement() {
		StatementNode returner = null;
		// Check for a comma token
		Token comma = new Token(",", "");
		// print list
		ArrayList<Node> printlist = new ArrayList<Node>();
		// Since a print statement was found, we create a comma token so that we can
		// Enter the following loop. we are looking for one or more expressions
		Token input = new Token(",", "");

		// Loop until we are at the end of the input
		while (input != null) {
			// We gathered the expression
			Node exp = this.expression();
			// Try and get comma
			input = this.matchAndRemove(comma);
			// Comma was found
			if (input != null) {
				// Call exp again by looping
				// Add current expression to list
				printlist.add(exp);
			}
			// Was the node returned by expression a variable?
			// else if (exp instanceof VariableNode) {
			// Since the token was removed, we have to create a new identifier token
			// and add it to the beginning of the token list
			// Token var = new Token("identifier", ((VariableNode) exp).getName());
			// this.input.add(0, var);
			// Create the print node and return
			// returner = new PrintNode(printlist);
			// }
			// Was the node returned by expression a String?
			else if (exp instanceof StringNode) {
				printlist.add(exp);
			}
			// This is an edge case for expression function.. we first have to check for
			// Comma if it is present. If comma is not present, go do this
			else if (this.input.size() != 1) {
				// There is more to be parsed!
				// There should be an Operation Below Us
				Token op = this.input.remove(0);
				// Get the second operand
				Node round2 = this.parse();
				MathOpNode AstTree = new MathOpNode(op);
				AstTree.setL(returner);
				AstTree.setR(round2);
				exp = AstTree;
				printlist.add(exp);
			}
			// Comma was not found, and input size = 1, add exp and return
			else {
				printlist.add(exp);
				// Create the printNode
				returner = new PrintNode(printlist);
			}
		}
		returner = new PrintNode(printlist);
		return returner;
	}

	public Token matchAndRemove(Token target) {
		Token returner = input.get(0);
		if (returner.getEntity() == target.getEntity()) {
			input.remove(0);
			return returner;
		} else
			return null;
	}

	/*
	 * EXPRESSION = TERM { (plus or minus) TERM} TERM = FACTOR { (times or divide)
	 * FACTOR} FACTOR = number or lparen EXPRESSION rparen
	 * 
	 * HOW DO WE HANDLE FINDING A VARIABLE NODE
	 */

	public Node expression() {
		// First check to see if the Term function returns a Value! Then check for Plus
		// or minus
		Node input = term();
		// Check for var
		if (input instanceof VariableNode) {
			return input;
		}
		// Check for String
		else if (input instanceof StringNode) {
			return input;
		} else if (input != null) {
			// Identity tokens that will be passed to matchAndRemove
			Token add = new Token("plus", "");
			Token subtract = new Token("minus", "");

			Token op = this.matchAndRemove(add);

			/*
			 * if plus is found, call term, save the node (if its not null), then create a
			 * MathOpNode and return it, Same thing for minus
			 */
			if (op != null) {
				Node input2 = this.term();
				// Check for null node
				if (input2 == null) {
					System.out.println("Term Fxn Error: Second Term function returned null, returning null");
					return null;
				} else {
					// Create mathOpNode and attach children then return it
					MathOpNode returner = new MathOpNode(op);
					returner.setL(input);
					returner.setR(input2);
					return returner;
				}
			}

			// plus was not found, search for minus
			op = this.matchAndRemove(subtract);

			if (op != null) {
				Node input2 = this.term();
				// Check for null node
				if (input2 == null) {
					System.out.println("Term Fxn Error: Second Term function returned null, returning null");
					return null;
				} else {
					// Create mathOpNode and attach children then return it
					MathOpNode returner = new MathOpNode(op);
					returner.setL(input);
					returner.setR(input2);
					return returner;
				}
			}
		} // If our first Term function call returned null, print error and return null
		else {
			System.out.println("Expression Error: first term function returned null.");
			return null;
		}

		return input;
	}

	public Node term() {
		// Call Factor
		Node input = this.factor();

		// if Factor returns null, return null
		if (input == null) {
			System.out.println("Term Fxn Error: Factor function returned null, returning null");
			return null;
		}
		// Check for VarNode
		else if (input instanceof VariableNode) {
			return input;
		}
		// Check for StrNode
		else if (input instanceof StringNode) {
			return input;
		}
		// If factor returned a number, look for * or /
		else {
			// We will pass these identity tokens to matchAndRemove so it knows what to look
			// for
			Token times = new Token("times", "");
			Token divide = new Token("divide", "");

			Token op = this.matchAndRemove(times);
			/*
			 * if times is found, call factor, save the node (if its not null), then create
			 * a MathOpNode and return it, Same thing for divide
			 */
			if (op != null) {
				Node input2 = this.factor();
				// Check for null node
				if (input2 == null) {
					System.out.println("Term Fxn Error: Second Factor function returned null, returning null");
					return null;
				} else {
					// Create mathOpNode and attach children then return it
					MathOpNode returner = new MathOpNode(op);
					returner.setL(input);
					returner.setR(input2);
					return returner;
				}
			}

			// Check for divide symbol
			op = this.matchAndRemove(divide);
			if (op != null) {
				Node input2 = this.factor();
				// Check for null node
				if (input2 == null) {
					System.out.println("Term Fxn Error: Second Factor function returned null, returning null");
					return null;
				} else {
					// Create mathOpNode and attach children then return it
					MathOpNode returner = new MathOpNode(op);
					returner.setL(input);
					returner.setR(input2);
					return returner;
				}
			}
			// If op is null, then return the number from factor (input)
			return input;
		}
	}

	// FACTOR = number or lparen EXPRESSION rparen
	public Node factor() {
		// Check for a number token
		Token number = new Token("number", "0");
		// Check for a lparen token
		Token lparen = new Token("(", "");
		// Check for a rparen token
		Token rparen = new Token(")", "");
		// Check for IDENTIFIER
		Token id = new Token("identifier", "");
		// Check for STRING
		Token str = new Token("string", "");
		// Check for LABEL
		Token label = new Token("label", "");

		// save our response from matchAndRemove

		Token input = this.matchAndRemove(number);

		// Check for the number token first
		if (input != null) {
			// input is a number, so check if it is a float number
			if (input.getValue().indexOf(".") != -1) {
				// Generate the float value and return it
				float value = Float.parseFloat(input.getValue());
				FloatNode returner = new FloatNode(value);
				return returner;
			} else {
				// Generate the Int and Return it
				int value = Integer.parseInt(input.getValue());
				IntegerNode returner = new IntegerNode(value);
				return returner;
			}
		}

		// Check for LParenThesis
		input = this.matchAndRemove(lparen);
		if (input != null) {
			// Call Expression then look for right parenthesis
			Node returner = this.expression();
			input = this.matchAndRemove(rparen);

			// if rparen is not found throw exception, else return partial
			if (input != null) {
				return returner;
			} else {
				System.out.println("ERROR: Found ( but there was no ) to close it.");
				return null;
			}
		}

		// Check for ID
		input = this.matchAndRemove(id);
		if (input != null) {
			VariableNode var = new VariableNode(input.getValue());
			return var;
		}

		// Check for String
		input = this.matchAndRemove(str);
		if (input != null) {
			StringNode stringToken = new StringNode(input.getValue());
			return stringToken;
		}

		// Check for label
		input = this.matchAndRemove(label);
		if (input != null) {
			StringNode stringToken = new StringNode(input.getValue());
			return stringToken;
		}
		// If there is no L paren then their is an invalid token in the list
		else {
			System.out.println("ERROR: Invalid Input, not #, Operation, IDENTIFIER, STRING, or ()");
			return null;
		}
	}

}
