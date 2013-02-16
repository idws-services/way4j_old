package way4j.tools.generics.service;

import java.io.Serializable;
import java.util.List;

import way4j.tools.generics.dao.GenericDao;
import way4j.tools.generics.dao.SearchCriteria;

public interface IGenericService<T extends Serializable> {
	
	T insert(T obj);
	
	void delete(Long id);
	void delete(T obj);
	void delete(String filter);
	void delete(SearchCriteria filter);
	
	T update(T obj);
	
	T get(Long id);
	T get(String filter);
	T get(SearchCriteria filter);
	
	List<T> list(String filter);
	List<T> list(SearchCriteria filter);
	
	Long getRowCount(String filter);	
	GenericDao<T> getDao();
	
}
