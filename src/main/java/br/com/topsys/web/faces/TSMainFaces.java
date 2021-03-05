package br.com.topsys.web.faces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.context.PrimeRequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.topsys.base.model.TSControleAcesso;
import br.com.topsys.web.session.TSControleAcessoSession;
import br.com.topsys.web.util.TSCookie;
import br.com.topsys.web.util.TSMessageFaces;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
@Data
public abstract class TSMainFaces extends TSMessageFaces implements Serializable {

	protected static final String TOKEN = "token";
	protected static final String OPERACAO_OK = "Operação realizada com sucesso!";

	@Autowired
	private transient HttpSession httpSession;

	@Autowired
	private transient HttpServletRequest httpServletRequest;

	@Autowired
	private transient HttpServletResponse httpServletResponse;

	private Integer tabActive;

	private boolean clearFields;

	protected List<SelectItem> initCombo(List<?> coll, String nomeValue, String nomeLabel) {
		List<SelectItem> list = new ArrayList<>();

		for (Object o : coll) {
			try {

				list.add(new SelectItem(BeanUtils.getProperty(o, nomeValue), BeanUtils.getProperty(o, nomeLabel)));

			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();

				this.addErrorMessage(e.getMessage());
			}
		}
		return list;
	}

	protected ServletContext getServletContext() {
		return (ServletContext) getFacesContext().getExternalContext().getContext();
	}

	

	protected String getToken() {
		return TSCookie.getCookie(httpServletRequest, TOKEN) != null
				? TSCookie.getCookie(httpServletRequest, TOKEN).getValue()
				: null;
	}

	protected void setToken(String token, Integer duracao) {
		TSCookie.addCookie(httpServletResponse, TOKEN, token, duracao);
	}

	protected void removeToken() {
		TSCookie.addCookie(httpServletResponse, TOKEN, "", 0);
	}

	protected void addCookie(String nome, String valor) {
		TSCookie.addCookie(httpServletResponse, nome, valor, -1);
	}

	protected String getCookie(String nome) {
		return TSCookie.getCookie(httpServletRequest, nome) != null
				? TSCookie.getCookie(httpServletRequest, nome).getValue()
				: null;
	}

	protected TSControleAcesso getTSControleAcesso() {
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

	
	

}
