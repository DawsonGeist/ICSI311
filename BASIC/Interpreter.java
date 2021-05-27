import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {

	private HashMap<String, Integer> integerVariables = new HashMap<String, Integer>();
	private HashMap<String, Float> floatVariables = new HashMap<String, Float>();
	private HashMap<String, String> stringVariables = new HashMap<String, String>();
	private HashMap<String, Node> labeledNodeVariables = new HashMap<String, Node>();
	private ArrayList<Node> statements;
	private ArrayList<Node> data;
	private Stack<Node> stack = new Stack<Node>();
	private Scanner scanner = new Scanner(System.in);

	// Pass the list of statements returned from parse into the interpreter
	public Interpreter(ArrayList<Node> statements) {
		this.statements = statements;
		this.init();
	}

	// Add to our data collection
	public void addData(Node e) {
		data.add(e);
	}

	// Getters and Setters
	public HashMap<String, Integer> getIntegerVariables() {
		return integerVariables;
	}

	public void setIntegerVariables(HashMap<String, Integer> integerVariables) {
		this.integerVariables = integerVariables;
	}

	public HashMap<String, Float> getFloatVariables() {
		return floatVariables;
	}

	public void setFloatVariables(HashMap<String, Float> floatVariables) {
		this.floatVariables = floatVariables;
	}

	public HashMap<String, String> getStringVariables() {
		return stringVariables;
	}

	public void setStringVariables(HashMap<String, String> stringVariables) {
		this.stringVariables = stringVariables;
	}

	public HashMap<String, Node> getLabeledNodeVariables() {
		return labeledNodeVariables;
	}

	public void setLabeledNodeVariables(HashMap<String, Node> labeledNodeVariables) {
		this.labeledNodeVariables = labeledNodeVariables;
	}

	// Functions for adding elements to the hash maps
	public void addNewIntegerVariable(String name, int value) {
		this.integerVariables.put(name, value);
	}

	public void addNewFloatVariable(String name, float value) {
		this.floatVariables.put(name, value);
	}

	public void addNewStringVariable(String name, String value) {
		this.stringVariables.put(name, value);
	}

	public void addNewLabledNodeVariable(String name, Node value) {
		this.labeledNodeVariables.put(name, value);
	}

	// Walking functions
	public boolean labeledStatementWalk() {
		// Since there could be nested Label Nodes add a boolean variable that will
		// return true if a label was found
		// and false if no labels are found
		boolean returner = false;
		// Create our dummy visitor node
		StatementNode visitor = null;
		// Create an index variable so we know the current position of current within
		// our statements array
		int index = 0;
		// loop through our ArrayList of Statement Nodes
		for (Node current : statements) {
			// Cast current to Statement Node and assign it to visitor
			visitor = (StatementNode) current;
			// Check if visitor is instance of LabeledStatementNode, if so, do work, else,
			// pass
			if (visitor instanceof LabeledStatementNode) {
				// Replace the current node in statements array with the child of the labeled
				// node
				StatementNode child = ((LabeledStatementNode) visitor).getState();
				statements.set(index, child);
				// Add the Labeled Node to the LabeledNodeVariables HashMap
				this.addNewLabledNodeVariable(((LabeledStatementNode) visitor).getLabel(),
						((LabeledStatementNode) visitor).getState());
				// Set the returner variable to true
				returner = true;
			} else {
				// pass
			}
			index++;
		}
		return returner;
	}

	/*
	 * FOR REFERENCE Index = Location of For node I = location of Next Node J =
	 * Location of LabeledStatement
	 */
	public void forNodeWalk() {
		// Create our dummy visitor node
		StatementNode visitor = null;
		// Create an index variable so we know the current position of current within
		// our statements array
		int index = 0;
		// loop through our ArrayList of Statement Nodes
		for (Node current : statements) {
			// Cast current to Statement Node and assign it to visitor
			visitor = (StatementNode) current;
			// Check if visitor is instance of ForNode, if so, do work, else,
			// pass
			if (visitor instanceof ForNode) {
				// Find Next, set the nextNode property equal to next property within NextNode
				// Use a dummyNode to hold the nodes within statements, so that visitor is not
				// changed (it holds the original for node)
				StatementNode temp = null;
				// Create a dummy variable afterNext, which will be assigned to the nextNode
				// property of the ForNode object

				for (int i = index; i < statements.size(); i++) {
					temp = (StatementNode) statements.get(i);
					// Check that temp is instanceof NextNode
					if (temp instanceof NextNode) {
						// FINISHING THE PARSER SAYS SET FOR NODE-NEXT EQUAL TO NODE AFTER NEXT
						((StatementNode) statements.get(index)).setNextNode(((StatementNode) statements.get(i + 1)));

						// modify the statements array, such that the next node has a nextNode property
						// equal to its next property

						// Next Node holds a variable, so we will search the statements array for a
						// LabeledStatementNode
						// with the same name as the variable node stored within NextNode
						String varName = ((NextNode) statements.get(i)).getNext().getName();
						// Search the statements array for varName
						StatementNode nextNodeDestination = null;
						for (int j = 0; j < statements.size(); j++) {
							nextNodeDestination = (StatementNode) statements.get(i);
							if (nextNodeDestination instanceof LabeledStatementNode) {
								// Check if the LabeledStatementNode has a matching variable name
								if (((LabeledStatementNode) nextNodeDestination).getLabel().equalsIgnoreCase(varName)) {
									// Set the NextNode property of the current NextNode to the LabeledStatementNode
									((StatementNode) statements.get(i))
											.setNextNode(((StatementNode) statements.get(j)));
									// Exit the loop
									break;
								}
								// No matching LabeledStatementsFound, setNextNode to For node
								else if (j == statements.size() - 1) {
									((StatementNode) statements.get(i))
											.setNextNode((StatementNode) statements.get(index));
								}
							}
						}
						// Next Node (i) has been linked, now link the For Node (index) to the node
						// after Next (i+1)
						((StatementNode) statements.get(index)).setNextNode(((StatementNode) statements.get(i + 1)));
					}
				}
			} else {
				// pass
			}
			index++;
		}
	}

	// Data Walk
	public void dataWalk() {
		// Walk statements
		for (Node statement : statements) {
			// Check if the current Node is data node, add its contents to data Array
			if (statement instanceof DataNode) {
				ArrayList<Node> temp = ((DataNode) statement).getList();
				// Add elements to data array
				for (Node data : temp) {
					this.addData(data);
				}
			}
		}
	}

	// Link Walk
	public void linkWalk() {
		StatementNode dummy = null;
		// Walk statements
		for (int i = 0; i < statements.size(); i++) {
			dummy = (StatementNode) statements.get(i);
			// Check if the current Node is has NextNode assigned, else link it to the next
			// node - if end of list, break
			if (i == statements.size() - 1) {
				break;
			} else {
				if (dummy.getNextNode() == null) {
					((StatementNode) statements.get(i)).setNextNode(((StatementNode) statements.get(i + 1)));
				}
			}

		}
	}

	// Init
	public void init() {
		boolean flag = true;
		while (flag) {
			flag = this.labeledStatementWalk();
		}
		this.forNodeWalk();
		this.dataWalk();
		this.linkWalk();
		System.out.println("Interpreter Init completed, Calling Interpret");
		this.interpret(((StatementNode) this.statements.get(0)));
	}

	// Interpret
	@SuppressWarnings("unused")
	public void interpret(StatementNode currentStatement) {
		// If Statements Testing the Type of Statement CurrentStatement is
		if (currentStatement instanceof ReadNode) {
			// Read Node holds a list of VariableNodes, Check the Type of data in data
			// array, add both to the map
			for (Node var : ((ReadNode) currentStatement).getList()) {
				String varName = ((VariableNode) var).getName();
				Node value = null;
				try {
					value = data.remove(0);
				} catch (Exception e) {
					System.out.println(
							"Error In ReadStatement, data was unable to remove value at index 0 :" + e.getMessage());
				}

				// Check for String, Int, Float, Null
				if (value instanceof StringNode) {
					// Check for Duplicate Variables
					if (stringVariables.containsKey(varName)) {
						stringVariables.replace(varName, ((StringNode) value).getValue());
					} else if (integerVariables.containsKey(varName) || floatVariables.containsKey(varName)) {
						System.out.println(
								"Error in ReadStatement: Var " + varName + " already exists, type mismatch error.");
					} else
						stringVariables.put(varName, ((StringNode) value).getValue());
				} else if (value instanceof IntegerNode) {
					// Check that there is not a duplicate variable - Type Mismatch error
					if (stringVariables.containsKey(varName) || floatVariables.containsKey(varName)) {
						System.out.println("Error in AssignmentNode: " + varName
								+ " already exists as a String/Float var. Type Mismatch error");
					} else if (integerVariables.containsKey(varName)) {
						integerVariables.replace(varName, ((IntegerNode) value).getValue());
					} else {
						// Variable Doesn't exist yet, add it to IntegerVariables
						integerVariables.put(varName, ((IntegerNode) value).getValue());
					}
				} else if (value instanceof FloatNode) {
					// Check that there is not a duplicate variable - Type Mismatch error
					if (stringVariables.containsKey(varName) || integerVariables.containsKey(varName)) {
						System.out.println("Error in AssignmentNode: " + varName
								+ " already exists as a String/Int var. Type Mismatch error");
					} else if (floatVariables.containsKey(varName)) {
						floatVariables.replace(varName, ((FloatNode) value).getValue());
					} else {
						// Variable Doesn't exist yet, add it to FloatVariables
						floatVariables.put(varName, ((FloatNode) value).getValue());
					}
				} else {
					System.out.println("Error with read statement: data value was not String, Int, or Float");
				}
			}
			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if (currentStatement instanceof AssignmentNode) {
			// AssignmentNode has a string name and a node value
			Node var = ((AssignmentNode) currentStatement).getVar();
			// Get the String version of the Variable Name
			String varName = ((VariableNode) var).getName();
			// Get the variable value
			Node value = ((AssignmentNode) currentStatement).getValue();
			// Handle the Various Variable Types
			// Also Check if the variable already exists
			if (value instanceof StringNode) {
				// Check that there is not a duplicate variable - Type Mismatch error
				if (integerVariables.containsKey(varName) || floatVariables.containsKey(varName)) {
					System.out.println("Error in AssignmentNode: " + varName
							+ " already exists as a Int/Float var. Type Mismatch error");
				} else if (stringVariables.containsKey(varName)) {
					stringVariables.replace(varName, ((StringNode) value).getValue());
				} else {
					// Variable Doesn't exist yet, add it to StringVariables
					stringVariables.put(varName, ((StringNode) value).getValue());
				}
			} else if (value instanceof IntegerNode) {
				// Check that there is not a duplicate variable - Type Mismatch error
				if (stringVariables.containsKey(varName) || floatVariables.containsKey(varName)) {
					System.out.println("Error in AssignmentNode: " + varName
							+ " already exists as a String/Float var. Type Mismatch error");
				} else if (integerVariables.containsKey(varName)) {
					integerVariables.replace(varName, ((IntegerNode) value).getValue());
				} else {
					// Variable Doesn't exist yet, add it to IntegerVariables
					integerVariables.put(varName, ((IntegerNode) value).getValue());
				}
			} else if (value instanceof FloatNode) {
				// Check that there is not a duplicate variable - Type Mismatch error
				if (stringVariables.containsKey(varName) || integerVariables.containsKey(varName)) {
					System.out.println("Error in AssignmentNode: " + varName
							+ " already exists as a String/Int var. Type Mismatch error");
				} else if (floatVariables.containsKey(varName)) {
					floatVariables.replace(varName, ((FloatNode) value).getValue());
				} else {
					// Variable Doesn't exist yet, add it to FloatVariables
					floatVariables.put(varName, ((FloatNode) value).getValue());
				}
			} else if (value instanceof MathOpNode) {
				float newValue = this.evaluateFloatMathOpNode(value);
				// Check that there is not a duplicate variable - Type Mismatch error
				if (stringVariables.containsKey(varName) || integerVariables.containsKey(varName)) {
					System.out.println("Error in AssignmentNode: " + varName
							+ " already exists as a String/Int var. Type Mismatch error");
				} else if (floatVariables.containsKey(varName)) {
					floatVariables.replace(varName, newValue);
				} else {
					// Variable Doesn't exist yet, add it to FloatVariables
					floatVariables.put(varName, newValue);
				}

			} else {
				System.out.println("Error with input statement: data value was not MathOp, String, Int, or Float");
			}
			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if (currentStatement instanceof InputNode) {
			// Input Node recieves input from the user, if the first Node is a String Node,
			// Print it out
			// Then recieve input from the user, and save it to the appropriate collection,
			// with the key as the variable
			String userInput = "";
			for (Node var : ((InputNode) currentStatement).getList()) {
				if (var instanceof StringNode) {
					System.out.println(((StringNode) var).getValue());
				} else if (var instanceof VariableNode) {
					String varName = ((VariableNode) var).getName();
					System.out.println(varName + ": ");
					userInput = scanner.nextLine();
					// Check for Numeric Values
					Integer intValue = null;
					Float floatValue = null;
					try {
						intValue = Integer.parseInt(userInput);
					} catch (Exception e) {
						// Pass - Input was not an Int
					}
					try {
						floatValue = Float.parseFloat(userInput);
					} catch (Exception e) {
						// Pass - Input was not an Float
					}

					if (intValue != null) {
						// If Int, Check for duplicate or Mismatch error, then add or replace
						// Check that there is not a duplicate variable - Type Mismatch error
						if (stringVariables.containsKey(varName) || floatVariables.containsKey(varName)) {
							System.out.println("Error in InputNode: " + varName
									+ " already exists as a String/Float var. Type Mismatch error");
						} else if (integerVariables.containsKey(varName)) {
							integerVariables.replace(varName, intValue);
						} else {
							// Variable Doesn't exist yet, add it to IntegerVariables
							integerVariables.put(varName, intValue);
						}
					} else if (floatValue != null) {
						// If Float, Check for duplicate or Mismatch error, then add or replace
						// Check that there is not a duplicate variable - Type Mismatch error
						if (stringVariables.containsKey(varName) || integerVariables.containsKey(varName)) {
							System.out.println("Error in InputtNode: " + varName
									+ " already exists as a String/Int var. Type Mismatch error");
						} else if (floatVariables.containsKey(varName)) {
							floatVariables.replace(varName, floatValue);
						} else {
							// Variable Doesn't exist yet, add it to FloatVariables
							floatVariables.put(varName, floatValue);
						}
					} else {
						// Couldnt Parse out a float or int, Must be string
						// Check that there is not a duplicate variable - Type Mismatch error
						if (integerVariables.containsKey(varName) || floatVariables.containsKey(varName)) {
							System.out.println("Error in AssignmentNode: " + varName
									+ " already exists as a Int/Float var. Type Mismatch error");
						} else if (stringVariables.containsKey(varName)) {
							stringVariables.replace(varName, userInput);
						} else {
							// Variable Doesn't exist yet, add it to StringVariables
							stringVariables.put(varName, userInput);
						}
					}
				} else {
					// Node was not String Node or Variable Node; Throw error
					System.out.println(
							"Error in InputStatement: Node within InputStatement was not String or Variable Node: "
									+ var.toString());
				}
			}
			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if (currentStatement instanceof PrintNode) {
			// Print out the Print List
			for (Node valueToPrint : ((PrintNode) currentStatement).getList()) {
				System.out.println(valueToPrint.toString());
			}
			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if (currentStatement instanceof FunctionNode) {
			/*
			 * I do not believe we got to implementing the body of the FunctionNode
			 * 
			 * in the official BASIC documentation there is a token 'End' which is used to
			 * mark the end of a function body declaration. Our Current Function Node doesnt
			 * have a Body member
			 * 
			 * To be honest I do not know how to proceed with the Function Node Since there
			 * is nothing to evaluate
			 * 
			 * From Assignment 7 - Finish the Parser, This is what was written for Function
			 * Invocation Make a new parser method for functionInvocation that looks for the
			 * appropriate keywords and returns a FunctionNode or NULL. A function follows
			 * this pattern: FUNCTIONNAME LPAREN parameterList RPAREN A parameterList is
			 * empty OR a list (1 or more) of EXPRESSION or STRING
			 *
			 * There was no Mention of a Function Body or how to create one. Parameters are
			 * passed into a function, but in a function declaration they are temporary
			 * variables? so I don't want to add them to the collection Which could then
			 * interfere with assignment statements in the future... I do not want to lose
			 * points for not attempting this portion of assignment. Because at the current
			 * state of the program. There is Nothing that can be done that would be
			 * productive and/or wouldnt interfere with the rest of the program.
			 * 
			 */
			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if ((Node) currentStatement instanceof MathOpNode) {
			Integer lI = null;
			Float lF = null;
			Integer rI = null;
			Float rF = null;

			// Evaluate the Node
			if (((MathOpNode) ((Node) currentStatement)).getOp().equalsIgnoreCase("+")) {
				Node left = ((MathOpNode) ((Node) currentStatement)).L;
				Node right = ((MathOpNode) ((Node) currentStatement)).R;
				// Is Left an Int or Float
				if (left instanceof IntegerNode) {
					lI = ((IntegerNode) left).getValue();
				} else if (left instanceof FloatNode) {
					lF = ((FloatNode) left).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					lI = this.evaluateIntMathOpNode(left);
					if (lI == null) {
						lF = this.evaluateFloatMathOpNode(left);
					}
				}
				// Is Right an Int or Float
				if (left instanceof IntegerNode) {
					rI = ((IntegerNode) right).getValue();
				} else if (left instanceof FloatNode) {
					rF = ((FloatNode) right).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					rI = this.evaluateIntMathOpNode(left);
					if (rI == null) {
						rF = this.evaluateFloatMathOpNode(left);
					}
				}
				// We should have values for Left and right
				if (lI != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI + rI));
				} else if (lI != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI + rF));
				} else if (lF != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI + rI));
				} else if (lF != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI + rI));
				}

			} else if (((MathOpNode) ((Node) currentStatement)).getOp().equalsIgnoreCase("-")) {
				Node left = ((MathOpNode) ((Node) currentStatement)).L;
				Node right = ((MathOpNode) ((Node) currentStatement)).R;
				// Is Left an Int or Float
				if (left instanceof IntegerNode) {
					lI = ((IntegerNode) left).getValue();
				} else if (left instanceof FloatNode) {
					lF = ((FloatNode) left).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					lI = this.evaluateIntMathOpNode(left);
					if (lI == null) {
						lF = this.evaluateFloatMathOpNode(left);
					}
				}
				// Is Right an Int or Float
				if (left instanceof IntegerNode) {
					rI = ((IntegerNode) right).getValue();
				} else if (left instanceof FloatNode) {
					rF = ((FloatNode) right).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					rI = this.evaluateIntMathOpNode(left);
					if (rI == null) {
						rF = this.evaluateFloatMathOpNode(left);
					}
				}
				// We should have values for Left and right
				if (lI != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI - rI));
				} else if (lI != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI - rF));
				} else if (lF != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI - rI));
				} else if (lF != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI - rI));
				}

			} else if (((MathOpNode) ((Node) currentStatement)).getOp().equalsIgnoreCase("*")) {
				Node left = ((MathOpNode) ((Node) currentStatement)).L;
				Node right = ((MathOpNode) ((Node) currentStatement)).R;
				// Is Left an Int or Float
				if (left instanceof IntegerNode) {
					lI = ((IntegerNode) left).getValue();
				} else if (left instanceof FloatNode) {
					lF = ((FloatNode) left).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					lI = this.evaluateIntMathOpNode(left);
					if (lI == null) {
						lF = this.evaluateFloatMathOpNode(left);
					}
				}
				// Is Right an Int or Float
				if (left instanceof IntegerNode) {
					rI = ((IntegerNode) right).getValue();
				} else if (left instanceof FloatNode) {
					rF = ((FloatNode) right).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					rI = this.evaluateIntMathOpNode(left);
					if (rI == null) {
						rF = this.evaluateFloatMathOpNode(left);
					}
				}
				// We should have values for Left and right
				if (lI != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI * rI));
				} else if (lI != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI * rF));
				} else if (lF != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI * rI));
				} else if (lF != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI * rI));
				}

			} else if (((MathOpNode) ((Node) currentStatement)).getOp().equalsIgnoreCase("/")) {
				Node left = ((MathOpNode) ((Node) currentStatement)).L;
				Node right = ((MathOpNode) ((Node) currentStatement)).R;
				// Is Left an Int or Float
				if (left instanceof IntegerNode) {
					lI = ((IntegerNode) left).getValue();
				} else if (left instanceof FloatNode) {
					lF = ((FloatNode) left).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					lI = this.evaluateIntMathOpNode(left);
					if (lI == null) {
						lF = this.evaluateFloatMathOpNode(left);
					}
				}
				// Is Right an Int or Float
				if (left instanceof IntegerNode) {
					rI = ((IntegerNode) right).getValue();
				} else if (left instanceof FloatNode) {
					rF = ((FloatNode) right).getValue();
				} else {
					// Child is a mathOpNode
					// Call EvaluateIntMathOpNode -> if it returns null; Call
					// EvaluateFloatMathOpNode
					rI = this.evaluateIntMathOpNode(left);
					if (rI == null) {
						rF = this.evaluateFloatMathOpNode(left);
					}
				}
				// We should have values for Left and right
				if (lI != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI / rI));
				} else if (lI != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI / rF));
				} else if (lF != null && rI != null) {
					System.out.println("Math Op Node result: " + (lI / rI));
				} else if (lF != null && rF != null) {
					System.out.println("Math Op Node result: " + (lI / rI));
				}
			}
			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if (currentStatement instanceof IfNode) {
			// Get arguments
			Node L = ((IfNode) currentStatement).getExp1();
			Node R = ((IfNode) currentStatement).getExp1();
			// Create Holder Variables for Values returned by MathOpNodes
			Float lF = null;
			Float rF = null;
			Integer lI = null;
			Integer rI = null;
			// Get Math Op
			String op = ((IfNode) currentStatement).getOp().toString();
			// Evaluate each side
			if (L instanceof MathOpNode) {
				lF = this.evaluateFloatMathOpNode(L);
			}
			if (R instanceof MathOpNode) {
				rF = this.evaluateFloatMathOpNode(L);
			}
			// Now Check if Each side is a variable
			if (L instanceof VariableNode) {
				// Check int and float tables
				lI = integerVariables.get(((VariableNode) L).getName());
				lF = floatVariables.get(((VariableNode) L).getName());
			}
			if (R instanceof VariableNode) {
				// Check int and float tables
				rI = integerVariables.get(((VariableNode) R).getName());
				rF = floatVariables.get(((VariableNode) R).getName());
			}
			// Now Evaluate
			if (op.equals(">")) {
				// We should have values for Left and right
				if (lI != null && rI != null) {
					if (lI > rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lI != null && rF != null) {
					if (lI > rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rI != null) {
					if (lF > rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rF != null) {
					if (lF > rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				}
			} else if (op.equals(">=")) {
				// We should have values for Left and right
				if (lI != null && rI != null) {
					if (lI >= rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lI != null && rF != null) {
					if (lI >= rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rI != null) {
					if (lF >= rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rF != null) {
					if (lF >= rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				}
			} else if (op.equals("<")) {
				// We should have values for Left and right
				if (lI != null && rI != null) {
					if (lI < rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lI != null && rF != null) {
					if (lI < rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rI != null) {
					if (lF < rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rF != null) {
					if (lF < rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				}
			} else if (op.equals("<=")) {
				// We should have values for Left and right
				if (lI != null && rI != null) {
					if (lI <= rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lI != null && rF != null) {
					if (lI <= rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rI != null) {
					if (lF <= rI) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rF != null) {
					if (lF <= rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				}
			} else if (op.equals("<>")) {
				// We should have values for Left and right
				if (lI != null && rI != null) {
					if (lI.floatValue() != rI.floatValue()) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lI != null && rF != null) {
					if (lI.floatValue() != rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rI != null) {
					if (lF != rI.floatValue()) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rF != null) {
					if (lF != rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				}

			} else if (op.equals("=")) {
				// We should have values for Left and right
				if (lI != null && rI != null) {
					if (lI.floatValue() == rI.floatValue()) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lI != null && rF != null) {
					if (lI.floatValue() == rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rI != null) {
					if (lF == rI.floatValue()) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				} else if (lF != null && rF != null) {
					if (lF == rF) {
						System.out.println("True");
						// If true, set the currentNode to the FILL IN --- I Dont know what FILL IN is.
					} else {
						System.out.println("Flase");
					}
				}
			} else {
				System.out.print("Error: Operands were not float/int");
			}

			// Call interpret on nextNode, if nextNode = null, exit
			if (currentStatement.getNextNode() == null) {
				System.out.println("Current Statement has null nextNode, exiting program.");
			} else {
				this.interpret(currentStatement.getNextNode());
			}
		} else if (currentStatement instanceof GosubNode) {
			// push next statement onto stack
			stack.push(currentStatement.getNextNode());
			// Look Up Label
			String label = ((GosubNode) currentStatement).getID();
			Node gotoNode = labeledNodeVariables.get(label);
			// Test if its valid
			if (gotoNode == null) {
				System.out.println("Error in Gosub Node: No Labeled Statement Found");
				this.interpret((StatementNode) stack.pop());
			} else {
				// Jump to the gotoNode from the goSubNode label
				this.interpret((StatementNode) gotoNode);
			}
		} else if (currentStatement instanceof ReturnNode) {
			// Pop Stack and call Interpret
			Node next = stack.pop();
			if (next == null) {
				System.out.println("Error in return statement: Stack is empty");
			} else {
				this.interpret((StatementNode) next);
			}
		} else if (currentStatement instanceof ForNode) {
			// I Didnt have much programmed for For Node, I Dont believe that was reflected
			// in my Ruberic.
			// I Am going to increment the counter then jump to the next node. The max will
			// be Increment, which is not
			// How it is actually supposed to work. I understand this.
			int max = ((ForNode) currentStatement).getIncrement();
			for (int i = 0; i < max; i++) {
				// Get the node after for
				try {
					this.interpret((StatementNode) statements.get((statements.indexOf(currentStatement)) + 1));
				} catch (Exception e) {
					System.out.println(
							"Error in for loop, trying to grab node that is out of bounds of the statements array.");
				}
			}
			// At the end, Jump to Next Node
			this.interpret(currentStatement.getNextNode());
		} else if (currentStatement instanceof NextNode) {
			// This is going to cause an infinite loop but it is what the ruberic states to
			// do and the ruberic is law
			// NextNode – Set the currentNode to the ForNode.
			this.interpret(currentStatement.getNextNode());
		}
		// end of interpret Method
	}

	// Evaluate Int Math Op Node
	public Integer evaluateIntMathOpNode(Node input) {
		Integer returner = null;
		if (input instanceof IntegerNode) {
			return ((IntegerNode) input).getValue();
		} else if (input instanceof FloatNode) {
			return null;
		} else {
			// MathOpNode
			Float lF = null;
			Float rF = null;
			Integer left = this.evaluateIntMathOpNode(((MathOpNode) input).L);
			Integer right = this.evaluateIntMathOpNode(((MathOpNode) input).R);

			if (left == null) {
				lF = this.evaluateFloatMathOpNode(((MathOpNode) input).L);
			}
			if (right == null) {
				rF = this.evaluateFloatMathOpNode(((MathOpNode) input).R);
			}

			if (((MathOpNode) input).getOp() == "+") {
				if (left != null && right != null) {
					returner = (left + right);
				} else if (left != null && rF != null) {
					returner = (int) (left + rF);
				} else if (lF != null && right != null) {
					returner = (int) (lF + right);
				} else if (lF != null && rF != null) {
					returner = (int) (lF + rF);
				}
			} else if (((MathOpNode) input).getOp() == "*") {
				if (left != null && right != null) {
					returner = (left * right);
				} else if (left != null && rF != null) {
					returner = (int) (left * rF);
				} else if (lF != null && right != null) {
					returner = (int) (lF * right);
				} else if (lF != null && rF != null) {
					returner = (int) (lF * rF);
				}
			} else if (((MathOpNode) input).getOp() == "/") {
				if (left != null && right != null) {
					returner = (int) (left / right);
				} else if (left != null && rF != null) {
					returner = (int) (left / rF);
				} else if (lF != null && right != null) {
					returner = (int) (lF / right);
				} else if (lF != null && rF != null) {
					returner = (int) (lF / rF);
				}
			} else if (((MathOpNode) input).getOp() == "-") {
				if (left != null && right != null) {
					returner = (left - right);
				} else if (left != null && rF != null) {
					returner = (int) (left - rF);
				} else if (lF != null && right != null) {
					returner = (int) (lF - right);
				} else if (lF != null && rF != null) {
					returner = (int) (lF - rF);
				}
			}
		}
		return returner;
	}

	// Evaluate Float Math Op Node
	public Float evaluateFloatMathOpNode(Node input) {
		Float returner = null;
		if (input instanceof FloatNode) {
			return ((FloatNode) input).getValue();
		} else if (input instanceof IntegerNode) {
			return null;
		} else {
			// MathOpNode
			Integer lI = null;
			Integer rI = null;
			Float left = this.evaluateFloatMathOpNode(((MathOpNode) input).L);
			Float right = this.evaluateFloatMathOpNode(((MathOpNode) input).R);

			if (left == null) {
				lI = this.evaluateIntMathOpNode(((MathOpNode) input).L);
			}
			if (right == null) {
				rI = this.evaluateIntMathOpNode(((MathOpNode) input).R);
			}

			if (((MathOpNode) input).getOp() == "+") {
				if (left != null && right != null) {
					returner = (left + right);
				} else if (left != null && rI != null) {
					returner = (float) (left + rI);
				} else if (lI != null && right != null) {
					returner = (float) (lI + right);
				} else if (lI != null && rI != null) {
					returner = (float) (lI + rI);
				}
			} else if (((MathOpNode) input).getOp() == "*") {
				if (left != null && right != null) {
					returner = (left * right);
				} else if (left != null && rI != null) {
					returner = (float) (left * rI);
				} else if (lI != null && right != null) {
					returner = (float) (lI * right);
				} else if (lI != null && rI != null) {
					returner = (float) (lI * rI);
				}
			} else if (((MathOpNode) input).getOp() == "/") {
				if (left != null && right != null) {
					returner = (left / right);
				} else if (left != null && rI != null) {
					returner = (float) (left / rI);
				} else if (lI != null && right != null) {
					returner = (float) (lI / right);
				} else if (lI != null && rI != null) {
					returner = (float) (lI / rI);
				}
			} else if (((MathOpNode) input).getOp() == "-") {
				if (left != null && right != null) {
					returner = (left - right);
				} else if (left != null && rI != null) {
					returner = (float) (left - rI);
				} else if (lI != null && right != null) {
					returner = (float) (lI - right);
				} else if (lI != null && rI != null) {
					returner = (float) (lI - rI);
				}
			}
		}
		return returner;
	}
}
