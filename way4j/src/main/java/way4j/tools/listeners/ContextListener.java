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
		List<Curso> cursos = new ArrayList<Curso>();
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
		curso5.setNome("Português");
		curso6.setNome("Marketing");
		curso7.setNome("Mídia Social");
		
		cursos.add(curso1);
		cursos.add(curso2);
		cursos.add(curso3);
		cursos.add(curso4);
		cursos.add(curso5);
		cursos.add(curso6);
		//cursos.add(curso7);
		
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
		
		User user = new User();
		user.setLogin("admin");
		user.setSenha("projetos");
		user.setLanguage(lang);
		user.setCursos(cursos);
		userDao.insert(user);
		
		FilterParser fp = new FilterParser();
		userDao.getUsuariosNoCurso(curso7);
		// examplo trazendo os usuários que estão não estão cursando Geografia ( sRelacionamento MM )
		userDao.list(fp.parseFilter("[{" +
										"or:[" +
												"{c:{f:'cursos.nome', o:'in', v:'Mídia Social'}}"+
										"]" +
									"}]"));
		
		
		
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {}
	
}


