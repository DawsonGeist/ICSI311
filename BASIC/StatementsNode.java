import java.util.ArrayList;

public class StatementsNode extends Node {

	private ArrayList<Node> statements;

	public StatementsNode(ArrayList<Node> statements) {
		this.statements = statements;
	}

	public StatementsNode() {
		this.statements = new ArrayList<Node>();
	}

	public void addStatement(Node statement) {
		this.statements.add(statement);
	}

	public ArrayList<Node> getStatements() {
		return this.statements;
	}

	public String toString() {
		String returner = "";
		for (Node state : statements) {
			returner += state.toString();
		}

		return returner;
	}

}
