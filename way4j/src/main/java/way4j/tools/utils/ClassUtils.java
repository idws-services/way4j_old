package way4j.tools.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import way4j.tools.systemBuild.xmlParser.PackageLocations;
import way4j.tools.systemBuild.xmlParser.SystemParser;
import way4j.tools.utils.constants.Constants;

public class ClassUtils {
	
	private static PackageLocations packageLocations;
	
	static {
		packageLocations = new SystemParser().configurePackageLocations();
	}
	
	public static Class getModelClass(String className) {
		return searchClass(className, new String[]{packageLocations.getModel()});
	}
	
	public static Class searchClass(String className, String[] packages){
		Class clazz = null;

		for (String pct : packages) {
			try {
				clazz = Class.forName(pct + "." + className);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}

		return clazz;
	}
	
	public static Field getIdField(Class clazz){
		Field field = null;
		for(Field f : clazz.getDeclaredFields()){
			if(f.getAnnotation(Id.class) != null){
				return f;
			}
		}	
		return null;
	}
	
	public static Field getField(Class clazz, String fieldName){
		Field field = null;
		for(Field f : clazz.getDeclaredFields()){
			if(f.getName().equals(fieldName)){
				return f;
			}
		}	
		return null;
	}
	
	public static String getTableColumnName(Class clazz, String fieldName){
		Field field = getField(clazz, fieldName);
		String columnName = field.getAnnotation(Column.class).name();
		if(columnName == null || columnName.isEmpty()){
			columnName = fieldName.toLowerCase();
		}
		return columnName;
	}
	
	public static List<Class> getAllModelClasses() throws ClassNotFoundException, IOException {
		return getClasses(packageLocations.getModel());

	}

	public static Class getControllerOfModel(Class clazz) throws ClassNotFoundException{
		return searchClass(getClassName(clazz)+Constants.CONTROLLER_SUFIX, new String[]{packageLocations.getController()});
	}
	
	public static Class getDaoOfModel(Class clazz) throws ClassNotFoundException{
		return searchClass(getClassName(clazz)+Constants.DAO_SUFIX, new String[]{packageLocations.getDao()});
	}
	
	public static Class getServiceOfModel(Class clazz) throws ClassNotFoundException{
		return searchClass(getClassName(clazz)+Constants.SERVICE_SUFIX, new String[]{packageLocations.getService()});
	}
	
	public static Class getLazyDataModelOfModel(Class clazz) throws ClassNotFoundException{
		return searchClass(getClassName(clazz)+Constants.LAZY_DATA_MODEL_SUFIX, new String[]{packageLocations.getLazyDataModel()});
	}
	
	public static String getClassName(Class clazz){
		String[] packageAndClass = clazz.getName().split("\\.");
		return packageAndClass[packageAndClass.length-1];
	}
	
	public static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	private static List<Class> findClasses(File directory, String packageName)throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file,packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName+ '.'+ file.getName().substring(0,file.getName().length() - 6)));
			}
		}
		return classes;
	}
	
	public static boolean classExists(String canonicalName){
		try {
			Class.forName(canonicalName);
			return true;
		} catch (ClassNotFoundException e) {}
		return false;
	}
	
}
