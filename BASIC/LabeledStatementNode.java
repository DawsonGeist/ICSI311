
public class LabeledStatementNode extends StatementNode {
	private String label;
	private StatementNode state;

	public LabeledStatementNode(String label) {
		this.label = label;
		this.state = null;
	}

	public void setState(StatementNode s) {
		this.state = s;
	}

	public void setLabel(String l) {
		this.label = l;
	}

	public String getLabel() {
		return this.label;
	}

	public StatementNode getState() {
		return this.state;
	}

	public String toString() {
		return this.label + ": " + state.toString();
	}
}
