
public class VariableNode extends Node {

	private String name;

	public VariableNode(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return "Variable Node(" + this.name + ")";
	}
}
