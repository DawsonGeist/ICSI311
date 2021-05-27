
public class IfNode extends StatementNode {

	private Node exp1;
	private Node exp2;
	private BooleanOperationNode op;

	public IfNode(Node exp1, Node exp2, BooleanOperationNode op) {
		this.setExp1(exp1);
		this.setExp2(exp2);
		this.setOp(op);
	}

	public Node getExp1() {
		return exp1;
	}

	public void setExp1(Node exp1) {
		this.exp1 = exp1;
	}

	public Node getExp2() {
		return exp2;
	}

	public void setExp2(Node exp2) {
		this.exp2 = exp2;
	}

	public BooleanOperationNode getOp() {
		return op;
	}

	public void setOp(BooleanOperationNode op) {
		this.op = op;
	}

	public String toString() {
		return ("If(" + exp1.toString() + " " + op.toString() + " " + exp2.toString() + ")");
	}

}
