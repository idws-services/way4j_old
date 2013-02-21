package way4j.tools.generics.dao;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public class SearchCriteria {
	
	private Criterion criterion;
	private Order order;
	private Map<String, String> joins;
	//private Map<String, SearchCriteria> subQueries;
	private Integer start;
	private Integer limit;
	
	
	public SearchCriteria(){
		joins = new HashMap<String, String>();
		//subQueries = new HashMap<String, SearchCriteria>();
	}
	
	public Criterion getCriterion() {
		return criterion;
	}
	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Map<String, String> getJoins() {
		return joins;
	}
	public void setJoins(Map<String, String> joins) {
		this.joins = joins;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/*public Map<String, SearchCriteria> getSubQueries() {
		return subQueries;
	}

	public void setSubQueries(Map<String, SearchCriteria> subQueries) {
		this.subQueries = subQueries;
	}*/
}
