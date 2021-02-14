package br.com.topsys.web.faces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.session.TSControleAcesso;
import br.com.topsys.web.session.TSControleAcessoSession;
import br.com.topsys.web.util.TSCookie;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
@Data
public abstract class TSMainFaces implements Serializable {

	private static final String SMPEP_TOKEN = "smpep.token";

	@Value("${smpep.base.url}")
	private String baseURL;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private HttpServletResponse httpServletResponse;

	@PostConstruct
	private void init() {
		this.setBaseURL(this.baseURL);
	}

	protected List<SelectItem> initCombo(List<?> coll, String nomeValue, String nomeLabel) {
		List<SelectItem> list = new ArrayList<SelectItem>();

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

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected void addWarnMessage(String msg) {
		addInfoMessage(null, msg);
	}

	protected void addWarnMessage(String clientId, String msg) {
		getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, null, msg));
	}

	protected void addInfoMessage(String msg) {
		addInfoMessage(null, msg);
	}

	protected void addInfoMessage(String clientId, String msg) {
		getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, null, msg));
	}

	protected void addErrorMessage(String msg) {
		addErrorMessage(null, msg);
	}

	protected void addErrorMessage(String clientId, String msg) {
		getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, msg));
	}

	protected void addResultMessage(List<?> lista) {
		this.addResultMessage(lista, null);
	}

	protected void addResultMessage(List<?> lista, String destino) {

		String mensagem = "A pesquisa não retornou nenhuma ocorrência";

		if (!TSUtil.isEmpty(lista)) {

			mensagem = "A pesquisa retornou " + lista.size() + " ocorrência(s)";
		}

		if (TSUtil.isEmpty(destino)) {
			addInfoMessage(destino, mensagem);
		} else {
			addInfoMessage(mensagem);
		}

	}

	protected String getToken() {
		return TSCookie.getCookie(httpServletRequest, SMPEP_TOKEN) != null
				? TSCookie.getCookie(httpServletRequest, SMPEP_TOKEN).getValue()
				: null;
	}

	protected void setToken(String token, Integer duracao) {
		TSCookie.addCookie(httpServletResponse, SMPEP_TOKEN, token, duracao);
	}

	protected void removeToken() {
		TSCookie.addCookie(httpServletResponse, SMPEP_TOKEN, "", 0);
	}
	
	protected TSControleAcesso getTSControleAcesso() {
		return new TSControleAcessoSession().getTSControleAcesso();
	}

}
