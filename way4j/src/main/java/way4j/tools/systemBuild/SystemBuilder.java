package way4j.tools.systemBuild;

import java.lang.reflect.Method;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.dom4j.jaxb.JAXBReader;
import org.hibernate.SessionFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import way4j.tools.systemBuild.xmlModel.Configurations;
import way4j.tools.systemBuild.xmlParser.SystemParser;
import way4j.tools.utils.GenericUtils;

public class SystemBuilder {
	
	public SystemBuilder instance;
	public DriverManagerDataSource dataSource;
	public Properties hibernateProps;
	public SessionFactory sessionFactory;
	private static Configurations configurations;
	
	public SystemBuilder(){
		init();
	}
	
	private void init(){
		
	}
	
	public void configureApplication(ServletContext servletContext){
		GenericApplicationContext applicationContext = new GenericApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContext));
		configurations = applicationContext.getBean(SystemParser.class).getSystem().getConfigurations();
		
		// Gerar Controllers, Service, Daos...
		
		applicationContext.getBeanFactory().registerSingleton("dataSource", configurations.getDataSource());
		applicationContext.getBeanFactory().registerSingleton("sessionFactory", getSessionFactory());
		GenericUtils.setApplicationContext(applicationContext);
	}
	
	public SessionFactory getSessionFactory(){
		if(sessionFactory == null){
			AnnotationSessionFactoryBean annotationSessionFactory = new AnnotationSessionFactoryBean();
			annotationSessionFactory.setDataSource(configurations.getDataSource());
			annotationSessionFactory.setPackagesToScan(new String[]{configurations.getResourceLocations().getModel(), "way4j.tools.defaults.model"});
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
	
}
