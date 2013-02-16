package way4j.tools.generics.controller;

import way4j.tools.generics.lazyDataModel.GenericLazyDataModel;
import way4j.tools.generics.service.GenericService;
import way4j.tools.generics.service.IGenericService;
import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.primefaces.component.behavior.ajax.AjaxBehavior;
import org.springframework.beans.BeansException;

public class GenericController<T extends Serializable> implements IGenericController<T> {
	
	protected Class typeClass;
	protected GenericLazyDataModel<T> listGrid;
	protected T actionObj;
	protected String jsonActionObject;
	protected Long idToGet;
	protected T[] selectedItemsFromGrid;
	protected String filter;
	protected IGenericService<T> service;
	
	public GenericController(){
		
		this.typeClass = GenericUtils.getGenericTypeClass(this.getClass());
		
		try {
			listGrid = (GenericLazyDataModel<T>) ClassUtils.getLazyDataModelOfModel(typeClass).newInstance();
			actionObj = (T) typeClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public T insert() {
		T obj = getService().insert(actionObj);
		refreshObjects();
		return obj;
	}

	public void delete() {
		if(selectedItemsFromGrid != null){
			for(T selItem : selectedItemsFromGrid){
				getService().delete(selItem);	
			}
		}
	}
	
	public void deleteById(Long id){
		getService().delete(id);
	}
	
	public void deleteById(Long[] id){
		for(Long i : id){
			getService().delete(i);
		}
	}

	public void update() {
		getService().update(actionObj);
		refreshObjects();
	}

	public T get() {
		return getService().get(idToGet);
	}

	public ServletRequest getRequest() {
		return (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
	}

	public ServletResponse getResponse() {
		return (ServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}
	
	public HttpSession getSession() {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
	}
	
	protected IGenericService<T> getService(){
		if(service == null){
			try {
				return GenericUtils.springContext.getBean(ClassUtils.getServiceOfModel(typeClass));
			} catch (BeansException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return service;
	}
	
	public String listToJson(){
		List<T> list =  getService().list(filter);
		return GenericUtils.gson.toJson(list);
	}
	
	public void refreshObjects() {
		try {
			actionObj = (T) typeClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public GenericLazyDataModel getListGrid() {
		return this.listGrid;
	}

	public void setListGrid(GenericLazyDataModel lazyDataModel) {
		this.listGrid = lazyDataModel;
	}

	public Class getTypeClass() {
		return typeClass;
	}

	public void setTypeClass(Class typeClass) {
		this.typeClass = typeClass;
	}

	public T getActionObj() {
		return actionObj;
	}

	public void setActionObj(T actionObj) {
		this.actionObj = actionObj;
	}

	public Long getIdToGet() {
		return idToGet;
	}

	public void setIdToGet(Long idToGet) {
		this.idToGet = idToGet;
	}

	public T[] getSelectedItemsFromGrid() {
		return selectedItemsFromGrid;
	}

	public void setSelectedItemsFromGrid(T[] selectedItemsFromGrid) {
		this.selectedItemsFromGrid = selectedItemsFromGrid;
	}

	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getJsonActionObj() {
		return this.jsonActionObject;
	}

	public void setJsonActionObj(String jsonObj) {
		this.jsonActionObject = jsonObj;
	}
	
}
