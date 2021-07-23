package br.com.topsys.web.faces;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.topsys.base.util.TSUtil;

public final class TSMessageFaces {
	
	private FacesContext facesContext;
	
	public TSMessageFaces(FacesContext facesContext) {
		this.facesContext = facesContext;
	}
	

	public void addWarn(String msg) {
		addInfo(null, msg);
	}

	public void addWarn(String clientId, String msg) {
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, null, msg));
	}

	public void addInfo(String msg) {
		addInfo(null, msg);
	}

	public void addInfo(String clientId, String msg) {
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, null, msg));
	}

	public void addError(String msg) {
		addError(null, msg);
	}

	public void addError(String clientId, String msg) {
		this.facesContext.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, msg));
	}

	public void addResult(List<?> lista) {

		Integer quantidade = 0;

		if (!TSUtil.isEmpty(lista)) {
			quantidade = lista.size();
		}

		this.addResult(quantidade);

	}

	public void addResult(Integer quantidade) {

		String mensagem = "Nenhum registro encontrado";

		if (!TSUtil.isEmpty(quantidade) && quantidade > 0) {

			mensagem = "A pesquisa retornou " + quantidade + " ocorrência(s)";
		}

		addInfo(mensagem);

	}

}
