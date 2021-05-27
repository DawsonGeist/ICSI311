
public class MathOpNode extends Node {

	public enum operations {
		ADD {
			public String toString() {
				return "+";
			}
		},
		SUBTRACT {
			public String toString() {
				return "-";
			}
		},
		MULTIPLY {
			public String toString() {
				return "*";
			}
		},
		DIVIDE {
			public String toString() {
				return "/";
			}
		},
	}

	public Node L;
	public Node R;

	private operations op;

	public MathOpNode(Token operation) {
		switch (operation.toString()) {
		case "PLUS ":
			this.op = operations.ADD;
			break;
		case "SUBTRACT ":
			this.op = operations.SUBTRACT;
			break;
		case "TIMES ":
			this.op = operations.MULTIPLY;
			break;
		case "DIVIDE ":
			this.op = operations.DIVIDE;
			break;
		default:
			System.out.println("Invalid Case");
			this.op = operations.ADD;
			break;
		}
		this.L = null;
		this.R = null;
	}

	public void setL(Node l) {
		this.L = l;
	}

	public void setR(Node r) {
		this.R = r;
	}

	public String getOp() {
		return this.op.toString();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (this.L == null || this.R == null) {
			System.out.println("MathOpNode has a null child still");
			return "";
		} else {
			String returner = "MathOpNode(" + this.getOp() + ", " + this.L.toString() + ", " + this.R.toString() + ")";
			return returner;
		}
	}

}
