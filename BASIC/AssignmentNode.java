
public class AssignmentNode extends StatementNode {

	private Node var;
	private Node value;

	public AssignmentNode(Node var, Node value) {
		this.var = var;
		this.value = value;
	}

	public Node getVar() {
		return this.var;
	}

	public Node getValue() {
		return this.value;
	}

	public String toString() {
		String returner = this.var.toString() + ": " + this.value.toString();
		return returner;
	}

}
