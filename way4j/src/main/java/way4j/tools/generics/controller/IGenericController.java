package way4j.tools.generics.controller;

import way4j.tools.generics.lazyDataModel.GenericLazyDataModel;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface IGenericController<T> {
	
	public T insert();
	public void delete();
	public void update();
	public T get();
	
	public GenericLazyDataModel getListGrid();
	public void setListGrid(GenericLazyDataModel lazyDataModel);
	
	public void refreshObjects();
	public ServletRequest getRequest();
	public ServletResponse getResponse();
	
	public T getActionObj();
	public void setActionObj(T actionObj);

	public String getJsonActionObj();
	public void setJsonActionObj(String jsonObj);

	public T[] getSelectedItemsFromGrid();
	public void setSelectedItemsFromGrid(T[] selectedItemsFromGrid);
	
	public String listToJson();
	
}
