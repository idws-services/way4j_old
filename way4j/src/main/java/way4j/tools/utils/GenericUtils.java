package way4j.tools.utils;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JsonConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import way4j.tools.generics.controller.GenericController;
import way4j.tools.generics.dao.GenericDao;
import way4j.tools.generics.service.GenericService;

import com.google.gson.Gson;

@Component
public class GenericUtils {
	
	public static GenericApplicationContext springContext;
	public static Gson json = new Gson();
	public static void setApplicationContext(GenericApplicationContext appContext){
		springContext = appContext;
	}
	
	public static boolean isJsonArray(Object json){
		
		try {
			new JSONArray(""+json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static GenericController getController(Class model){
		try {
			return GenericUtils.springContext.getBean(ModelUtils.getControllerOfModel(model));
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static GenericService getService(Class model){
		try {
			return GenericUtils.springContext.getBean(ModelUtils.getServiceOfModel(model));
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GenericDao getDao(Class model){
		try {
			return GenericUtils.springContext.getBean(ModelUtils.getServiceOfModel(model));
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isJsonObject(Object json){
		try {
			new JSONObject(""+json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isJson(String json){
		return isJsonArray(json) || isJsonObject(json);
	}
		
	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			if (json == null || json.toString().length() == 0) {
				return map;
			}
			
			final JsonConfig cfg = new JsonConfig();
			cfg.setRootClass(LinkedHashMap.class);
			cfg.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
			cfg.setHandleJettisonSingleElementArray(false);
			return net.sf.json.JSONObject.fromObject(json, cfg);
		} catch (Exception e) {
			return map;
		}
	}
	
	public ApplicationContext getSpringContext() {
		return springContext;
	}

	public static String firstCharToUpperCase(String str){
		char first = Character.toUpperCase(str.charAt(0));
		str = first + str.substring(1);
		return str;
	}
	
	public static String firstCharToLowerCase(String str){
		char first = Character.toLowerCase(str.charAt(0));
		str = first + str.substring(1);
		return str;
	}

	public static Class getGenericTypeClass(Class generic){
		return (Class)((ParameterizedType)generic.getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public static String replaceLastString(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }

}
