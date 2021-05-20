package br.com.topsys.web.faces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.context.PrimeRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.model.TSControleAcessoModel;
import br.com.topsys.base.util.TSType;
import br.com.topsys.web.session.TSControleAcessoSession;
import br.com.topsys.web.util.TSCookie;
import br.com.topsys.web.util.TSRestAPI;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
@Data
public abstract class TSMainFaces implements Serializable {

	protected static final String TOKEN = "token";
	protected static final String OPERACAO_OK = "Operação realizada com sucesso!";

	@Autowired
	private transient HttpSession httpSession;

	@Autowired
	private transient HttpServletRequest httpServletRequest;

	@Autowired
	private transient HttpServletResponse httpServletResponse;

	@Autowired
	private transient TSRestAPI restAPI;

	private Integer tabActive;

	private boolean clearFields;

	protected List<SelectItem> initCombo(List<?> coll, String nomeValue, String nomeLabel) {
		List<SelectItem> list = new ArrayList<>();

		try {
			for (Object o : coll) {
				list.add(new SelectItem(BeanUtils.getProperty(o, nomeValue), BeanUtils.getProperty(o, nomeLabel)));
			}
		} catch (Exception e) {
			this.handlerException(e);
		}

		return list;
	}

	private TSMessageFaces getMessageFaces() {
		return new TSMessageFaces(FacesContext.getCurrentInstance());
	}

	protected void addWarnMessage(String msg) {
		this.addInfoMessage(null, msg);
	}

	protected void addWarnMessage(String clientId, String msg) {
		this.getMessageFaces().addWarn(clientId, msg);
	}

	protected void addInfoMessage(String msg) {
		this.addInfoMessage(null, msg);
	}

	protected void addInfoMessage(String clientId, String msg) {
		this.getMessageFaces().addInfo(clientId, msg);
	}

	protected void addErrorMessage(String msg) {
		addErrorMessage(null, msg);
	}

	protected void addErrorMessage(String clientId, String msg) {
		this.getMessageFaces().addError(clientId, msg);
	}

	protected void addResultMessage(List<?> lista) {
		this.getMessageFaces().addResult(lista);
	}

	protected void addResultMessage(Integer quantidade) {
		this.getMessageFaces().addResult(quantidade);

	}

	private TSCookie getCookie() {
		return new TSCookie(this.getHttpServletRequest(), this.getHttpServletResponse());
	}

	protected String getToken() {

		return this.getCookie().getValue(TOKEN);
	}

	protected void setToken(String token, Integer duracao) {
		this.getCookie().add(TOKEN, token, duracao);
	}

	protected void removeToken() {
		this.getCookie().add(TOKEN, "", 0);
	}

	protected void addCookie(String nome, String valor) {
		this.getCookie().add(nome, valor, -1);
	}

	protected String getCookie(String nome) {
		return this.getCookie().getValue(nome);
	}

	protected TSControleAcessoModel getTSControleAcesso() {
		return new TSControleAcessoSession(this.getHttpSession()).getTSControleAcesso();
	}

	protected void showDialog(String dialog) {
		StringBuilder builder = new StringBuilder();
		builder.append("PF('");
		builder.append(dialog);
		builder.append("').show()");
		PrimeRequestContext.getCurrentInstance().getScriptsToExecute().add(builder.toString());

	}

	protected void hideDialog(String dialog) {
		StringBuilder builder = new StringBuilder();
		builder.append("PF('");
		builder.append(dialog);
		builder.append("').hide()");
		PrimeRequestContext.getCurrentInstance().getScriptsToExecute().add(builder.toString());

	}

	protected void executeScriptPrime(String valor) {
		PrimeRequestContext.getCurrentInstance().getScriptsToExecute().add(valor);

	}

	protected boolean isValidFields() {
		return true;
	}

	@ExceptionHandler(Exception.class)
	protected void handlerException(Exception e) {
		if (e instanceof TSApplicationException) {

			TSApplicationException tsApplicationException = (TSApplicationException) e;

			if (TSType.ERROR.equals(tsApplicationException.getTSType())) {
				log.debug(e.getMessage());
				this.addErrorMessage(e.getMessage());

			} else {

				this.addInfoMessage(e.getMessage());

			} 

		} else {
			e.printStackTrace();
			this.addErrorMessage(e.getMessage());

		}

	}

}
