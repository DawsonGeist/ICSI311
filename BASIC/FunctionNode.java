import java.util.ArrayList;

public class FunctionNode extends StatementNode {

	private String name;
	private ArrayList<Node> params = new ArrayList<Node>();

	public FunctionNode(String name, ArrayList<Node> params) {
		this.setName(name);
		this.setParams(params);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Node> getParams() {
		return params;
	}

	public void setParams(ArrayList<Node> params) {
		this.params = params;
	}

	public String toString() {
		String returner = name + "(";
		for (Node x : params) {
			returner += x.toString() + ", ";
		}
		// This gets rid of the extra ',' at the end of returner and adds the closing
		// parenthesis
		return returner.substring(0, returner.length() - 2) + ")";
	}
}
