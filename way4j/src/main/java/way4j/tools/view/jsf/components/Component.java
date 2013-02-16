package way4j.tools.view.jsf.components;

public abstract class Component {
	
	private String value;
	
	public abstract Object createComponent();

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
