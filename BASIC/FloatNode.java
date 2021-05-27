
public class FloatNode extends Node {

	private float value;

	public FloatNode(float value) {
		this.value = value;
	}

	public Float getValue() {
		return this.value;
	}

	public String toString() {
		return "Float-Node(" + this.getValue().toString() + ")";
	}
}
