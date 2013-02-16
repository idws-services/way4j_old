package way4j.tools.generics.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import way4j.tools.utils.GenericUtils;

@Repository
public class GenericDao<T extends Serializable> implements IGenericDao<T>{

	private FilterParser filterParser = new FilterParser();
	private Class<T> typeClass;
	private SessionFactory sessionFactory;
	
	public GenericDao(){
		
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
	
	private Criteria configureCriteriaByFilter(String filter){
		if(filter == null || filter.isEmpty()){
			return createCriteria();
		}else{
			return loadFilterToCriteria(filterParser.parseFilter(filter));
		}
	}
	
	private Criteria loadFilterToCriteria(SearchCriteria filterResult){
		Criteria criteria = createCriteria();

		if(filterResult.getCriterion() != null){
			criteria.add(filterResult.getCriterion());
		}
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
	
	private Criteria createCriteria(){
		return getSessionFactory().openSession().createCriteria(getTypeClass());
	}
	
	private Session startTransaction(){
		Session session = getSession();
		session.beginTransaction();
		return session;
	}
	
	private void finishTransaction(Session session){
		session.getTransaction().commit();
	}
	
	public Long getRowCount(String filter){
		Criteria criteria = configureCriteriaByFilter(filter);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
}
