package br.com.topsys.web.session;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;

public class TSSession {

	private TSSession() {
		 
	}

	public static Object addObjectInSession(String key, Object object) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, object);
	}

	public static Object getObjectInSession(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
	}
	
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

}
  