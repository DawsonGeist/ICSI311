public class ForNode extends StatementNode {

	private int increment;

	public ForNode(int increment) {
		this.increment = increment;
	}

	public ForNode() {
		this.increment = 1;
	}

	public int getIncrement() {
		return this.increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public String toString() {
		return "FOR: " + "STEP: " + this.increment;
	}
}
