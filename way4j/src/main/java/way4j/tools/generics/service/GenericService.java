package way4j.tools.generics.service;

import way4j.tools.generics.dao.GenericDao;
import way4j.tools.generics.dao.SearchCriteria;
import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.BeansException;

public class GenericService<T extends Serializable> implements IGenericService<T> {
	
	private Class<T> typeClass;
	private GenericDao<T> dao;
	
	private Class<T> getTypeClass(){
		if(typeClass == null){
			typeClass = GenericUtils.getGenericTypeClass(getClass());
		}
		return typeClass;
	}
	
	public GenericService(){
	}
	
	public T insert(T obj) {
		return getDao().insert(obj);
	}

	public void delete(Long id) {
		getDao().delete(id);
	}

	public void delete(T obj) {
		getDao().delete(obj);
	}

	public void delete(String filter) {
		getDao().delete(filter);
	}

	public void delete(SearchCriteria filter) {
		getDao().delete(filter);
	}

	public T update(T obj) {
		getDao().update(obj);
		return obj;
	}

	public T get(Long id) {
		return getDao().get(id);
	}

	public T get(String filter) {
		return getDao().get(filter);
	}

	public T get(SearchCriteria filter) {
		return getDao().get(filter);
	}

	public List<T> list(String filter) {
		return getDao().list(filter);
	}

	public List<T> list(SearchCriteria filter) {
		return getDao().list(filter);
	}
	
	public GenericDao<T> getDao(){
		if(dao == null){
			try {
				dao = GenericUtils.springContext.getBean(ClassUtils.getDaoOfModel(getTypeClass()));
			} catch (BeansException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return dao;
	}

	public Long getRowCount(String filter) {
		return getDao().getRowCount(filter);
	}
		
}
