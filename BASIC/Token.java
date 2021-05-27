
public class Token {

	public enum Entities {
		PRINT {
			public String toString() {
				return "PRINT";
			}
		},
		IF {
			public String toString() {
				return "IF";
			}
		},
		GOSUB {
			public String toString() {
				return "GOSUB";
			}
		},
		RETURN {
			public String toString() {
				return "RETURN";
			}
		},
		FOR {
			public String toString() {
				return "FOR";
			}
		},
		NEXT {
			public String toString() {
				return "NEXT";
			}
		},
		LABEL {
			public String toString() {
				return "LABEL";
			}
		},
		DATA {
			public String toString() {
				return "DATA";
			}
		},
		READ {
			public String toString() {
				return "READ";
			}
		},
		INPUT {
			public String toString() {
				return "INPUT";
			}
		},
		COMMA {
			public String toString() {
				return "COMMA";
			}
		},
		IDENTIFIER {
			public String toString() {
				return "IDENTIFIER";
			}
		},
		STRING {
			public String toString() {
				return "STRING";
			}
		},
		RPAREN {
			public String toString() {
				return "RPAREN";
			}
		},
		LPAREN {
			public String toString() {
				return "LPAREN";
			}
		},
		NOTEQUAL {
			public String toString() {
				return "NOTEQUAL";
			}
		},
		GREATERTHANOREQUALTO {
			public String toString() {
				return "GREATERTHANOREQUALTO";
			}
		},
		GREATERTHAN {
			public String toString() {
				return "GREATERTHAN";
			}
		},
		LESSTHANOREQUALTO {
			public String toString() {
				return "LESSTHANOREQUALTO";
			}
		},
		LESSTHAN {
			public String toString() {
				return "LESSTHAN";
			}
		},
		EQUALS {
			public String toString() {
				return "EQUALS";
			}
		},
		EOL {
			public String toString() {
				return "EndOfLine";
			}
		},

		NUMBER {
			public String toString() {
				return "NUMBER";
			}
		},

		PLUS {
			public String toString() {
				return "PLUS";
			}
		},

		MINUS {
			public String toString() {
				return "MINUS";
			}
		},

		TIMES {
			public String toString() {
				return "TIMES";
			}
		},

		DIVIDE {
			public String toString() {
				return "DIVIDE";
			}
		}

	}

	private String value;
	private Entities entity;

	public Token() {
		setEntity(Entities.EOL);
		setValue("");
	}
	/*
	 * Add the following symbols = : EQUALS -- < : LESS THAN <=: LESS THAN OR EQUAL
	 * TO > : GREATER THAN >=: GREATER THAN OR EQUAL TO <>: NOT EQUALS ( : L PAREN )
	 * : R PAREN "": STRING : IDENTIFIER KNOWN WORDS HASHMAP
	 */

	public Token(String entity, String value) {
		switch (entity) {
		case "if":
			this.setEntity(Entities.IF);
			this.setValue(" ");
			break;
		case "print":
			this.setEntity(Entities.PRINT);
			this.setValue(" ");
			break;
		case "gosub":
			this.setEntity(Entities.GOSUB);
			this.setValue(" ");
			break;
		case "return":
			this.setEntity(Entities.RETURN);
			this.setValue(" ");
			break;
		case "for":
			this.setEntity(Entities.FOR);
			this.setValue(" ");
			break;
		case "next":
			this.setEntity(Entities.NEXT);
			this.setValue(" ");
			break;
		case "label":
			this.setEntity(Entities.LABEL);
			this.setValue(value);
			break;
		case "read":
			this.setEntity(Entities.READ);
			this.setValue(" ");
			break;
		case "data":
			this.setEntity(Entities.DATA);
			this.setValue(" ");
			break;
		case "input":
			this.setEntity(Entities.INPUT);
			this.setValue(" ");
			break;
		case ",":
			this.setEntity(Entities.COMMA);
			this.setValue(" ");
			break;
		case "identifier":
			this.setEntity(Entities.IDENTIFIER);
			this.setValue(value);
			break;
		case "string":
			this.setEntity(Entities.STRING);
			this.setValue(value);
			break;
		case ")":
			this.setEntity(Entities.RPAREN);
			this.setValue(" ");
			break;
		case "(":
			this.setEntity(Entities.LPAREN);
			this.setValue(" ");
			break;
		case "<>":
			this.setEntity(Entities.NOTEQUAL);
			this.setValue(" ");
			break;
		case ">=":
			this.setEntity(Entities.GREATERTHANOREQUALTO);
			this.setValue(" ");
			break;
		case ">":
			this.setEntity(Entities.GREATERTHAN);
			this.setValue(" ");
			break;
		case "<=":
			this.setEntity(Entities.LESSTHANOREQUALTO);
			this.setValue(" ");
			break;
		case "<":
			this.setEntity(Entities.LESSTHAN);
			this.setValue(" ");
			break;
		case "=":
			this.setEntity(Entities.EQUALS);
			this.setValue(" ");
			break;
		case "eol":
			this.setEntity(Entities.EOL);
			this.setValue(" ");
			break;
		case "number":
			this.setEntity(Entities.NUMBER);
			this.setValue(value);
			break;
		case "plus":
			this.setEntity(Entities.PLUS);
			this.setValue(" ");
			break;
		case "minus":
			this.setEntity(Entities.MINUS);
			this.setValue(" ");
			break;
		case "times":
			this.setEntity(Entities.TIMES);
			this.setValue(" ");
			break;
		case "divide":
			this.setEntity(Entities.DIVIDE);
			this.setValue(" ");
			break;
		default:
			System.out.println("Invalid entity String passed into Token Constructor: " + entity);
		}

	}

	public Entities getEntity() {
		return entity;
	}

	public void setEntity(Entities entity) {
		this.entity = entity;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		String result = "";

		if (this.entity == Entities.NUMBER || this.entity == Entities.STRING || this.entity == Entities.IDENTIFIER
				|| this.entity == Entities.LABEL) {
			result = this.entity.toString() + "(" + value + ") ";
		} else
			result = this.entity.toString() + value;

		return result;
	}

}
