package way4j.tools.generics.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import way4j.tools.utils.GenericUtils;

public interface IGenericDao<T> {
	
	public T insert(T obj);
	
	public void delete(Long id);
	public void delete(T obj);
	public void delete(String filter);
	
	public T update(T obj);
	
	public T get(Long id);
	public T get(String filter);
	
	public List<T> list(String filter);
	public Long getRowCount(String filter);
	
	public SessionFactory getSessionFactory();
	public Session getSession();
	
}
