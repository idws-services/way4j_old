package way4j.tools.generics.controller;

import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.util.ReflectionUtils;

import com.google.gson.Gson;

public class JsonReflectionDispatcher {
	
	
	// Criar return para as chamadas
	// return={ method=list, filter={ range:{ min:'', max:'' } } }
	public static void dispatch(ServletRequest request, ServletResponse response){
		
		Object actionObject = null;
		String modelClassName = request.getParameter(Constants.Parameters.MODEL_CLASS.value());
		String jsonObj = request.getParameter(Constants.Parameters.JSON_OBJ.value());
		String method = request.getParameter(Constants.Parameters.METHOD.value());
		GenericController controller = GenericUtils.getControllerOfModel(modelClassName);
		
		if(isDefaultAction(method)){
			if(isDelete(method)){
				controller.deleteById(GenericUtils.gson.fromJson(jsonObj, Long[].class));
			}else if(isList(method)){
				controller.setFilter(jsonObj);
				try {
					response.getWriter().write(controller.listToJson());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				Class modelClass = ClassUtils.getModelClass(modelClassName);
				
				actionObject = new Gson().fromJson(jsonObj, modelClass);
				controller.setActionObj(actionObject);
				
				Method methodObj = ReflectionUtils.findMethod(controller.getClass(), method);
				
				try {
					methodObj.invoke(controller);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}else{
			// ?
		}
		

	}
	
	private static boolean isDefaultAction(String str){
		if(isUpdate(str) || isDelete(str) || isGet(str) || isInsert(str)){
			return true;
		}
		return false;
	}
	
	private static boolean isInsert(String str){
		if(str.equalsIgnoreCase(Constants.DefaultActions.INSERT.value())){
			return true;
		}
		return false;
	}
	
	private static boolean isUpdate(String str){
		if(str.equalsIgnoreCase(Constants.DefaultActions.UPDATE.value())){
			return true;
		}
		return false;
	}
	
	private static boolean isGet(String str){
		if(str.equalsIgnoreCase(Constants.DefaultActions.GET.value())){
			return true;
		}
		return false;
	}
	
	private static boolean isList(String str){
		if(str.equalsIgnoreCase(Constants.DefaultActions.LIST.value())){
			return true;
		}
		return false;
	}
	
	private static boolean isDelete(String str){
		if(str.equalsIgnoreCase(Constants.DefaultActions.DELETE.value())){
			return true;
		}
		return false;
	}
	
	public static boolean isJsonInjectRequest(ServletRequest request){
		if(request.getParameter(Constants.Parameters.JSON_INJECT.value()) != null && request.getParameter(Constants.Parameters.JSON_INJECT.value()).equals("true")){
			return true;
		}else{
			return false;
		}
	}
	
}
