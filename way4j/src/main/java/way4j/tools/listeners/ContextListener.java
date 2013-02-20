package way4j.tools.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import way4j.tools.defaults.dao.LanguageDao;
import way4j.tools.defaults.dao.UserDao;
import way4j.tools.defaults.model.Language;
import way4j.tools.defaults.model.User;
import way4j.tools.generics.dao.FilterParser;
import way4j.tools.systemBuild.SystemBuilder;
import way4j.tools.utils.GenericUtils;

public class ContextListener implements ServletContextListener{

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		new SystemBuilder().configureApplication(servletContextEvent.getServletContext());
		
		LanguageDao langDao = GenericUtils.springContext.getBean(LanguageDao.class);
		UserDao userDao = GenericUtils.springContext.getBean(UserDao.class);
		
		Language lang = new Language();
		lang.setName("PT");
		lang.setAcronym("PT-BR");
		langDao.insert(lang);
		
		User user = new User();
		user.setLogin("admin");
		user.setSenha("projetos");
		user.setLanguage(lang);
		
		userDao.insert(user);
		
		FilterParser fp = new FilterParser();

		userDao.list(fp.parseFilter("{and:[{c:{f:'name',o:'=',v:'PT', bean:'language'}}, {c:{f:'login',o:'=',v:'admin'}}]}"));
		
		
		
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {}
	
}


