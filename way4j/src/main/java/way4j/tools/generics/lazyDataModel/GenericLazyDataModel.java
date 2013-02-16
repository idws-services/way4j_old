package way4j.tools.generics.lazyDataModel;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeansException;
import org.springframework.util.ReflectionUtils;

import way4j.tools.generics.dao.SearchCriteria;
import way4j.tools.generics.service.GenericService;
import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;

public class GenericLazyDataModel<T extends Serializable> extends LazyDataModel<T> {
	
	private String fastSearchGroupType = Constants.FilterConstants.GroupCondition.OR.value();
	private Class<T> typeClass;
	private GenericService<T> service;
	
	public GenericLazyDataModel(){
		this.typeClass = GenericUtils.getGenericTypeClass(this.getClass());
	}
	
	@Override
	public List<T> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		
		SearchCriteria filter = new SearchCriteria();
		filter.setStart(first);
		
		filter.setLimit(first+pageSize);
		
		if((sortField != null && !sortField.isEmpty())&& (sortOrder != null && !sortOrder.equals(SortOrder.UNSORTED))){
			if(sortOrder.equals(SortOrder.ASCENDING)){
				filter.setOrder(Order.asc(sortField));
			}else if(sortOrder.equals(SortOrder.DESCENDING)){
				filter.setOrder(Order.desc(sortField));
			}
		}
		Junction junction = Restrictions.disjunction();
		if(fastSearchGroupType.equals(Constants.FilterConstants.GroupCondition.AND.value())){
			junction = Restrictions.conjunction();
		}
		for(Entry<String, String> f : filters.entrySet()){
			junction.add(Restrictions.ilike(f.getKey(), "%"+f.getValue()+"%"));
		}
		filter.setCriterion(junction);
		List<T> list = getService().list(filter);
		this.setRowCount(getService().getRowCount(null).intValue());
		
		return list;
	}

	public List<T> load(String filter) {
		return getService().list(filter);
	}
	
	private GenericService<T> getService(){
		if(service == null){	
			try {
				service = GenericUtils.springContext.getBean(ClassUtils.getServiceOfModel(typeClass));
			} catch (BeansException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return service;
	}
	
	@Override
    public Object getRowKey(T object){
		Method getIdMethod = ReflectionUtils.findMethod(typeClass, "get"+GenericUtils.firstCharToUpperCase(ClassUtils.getIdField(typeClass).getName()));
		try {
			return getIdMethod.invoke(object);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
    
	@Override
    public T getRowData(String rowKey){
		List<T> wData = (List<T>) getWrappedData();
		
	    for(T item : wData) {  
            if(getRowKey(item).equals(rowKey))  {
                return item;  
            }
        }  
          
        return null;
	}

	public String getFastSearchGroupType() {
		return fastSearchGroupType;
	}

	public void setFastSearchGroupType(String fastSearchGroupType) {
		this.fastSearchGroupType = fastSearchGroupType;
	}
	
}