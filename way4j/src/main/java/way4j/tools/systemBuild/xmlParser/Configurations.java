package way4j.tools.systemBuild.xmlParser;

import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class Configurations {
	
	private Properties hibernateProperties;
	private DriverManagerDataSource dataSource;
	private PackageLocations packageLocations;
	
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
	public PackageLocations getPackageLocations() {
		return packageLocations;
	}
	public void setPackageLocations(PackageLocations packageLocations) {
		this.packageLocations = packageLocations;
	}	
	
}
