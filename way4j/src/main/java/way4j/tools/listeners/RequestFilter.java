package way4j.tools.listeners;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import way4j.tools.generics.controller.JsonReflectionDispatcher;

public class RequestFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	private boolean jsonRequest(ServletRequest request, ServletResponse response){
		// Funcionalidade de requisições através de json, retornando um json como resultado, 
		// está implementado apenas uma parte, e não é o foco no momento.
		if(JsonReflectionDispatcher.isJsonInjectRequest(request)){
			JsonReflectionDispatcher.dispatch(request, response);
			return true;
		}
		return false;
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	
}
