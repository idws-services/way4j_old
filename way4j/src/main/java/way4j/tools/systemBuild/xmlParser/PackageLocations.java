package way4j.tools.systemBuild.xmlParser;

public class PackageLocations {

	private String dao;
	private String service;
	private String controller;
	private String model;
	private String lazyDataModel;
	private String view;
	
	public PackageLocations(){}
	
	public PackageLocations(String dao, String service, String controller,String model, String lazyDataModel, String view) {
		this.dao = dao;
		this.service = service;
		this.controller = controller;
		this.model = model;
		this.lazyDataModel = lazyDataModel;
		this.view = view;
	}
	
	public String getDao() {
		return dao;
	}
	public void setDao(String dao) {
		this.dao = dao;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getController() {
		return controller;
	}
	public void setController(String controller) {
		this.controller = controller;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}

	public String getLazyDataModel() {
		return lazyDataModel;
	}

	public void setLazyDataModel(String lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
}