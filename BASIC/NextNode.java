
public class NextNode extends StatementNode {
	private VariableNode next;

	public NextNode(VariableNode next) {
		this.setNext(next);
	}

	public VariableNode getNext() {
		return next;
	}

	public void setNext(VariableNode next) {
		this.next = next;
	}

	public String toString() {
		return this.next.toString();
	}
}
