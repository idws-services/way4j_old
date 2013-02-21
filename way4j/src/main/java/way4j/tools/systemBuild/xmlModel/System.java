package way4j.tools.systemBuild.xmlModel;

import java.util.List;

public class System {
	
	private Configurations configurations;
	private List<Module> module;
	
	public System(){
		configurations = new Configurations();
	}
	
	public Configurations getConfigurations() {
		return configurations;
	}
	public void setConfigurations(Configurations configurations) {
		this.configurations = configurations;
	}
	public List<Module> getModule() {
		return module;
	}
	public void setModule(List<Module> module) {
		this.module = module;
	}	
	
}
