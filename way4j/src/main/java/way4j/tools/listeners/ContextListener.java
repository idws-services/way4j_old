package way4j.tools.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import way4j.tools.systemBuild.SystemBuilder;
import way4j.tools.systemBuild.xmlParser.SystemParser;
import way4j.tools.utils.GenericUtils;

public class ContextListener implements ServletContextListener{

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		new SystemBuilder().configureApplication(servletContextEvent.getServletContext());
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {}
	
}


