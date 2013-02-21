package way4j.tools.generics.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Subqueries;
import org.hibernate.impl.CriteriaImpl.Subcriteria;

import way4j.application.model.Pessoa;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;

public class GenericDao<T extends Serializable> implements IGenericDao<T>{

	private FilterParser filterParser;
	private Class<T> typeClass;
	private SessionFactory sessionFactory;
	
	public GenericDao(){
		 filterParser = new FilterParser();
	}
	
	private Class<T> getTypeClass(){
		if(typeClass == null){
			typeClass = GenericUtils.getGenericTypeClass(getClass());
		}
		return typeClass;
	}

	public SessionFactory getSessionFactory(){
		if(sessionFactory == null){
			sessionFactory = (SessionFactory) GenericUtils.springContext.getBean(SessionFactory.class);
		}
		return this.sessionFactory;
	}
	
	public Session getSession(){
		return getSessionFactory().getCurrentSession();
	}
	
	public T insert(T obj){
		Session session = startTransaction();
		obj = (T) session.save(obj);
		finishTransaction(session);
		return obj;
	}
	
	public void delete(Long id){
		Session session = startTransaction();
		session.delete(get(id));
		finishTransaction(session);
	}
	
	public void delete(T obj) {
		Session session = startTransaction();
		session.delete(obj);
		finishTransaction(session);
	}
	
	public void delete(String filter) {
		List<T> lines = list(filter);
		Session session = startTransaction(); 
		for(T l : lines){
			session.delete(l);	
		}
		finishTransaction(session);
	}
	
	public void delete(SearchCriteria filter) {
		List<T> lines = list(filter);
		Session session = startTransaction(); 
		for(T l : lines){
			session.delete(l);	
		}
		finishTransaction(session);
	}

	public T update(T obj) {
		Session session = startTransaction();
		session.update(obj);
		finishTransaction(session);
		return obj;
	}

	public T get(Long id) {
		return (T) getSession().get(getTypeClass(), id);
	}
	
	public T get(String filter) {
		return (T) configureCriteriaByFilter(filter).uniqueResult();
	}
	
	public T get(SearchCriteria filter) {
		return (T) loadFilterToCriteria(filter).uniqueResult();
	}
	
	public List<T> list(String filter) {
		return configureCriteriaByFilter(filter).list();
	}
	
	public List<T> list(SearchCriteria filterCriteria) {
		return loadFilterToCriteria(filterCriteria).list();
	}
	 
	protected Criteria configureCriteriaByFilter(String filter){
		if(filter == null || filter.isEmpty()){
			return createCriteria();
		}else{
			return loadFilterToCriteria(filterParser.parseFilter(filter));
		}
	}
	
	protected Criteria loadFilterToCriteria(SearchCriteria filterResult){
		Criteria criteria = createCriteria();
		if(filterResult.getCriterion() != null){
			criteria.add(filterResult.getCriterion());
		}
		if(filterResult.getJoins() != null && !filterResult.getJoins().isEmpty()){
			for(Entry<String, String> join : filterResult.getJoins().entrySet()){
				criteria.createAlias(join.getKey(), join.getValue(), getJoinType(join.getValue()));
			}
		}
		/*if(filterResult.getSubQueries() != null && !filterResult.getSubQueries().isEmpty()){
			for(Entry<String, SearchCriteria> subQuery : filterResult.getSubQueries().entrySet()){
				Criteria subCriteria = criteria.createCriteria(subQuery.getKey());
				//subCriteria.add(subQuery.getValue().getCriterion());
				criteria.createAlias(subQuery.getKey(), subQuery.getKey());
				subCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			}
		}*/
		if(filterResult.getOrder() != null){
			criteria.addOrder(filterResult.getOrder());
		}
		if(filterResult.getStart() != null){
			criteria.setFirstResult(filterResult.getStart());
		}
		if(filterResult.getLimit() != null){
			criteria.setMaxResults(filterResult.getLimit());
		}
		return criteria;
	}
	
	protected int getJoinType(String join){
		if(join != null){
			if(join.equals(Constants.FilterConstants.JoinTypes.FULL.value())){
				return Criteria.FULL_JOIN;
			}else if(join.equals(Constants.FilterConstants.JoinTypes.INNER.value())){
				return Criteria.INNER_JOIN;
			}
		}
		return Criteria.LEFT_JOIN;
	}
	
	protected Criteria createCriteria(){
		return getSessionFactory().openSession().createCriteria(getTypeClass());
	}
	
	protected Session startTransaction(){
		Session session = getSession();
		session.beginTransaction();
		return session;
	}
	
	protected void finishTransaction(Session session){
		session.getTransaction().commit();
	}
	
	public Long getRowCount(String filter){
		Criteria criteria = configureCriteriaByFilter(filter);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
}
