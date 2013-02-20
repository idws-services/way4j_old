package way4j.tools.systemBuild.clazz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import way4j.tools.systemBuild.xmlParser.PackageLocations;
import way4j.tools.systemBuild.xmlParser.SystemParser;
import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;

import com.sun.org.apache.xpath.internal.NodeSet;

public class MvcClassBuider {
	
	private static MvcClassBuider instance;
	private static PackageLocations packageLocations;
	private static Map<String,Class> singletonClasses;
	
	private MvcClassBuider(){}
	
	public static MvcClassBuider getInstance(){
		if(instance == null){
			instance = new MvcClassBuider();
		}
		init();
		return instance;
	}
	
	private static void init(){
		singletonClasses = new HashMap<String, Class>();
		packageLocations = GenericUtils.springContext.getBean(SystemParser.class).getConfigurations().getPackageLocations();
	}
	
	public void buildMvc(){
		// constroi o model
		// dao
		// controller ..
	}
	
	private Class buildModel(List<NodeSet> fields){
		return null;
	}
	
	private void generateControllerToModel(Class model) throws ClassNotFoundException{
		String canonicalName = packageLocations.getController()+"."+ClassUtils.getClassName(model)+Constants.CONTROLLER_SUFIX;
		if(!ClassUtils.classExists(packageLocations.getController()+"."+ClassUtils.getClassName(model)+Constants.CONTROLLER_SUFIX)){
			Compiler.compile(canonicalName, generateController(model));
		}
		singletonClasses.put(canonicalName,Class.forName(canonicalName));
	}
	
	private void generateServiceToModel(Class model) throws ClassNotFoundException{
		String canonicalName = packageLocations.getService()+"."+ClassUtils.getClassName(model)+Constants.SERVICE_SUFIX;
		if(!ClassUtils.classExists(canonicalName)){
			Compiler.compile(canonicalName, generateService(model));
		}
		singletonClasses.put(canonicalName, Class.forName(canonicalName));
	}
	
	private void generateDaoToModel(Class model) throws ClassNotFoundException{
		String canonicalName = packageLocations.getDao()+"."+ClassUtils.getClassName(model)+Constants.DAO_SUFIX;
		if(!ClassUtils.classExists(canonicalName)){
			Compiler.compile(canonicalName, generateDao(model));
		}
		singletonClasses.put(canonicalName, Class.forName(canonicalName));
	}
	
	private void generateLazyDataModelToModel(Class model) throws ClassNotFoundException{
		String canonicalName = packageLocations.getLazyDataModel()+"."+ClassUtils.getClassName(model)+Constants.LAZY_DATA_MODEL_SUFIX;
		if(!ClassUtils.classExists(canonicalName)){
			Compiler.compile(canonicalName, generateLazyDataModel(model));
		}
		singletonClasses.put(canonicalName, Class.forName(canonicalName));
	}
	
	private String generateController(Class model){
		
		StringBuilder classStr = new StringBuilder();
		String className = ClassUtils.getClassName(model);
		classStr.append("package "+packageLocations.getController()+";");
		classStr.append("import "+model.getCanonicalName()+";");
		classStr.append("import way4j.tools.generics.controller.GenericController;");
		classStr.append("import javax.faces.bean.ManagedBean;");
		classStr.append("import javax.faces.bean.SessionScoped;");
		classStr.append("import org.springframework.stereotype.Controller;");
		classStr.append("@ManagedBean(name=\""+GenericUtils.firstCharToLowerCase(className)+Constants.CONTROLLER_SUFIX+"\")");
		classStr.append("@SessionScoped");
		classStr.append("@Controller");
		classStr.append("public class "+className+Constants.CONTROLLER_SUFIX+" extends GenericController<"+ClassUtils.getClassName(model)+">{");
		classStr.append("}");
		return classStr.toString();
	}
	
	private String generateService(Class model){
		StringBuilder classStr = new StringBuilder();
		String className = ClassUtils.getClassName(model);
		classStr.append("package "+packageLocations.getService()+";");
		classStr.append("import "+model.getCanonicalName()+";");
		classStr.append("import way4j.tools.generics.controller.GenericService;");
		classStr.append("import org.springframework.stereotype.Service;");
		classStr.append("@Service");
		classStr.append("public class "+className+Constants.SERVICE_SUFIX+" extends GenericService<"+ClassUtils.getClassName(model)+">{");
		classStr.append("}");
		return classStr.toString();
	}
	
	private String generateLazyDataModel(Class model){
		StringBuilder classStr = new StringBuilder();
		String className = ClassUtils.getClassName(model);
		classStr.append("package "+packageLocations.getLazyDataModel()+";");
		classStr.append("import "+model.getCanonicalName()+";");
		classStr.append("import way4j.tools.generics.lazyDataModel.GenericLazyDataModel;");
		classStr.append("public class "+className+Constants.LAZY_DATA_MODEL_SUFIX+" extends GenericLazyDataModel<"+ClassUtils.getClassName(model)+">{");
		classStr.append("}");
		return classStr.toString();
	}
	
	private String generateDao(Class model){
		StringBuilder classStr = new StringBuilder();
		String className = ClassUtils.getClassName(model);
		classStr.append("package "+packageLocations.getDao()+";");
		classStr.append("import "+model.getCanonicalName()+";");
		classStr.append("import way4j.tools.generics.controller.GenericDao;");
		classStr.append("import org.springframework.stereotype.Repository;");
		classStr.append("@Repositoty");
		classStr.append("public class "+className+Constants.DAO_SUFIX+" extends GenericDao<"+ClassUtils.getClassName(model)+">{");
		classStr.append("}");
		return classStr.toString();
	}

	public PackageLocations getPackageLocations() {
		return packageLocations;
	}

	public void setPackageLocations(PackageLocations packageLocations) {
		this.packageLocations = packageLocations;
	}

	public static Map<String, Class> getSingletonClasses() {
		return singletonClasses;
	}
	
}
