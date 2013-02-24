package way4j.tools.listeners;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import way4j.tools.defaults.dao.CursoDao;
import way4j.tools.defaults.dao.LanguageDao;
import way4j.tools.defaults.dao.UserDao;
import way4j.tools.defaults.model.Curso;
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
		CursoDao cursoDao = GenericUtils.springContext.getBean(CursoDao.class);
		List<Curso> cursos1 = new ArrayList<Curso>();
		List<Curso> cursos2 = new ArrayList<Curso>();
		Curso curso1 = new Curso();
		Curso curso2 = new Curso();
		Curso curso3 = new Curso();
		Curso curso4 = new Curso();
		Curso curso5 = new Curso();
		Curso curso6 = new Curso();
		Curso curso7 = new Curso();
		
		curso1.setNome("Programação");
		curso2.setNome("Engenharia");
		curso3.setNome("Geografia");
		curso4.setNome("Inglês");
		// 2
		curso5.setNome("Português");
		curso6.setNome("Marketing");
		curso7.setNome("Mídia Social");
		
		cursos1.add(curso1);
		cursos1.add(curso2);
		cursos1.add(curso3);
		cursos1.add(curso4);
		cursos2.add(curso5);
		cursos2.add(curso6);
		cursos2.add(curso7);
		
		cursoDao.insert(curso1);
		cursoDao.insert(curso2);
		cursoDao.insert(curso3);
		cursoDao.insert(curso4);
		cursoDao.insert(curso5);
		cursoDao.insert(curso6);
		cursoDao.insert(curso7);
		
		Language lang = new Language();
		lang.setName("PT");
		lang.setAcronym("PT-BR");
		langDao.insert(lang);
		
		User user1 = new User();
		user1.setLogin("admin");
		user1.setSenha("projetos");
		user1.setLanguage(lang);
		user1.setCursos(cursos1);
		userDao.insert(user1);
		
		User user2 = new User();
		user2.setCursos(cursos2);
		user2.setLanguage(lang);
		user2.setLogin("djefferson");
		user2.setSenha("projetos");
		userDao.insert(user2);
		
		FilterParser fp = new FilterParser();
		// examplo trazendo os usuários que estão não estão cursando Geografia ( sRelacionamento MM )
		userDao.list("[{" +
							"or:[" +
									"{c:{f:'cursos.nome', o:'<>', v:'Engenharia'}},"+
									"{c:{f:'cursos.nome', o:'=', v:'Marketing'}},"+
									"{c:{f:'login', o:'=', v:'Maria'}}"+
							"]" +
						"}]");
		
		
		
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {}
	
}


