
public class BooleanOperationNode extends StatementNode {

	private String op;

	public BooleanOperationNode(String op) {
		this.setOp(op);
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String toString() {
		return op.toString();
	}
}
