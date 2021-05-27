
public class StatementNode extends Node {

	private StatementNode nextNode = null;

	public StatementNode() {
		// Do not know if this needs to be instantiated or not
	}

	public String toString() {
		return null;
	}

	public StatementNode getNextNode() {
		return nextNode;
	}

	public void setNextNode(StatementNode next) {
		this.nextNode = next;
	}

}
