
package br.com.topsys.web.faces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;


import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSAccessControlModel;
import br.com.topsys.base.util.TSType;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.session.TSSession;
import br.com.topsys.web.session.TSTypeSession;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("serial")
@Slf4j
public abstract class TSMainView implements Serializable {

	protected TSAccessControlModel getAccessControl() {
		return (TSAccessControlModel) TSSession.getObjectInSession(TSTypeSession.ACCESS_CONTROL_SESSION_MODEL.name());
	}
	
	protected void init() { 
		
	}
	
	protected void addInfoMessage(String msg) {
		this.addMessage(FacesMessage.SEVERITY_INFO, "Info!", msg);
	}
	
	protected void addErrorMessage(String msg) {
		this.addMessage(FacesMessage.SEVERITY_ERROR, "Erro!", msg);
	}
	
	protected void addWarnMessage(String msg) {
		this.addMessage(FacesMessage.SEVERITY_WARN, "Alerta!", msg);
	}
	
	private void addMessage(Severity severity, String header, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, header, message));
	}
	
	@SuppressWarnings("rawtypes")
	protected List<SelectItem> initCombo(Collection coll, String nomeValue, String nomeLabel) {
		
		List<SelectItem> list = new ArrayList<SelectItem>();

		for (Object o : coll) {
			try {

				list.add(new SelectItem(PropertyUtils.getProperty(o, nomeValue), String.valueOf(PropertyUtils.getProperty(o, nomeLabel))));

			} catch (Exception e) {

				e.printStackTrace();

				throw new TSSystemException(e);
			}
		}
		return list;
	}
	
	@ExceptionHandler(Exception.class)
	protected void handlerException(Exception e) {
		this.handlerException(e, null);

	}
	
	protected void handlerException(Exception e, String message) {
		
		String messageAux = TSUtil.isEmpty(message) ? e.getMessage() : message;
				
		if (e instanceof TSApplicationException) {

			TSApplicationException tsApplicationException = (TSApplicationException) e;

			if (TSType.ERROR.equals(tsApplicationException.getTSType())) {
				log.error(messageAux);
				this.addErrorMessage(messageAux);

			} else {

				this.addInfoMessage(messageAux);

			} 
 
		} else {
			
			this.addErrorMessage("Erro inesperado, ocorreu um erro inesperado. Contate a TI");
			
			e.printStackTrace();
		

		}

	}
	
}
