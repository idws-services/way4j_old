package way4j.tools.defaults.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.stereotype.Repository;

import way4j.tools.defaults.model.Curso;
import way4j.tools.defaults.model.User;
import way4j.tools.generics.dao.GenericDao;

@Repository
public class UserDao extends GenericDao<User>{
	
	public List<User> getUsuariosNoCurso(Curso curso){
		Criteria c = createCriteria();
		c.createCriteria("cursos")
			.add(Restrictions.not(Restrictions.in("nome", new Object[]{"Programação"})))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return c.list();
	}
	
}
