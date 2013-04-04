package way4j.tools.generics.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import way4j.tools.generics.service.GenericService;
import way4j.tools.utils.ModelUtils;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;

public class GenericController<T extends Serializable> implements IGenericController<T> {
	
	private Class typeClass;
	private GenericService<T> service;
	
	public GenericController(){
		this.typeClass = GenericUtils.getGenericTypeClass(getClass());
	}
	
	@Override
	public T insert(Map params) {
		T actionObject = (T) GenericUtils.json.fromJson(String.valueOf(params.get(Constants.Parameters.ACTION_OBJ.value())), typeClass);
		actionObject = getService().insert(actionObject);
		return actionObject;
	}

	@Override
	public void delete(Map params) {
		if(params.get(Constants.Parameters.FILTER.value()) != null){
			String filter = String.valueOf(params.get(Constants.Parameters.FILTER.value()));
			getService().delete(filter);
		}else if(params.get(Constants.Parameters.ACTION_OBJ) != null){
			long[] ids = GenericUtils.json.fromJson(String.valueOf(params.get(Constants.Parameters.ACTION_OBJ)), new long[]{}.getClass());
			StringBuilder filter = new StringBuilder("{or:[");
			String idField = ModelUtils.getIdField(typeClass).getName();
			for(long id : ids){
				filter.append("{c:{f:'"+idField+"',o:'=',v:'"+id+"'}},");
			}
			filter.deleteCharAt(filter.toString().length());
			filter.append("]}");
			getService().delete(filter.toString());
		}
	}

	@Override
	public T update(Map params) {
		T actionObject = (T) GenericUtils.json.fromJson(String.valueOf(params.get(Constants.Parameters.ACTION_OBJ)), typeClass);
		actionObject = getService().update(actionObject);
		return actionObject;
	}

	@Override
	public T get(Map params) {
		T result = null;
		if(params.get(Constants.Parameters.ID.value()) != null){
			long id = GenericUtils.json.fromJson(String.valueOf(params.get(Constants.Parameters.ID.value())), long.class);
			result = getService().get(id);
		}else if(params.get(Constants.Parameters.FILTER.value()) != null){
			String filter = String.valueOf(params.get(Constants.Parameters.FILTER.value()));
			result = getService().get(filter);
		}
		return result;
	}

	@Override
	public List<T> list(Map params) {
		return getService().list(String.valueOf(params.get(Constants.Parameters.FILTER.value())));
	}

	@Override
	public GenericService<T> getService() {
		if(service == null){
			service = GenericUtils.getService(typeClass);
		}
		return service;
	}
	
}
