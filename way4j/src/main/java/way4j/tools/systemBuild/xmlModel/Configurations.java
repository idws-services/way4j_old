package way4j.tools.systemBuild.xmlModel;

import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class Configurations {
	
	private Properties hibernateProperties;
	private DriverManagerDataSource dataSource;
	private ResourceLocations resourceLocations;
	
	public Properties getHibernateProperties() {
		return hibernateProperties;
	}
	public void setHibernateProperties(Properties hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}
	public DriverManagerDataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}
	public ResourceLocations getResourceLocations() {
		return resourceLocations;
	}
	public void setResourceLocations(ResourceLocations resourceLocations) {
		this.resourceLocations = resourceLocations;
	}
	
}
