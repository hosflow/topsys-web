package br.com.topsys.web.util;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.topsys.base.util.TSUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TSMessageFaces {
	
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public void addWarnMessage(String msg) {
		addInfoMessage(null, msg);
	}

	public void addWarnMessage(String clientId, String msg) {
		getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, null, msg));
	}

	public void addInfoMessage(String msg) {
		addInfoMessage(null, msg);
	}

	public void addInfoMessage(String clientId, String msg) {
		getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, null, msg));
	}

	public void addErrorMessage(String msg) {
		addErrorMessage(null, msg);
	}

	public void addErrorMessage(String clientId, String msg) {
		getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, msg));
	}

	public void addResultMessage(List<?> lista) {

		Integer quantidade = 0;

		if (!TSUtil.isEmpty(lista)) {
			quantidade = lista.size();
		}

		this.addResultMessage(quantidade);

	}

	public void addResultMessage(Integer quantidade) {

		String mensagem = "A pesquisa não retornou nenhuma ocorrência";

		if (!TSUtil.isEmpty(quantidade) && quantidade > 0) {

			mensagem = "A pesquisa retornou " + quantidade + " ocorrência(s)";
		}

		addInfoMessage(mensagem);

	}

}
