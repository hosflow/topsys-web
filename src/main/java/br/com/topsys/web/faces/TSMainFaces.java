package br.com.topsys.web.faces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.util.TSRestAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class TSMainFaces<T extends Serializable> {

	private String baseURL;

	private T model;

	protected String getBaseURL() {
		return this.baseURL;
	}

	protected void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public T getModel() {
		return this.model;
	}

	public void setModel(T model) {
		this.model = model;
	}

	protected T postReturnObject(Class<T> classe, String url, T objeto) {

		return (T) new TSRestAPI<T>(this.baseURL).postReturnObject(classe, url, objeto);
	}

	protected List<T> postReturnList(String url, T object) {

		return new TSRestAPI<T>(this.baseURL).postReturnList(url, object);

	}

	protected List<T> postReturnList(String url) {

		return this.postReturnList(url, null);

	}

	@SuppressWarnings("static-access")
	protected List<SelectItem> initCombo(List<?> coll, String nomeValue, String nomeLabel) {
		List<SelectItem> list = new ArrayList<SelectItem>();

		for (Object o : coll) {
			try {

				list.add(new SelectItem(BeanUtils.getProperty(o, nomeValue), BeanUtils.getProperty(o, nomeLabel)));

			} catch (Exception e) {
				log.error(e.getMessage());

				this.addErrorMessage(e.getMessage());
			}
		}
		return list;
	}

	protected ServletContext getServletContext() {
		return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	}

	protected Object getManagedBeanInSession(String beanName) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(beanName);
	}

	protected void addObjectInSession(String beanName, Object managedBean) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
	}

	protected void addObjectInRequest(String beanName, Object managedBean) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(beanName, managedBean);

	}

	protected void addRequestParameter(String name, String object) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().put(name, object);
	}

	protected void removeObjectInSession(String beanName) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(beanName);
	}

	protected Object getObjectInSession(String beanName) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(beanName);
	}

	protected String getRequestParameter(String name) {
		return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);

	}

	protected Object getObjectInRequest(String name) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(name);

	}

	public static void addInfoMessage(String msg) {
		addInfoMessage(null, msg);
	}

	protected static void addInfoMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, null, msg));
	}

	protected HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	protected HttpServletResponse getResponse() {
		return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public static void addErrorMessage(String msg) {
		addErrorMessage(null, msg);
	}

	protected static void addErrorMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null, msg));
	}

	protected void addResultMessage(List<?> lista) {
		this.addResultMessage(lista, null);
	}

	protected void addResultMessage(List<?> lista, String destino) {

		if (TSUtil.isEmpty(lista)) {

			String msg_nenhuma_ocorrencia = "A pesquisa não retornou nenhuma ocorrência";

			if (TSUtil.isEmpty(destino)) {
				addInfoMessage(destino, msg_nenhuma_ocorrencia);
			} else {
				addInfoMessage(msg_nenhuma_ocorrencia);
			}

		} else {

			String msg_ocorrencia = "A pesquisa retornou " + lista.size() + " ocorrência(s)";

			if (TSUtil.isEmpty(destino)) {
				addInfoMessage(destino, msg_ocorrencia);
			} else {
				addInfoMessage(msg_ocorrencia);
			}

		}

	}

	protected Cookie getCookie(String nome) {

		Cookie cookies[] = getRequest().getCookies();

		Cookie donaBenta = null;

		if (cookies != null) {

			for (int x = 0; x < cookies.length; x++) {

				if (cookies[x].getName().equals(nome)) {

					donaBenta = cookies[x];

					break;

				}

			}

		}

		return donaBenta;

	}

	protected void addCookie(String nome, String valor, Integer duracao) {

		Cookie donaBenta = new Cookie(nome, valor);

		donaBenta.setMaxAge(duracao);

		getResponse().addCookie(donaBenta);

	}

}
