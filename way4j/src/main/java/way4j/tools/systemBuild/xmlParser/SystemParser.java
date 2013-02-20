package way4j.tools.systemBuild.xmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import way4j.tools.utils.constants.Constants;
import way4j.tools.utils.constants.XmlSysConfigConstants;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;

@Component
public class SystemParser {
	
	private Configurations configurations;
	private XPath  xpathSysConfig;
	private DeferredDocumentImpl docSysConfig;
	
	public SystemParser(){
		init();
	}
	
	private void init(){
		this.configurations = new Configurations();
		this.xpathSysConfig  = XPathFactory.newInstance().newXPath();
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(false);
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			docSysConfig = (DeferredDocumentImpl) docBuilder.parse(this.getClass().getClassLoader().getResourceAsStream("systemConfig.xml"));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		loadConfigurations();
	}
	
	public void loadConfigurations(){
		configureDataSource();
		configureHibernate();
		configurePackageLocations();
	}
	
	public DriverManagerDataSource configureDataSource(){
		Node dataSourceNode = (Node) queryInXmlSysConfig("//dataSource", XPathConstants.NODE);
		String driver = dataSourceNode.getAttributes().getNamedItem(XmlSysConfigConstants.DataSource.DRIVER.getValue()).getNodeValue();
		String user = dataSourceNode.getAttributes().getNamedItem(XmlSysConfigConstants.DataSource.USER.getValue()).getNodeValue();
		String password = dataSourceNode.getAttributes().getNamedItem(XmlSysConfigConstants.DataSource.PASSWORD.getValue()).getNodeValue();
		String url = dataSourceNode.getAttributes().getNamedItem(XmlSysConfigConstants.DataSource.URL.getValue()).getNodeValue();
		DriverManagerDataSource dataSource = new DriverManagerDataSource(driver, url, user,password);
		configurations.setDataSource(dataSource);
		return dataSource;
	}
	
	public Properties configureHibernate(){
		NodeList hibernatePropsNodes = (NodeList) queryInXmlSysConfig("//dataSource/hibernateProps/prop", XPathConstants.NODESET);
		Properties hibernateProps = getDefaultHibernateProperties();
		for (int i = 0; i < hibernatePropsNodes.getLength(); i++) {
			Node prop = hibernatePropsNodes.item(i);
			hibernateProps.put(prop.getAttributes().getNamedItem(Constants.NAME).getNodeValue(), prop.getAttributes().getNamedItem(Constants.VALUE).getNodeValue());
		}
		configurations.setHibernateProperties(hibernateProps);
		return hibernateProps;
	}
	
	public PackageLocations configurePackageLocations(){
		PackageLocations packageLocations = new PackageLocations();
		Node element = (Node) queryInXmlSysConfig("//resourceLocations/dao", XPathConstants.NODE);
		packageLocations.setDao(element.getAttributes().getNamedItem(XmlSysConfigConstants.PackageLocations.LOCATION.getValue()).getNodeValue());
		element = (Node) queryInXmlSysConfig("//resourceLocations/service", XPathConstants.NODE);
		packageLocations.setService(element.getAttributes().getNamedItem(XmlSysConfigConstants.PackageLocations.LOCATION.getValue()).getNodeValue());
		element = (Node) queryInXmlSysConfig("//resourceLocations/controller", XPathConstants.NODE);
		packageLocations.setController(element.getAttributes().getNamedItem(XmlSysConfigConstants.PackageLocations.LOCATION.getValue()).getNodeValue());
		element = (Node) queryInXmlSysConfig("//resourceLocations/view", XPathConstants.NODE);
		packageLocations.setView(element.getAttributes().getNamedItem(XmlSysConfigConstants.PackageLocations.LOCATION.getValue()).getNodeValue());
		element = (Node) queryInXmlSysConfig("//resourceLocations/lazyDataModel", XPathConstants.NODE);
		packageLocations.setLazyDataModel(element.getAttributes().getNamedItem(XmlSysConfigConstants.PackageLocations.LOCATION.getValue()).getNodeValue());
		element = (Node) queryInXmlSysConfig("//resourceLocations/model", XPathConstants.NODE);
		packageLocations.setModel(element.getAttributes().getNamedItem(XmlSysConfigConstants.PackageLocations.LOCATION.getValue()).getNodeValue());
		configurations.setPackageLocations(packageLocations);
		return packageLocations;
	}
	
	public Object queryInXmlSysConfig(String query, QName returnType){
		try {
			return (NodeList) this.xpathSysConfig.evaluate(query, docSysConfig, returnType);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Properties getDefaultHibernateProperties(){
		Properties props = new Properties();
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.format_sql", "true");
		props.put("hibernate.current_session_context_class", "thread");
		props.put("hibernate.hbm2ddl.auto", "create");
		props.put("hibernate.transaction.auto_close_session", "true");
		props.put("hibernate.transaction.flush_before_completion", "true");
		props.put("hibernate.connection.release_mode","after_transaction");
		props.put("hibernate.c3p0.min_size","5");
		props.put("hibernate.c3p0.max_size","100");
		props.put("hibernate.c3p0.max_statements","50");
		props.put("hibernate.c3p0.idle_test_period","5");
		props.put("hibernate.c3p0.timeout","10");
		return props;
	}

	public Configurations getConfigurations() {
		return configurations;
	}

	
}
