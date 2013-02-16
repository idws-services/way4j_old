package way4j.tools.systemDefinitions;

import java.util.Properties;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import way4j.tools.systemBuild.xmlParser.PackageLocations;

public interface IApplicationConfig {
	
	public DriverManagerDataSource getDataSource();
	public PackageLocations getPackageLocations();
	public Properties getHibernateProperties();
	
}
