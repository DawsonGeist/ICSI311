
public class GosubNode extends StatementNode {

	private String id;

	public GosubNode(String id) {
		this.id = id;
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String toString() {
		return "Gosub: " + id.toString();
	}
}
