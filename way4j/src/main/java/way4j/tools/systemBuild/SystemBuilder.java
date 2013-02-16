package way4j.tools.systemBuild;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import way4j.tools.systemBuild.clazz.MvcClassBuider;
import way4j.tools.systemBuild.xmlParser.Configurations;
import way4j.tools.systemBuild.xmlParser.SystemParser;
import way4j.tools.systemDefinitions.IApplicationConfig;
import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;

public class SystemBuilder {
	
	public SystemBuilder instance;
	public DriverManagerDataSource dataSource;
	public Properties hibernateProps;
	public SessionFactory sessionFactory;
	private static Configurations configurations;
	
	static {
		init();
	}
	
	public SystemBuilder(){
		init();
	}
	
	private static void init(){
		
	}
	
	public void configureApplication(ServletContext servletContext){
		GenericApplicationContext applicationContext = new GenericApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContext));
		configurations = applicationContext.getBean(SystemParser.class).getConfigurations();
		
		// Gerar Controllers, Service, Daos...
		
		applicationContext.getBeanFactory().registerSingleton("dataSource", getDataSource());
		applicationContext.getBeanFactory().registerSingleton("sessionFactory", getSessionFactory());
		GenericUtils.setApplicationContext(applicationContext);
	}
	
	public SessionFactory getSessionFactory(){
		if(sessionFactory == null){
			AnnotationSessionFactoryBean annotationSessionFactory = new AnnotationSessionFactoryBean();
			annotationSessionFactory.setDataSource(getDataSource());
			configurations.getPackageLocations().getModel();
			annotationSessionFactory.setPackagesToScan(new String[]{"way4j.application.model", "way4j.tools.model"});
			annotationSessionFactory.setHibernateProperties(configurations.getHibernateProperties());
			try {
				// O método é protected, por isso a necessidade de utilizar reflection
				Method buidSessionFactory = ReflectionUtils.findMethod(AnnotationSessionFactoryBean.class, "buildSessionFactory");
				buidSessionFactory.setAccessible(true);
				
				sessionFactory = (SessionFactory) buidSessionFactory.invoke(annotationSessionFactory);
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return sessionFactory;
	}
	
	public static DriverManagerDataSource getDataSource(){
		return configurations.getDataSource();
	}
	
}
