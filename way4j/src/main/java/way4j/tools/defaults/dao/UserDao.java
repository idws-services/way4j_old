package way4j.tools.defaults.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import way4j.tools.defaults.model.User;
import way4j.tools.generics.dao.GenericDao;

@Repository
public class UserDao extends GenericDao<User>{
	
}
