package way4j.tools.systemBuild.xmlModel;

import java.util.List;

public class Bean {
	
	private List<Property> properties;
	private String table;
	private String name;
	private char type;
	private String windowSizesCrud;
	private String extendsBean;

	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public String getWindowSizesCrud() {
		return windowSizesCrud;
	}
	public void setWindowSizesCrud(String windowSizesCrud) {
		this.windowSizesCrud = windowSizesCrud;
	}
	public String getExtendsBean() {
		return extendsBean;
	}
	public void setExtendsBean(String extendsBean) {
		this.extendsBean = extendsBean;
	}
	
}
