package way4j.tools.generics.dao;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

public class SearchCriteria {
	
	private Criterion criterion;
	private Order order = null;
	private Map<String, SearchCriteria> joins = new HashMap<String, SearchCriteria>();
	
	private Integer start;
	private Integer limit;

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
	public Map<String, SearchCriteria> getJoins() {
		return joins;
	}
	public void setJoins(Map<String, SearchCriteria> joins) {
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
}
