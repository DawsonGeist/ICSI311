import java.util.ArrayList;

public class InputNode extends StatementNode {

	private ArrayList<Node> list;

	public InputNode(ArrayList<Node> list) {
		this.list = list;
	}

	public ArrayList<Node> getList() {
		return this.list;
	}

	public void setList(ArrayList<Node> list) {
		this.list = list;
	}

	public void addElement(Node e) {
		this.list.add(e);
	}

	public String toString() {
		String returner = "InputNode(";

		for (Node e : list) {
			if (e.equals(list.get(list.size() - 1))) {
				returner += e.toString() + "";
			} else {
				returner += e.toString() + ", ";
			}
		}
		return returner + ")";
	}

}
