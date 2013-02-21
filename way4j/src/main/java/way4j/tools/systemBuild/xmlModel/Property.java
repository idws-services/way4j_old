package way4j.tools.systemBuild.xmlModel;

public class Property {
	
	private String name;
	private String defaultValue;
	private String service;
	private String showWhenExpression;
	private String fetch;
	private boolean primaryKey;
	private boolean foreignKey;
	private int maxLength;
	private boolean required;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getShowWhenExpression() {
		return showWhenExpression;
	}
	public void setShowWhenExpression(String showWhenExpression) {
		this.showWhenExpression = showWhenExpression;
	}
	public String getFetch() {
		return fetch;
	}
	public void setFetch(String fetch) {
		this.fetch = fetch;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	public boolean isForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	
}
