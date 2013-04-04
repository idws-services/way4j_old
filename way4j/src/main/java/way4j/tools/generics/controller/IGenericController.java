package way4j.tools.generics.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import way4j.tools.generics.service.GenericService;


public interface IGenericController<T extends Serializable> {
	
	T insert(Map params);
	void delete(Map params);
	T update(Map params);
	T get(Map params);
	List<T> list(Map params);
	GenericService<T> getService();
	
}
