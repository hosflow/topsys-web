package br.com.topsys.web.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.context.FacesContext;

public class TSMessage {
	
	public static void addInfoMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_INFO, "Info!", msg);
	}
	
	public static void addErrorMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_ERROR, "Erro!", msg);
	}
	
	public static void addWarnMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_WARN, "Alerta!", msg);
	}
	
	private static void addMessage(Severity severity, String header, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, header, message));
	}

}
