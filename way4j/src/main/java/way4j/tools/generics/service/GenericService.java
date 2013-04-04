package way4j.tools.generics.service;

import java.io.Serializable;
import java.util.List;

import way4j.tools.generics.dao.GenericDao;
import way4j.tools.utils.GenericUtils;

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

	public List<T> list(String filter) {
		return getDao().list(filter);
	}
	
	public GenericDao<T> getDao(){
		if(dao == null){
			dao = GenericUtils.getDao(typeClass);
		}
		return dao;
	}

	public Long getRowCount(String filter) {
		return getDao().getRowCount(filter);
	}
		
}
