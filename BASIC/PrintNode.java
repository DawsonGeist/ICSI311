import java.util.ArrayList;

public class PrintNode extends StatementNode {

	private ArrayList<Node> list;

	public PrintNode(ArrayList<Node> list) {
		this.list = list;
	}

	public ArrayList<Node> getList() {
		return this.list;
	}

	public String toString() {
		String returner = "Print Node(";
		for (Node currentNode : this.list) {
			returner += currentNode.toString() + " ";
		}
		return returner + ")";
	}

}
