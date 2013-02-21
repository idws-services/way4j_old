package way4j.tools.systemBuild.xmlModel;

import java.util.List;

public class Module {
	
	private String name;
	private List<Bean> beans;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Bean> getBeans() {
		return beans;
	}
	public void setBeans(List<Bean> beans) {
		this.beans = beans;
	}
	
}
