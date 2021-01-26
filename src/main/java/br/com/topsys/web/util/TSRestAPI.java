package br.com.topsys.web.util;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.util.TSType;
import br.com.topsys.web.exception.TSRestResponseException;
import br.com.topsys.web.faces.TSMainFaces;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TSRestAPI<T extends Serializable> {

	private String baseURL;

	public String getBaseURL() {
		return this.baseURL;
	}

	private RestTemplate restTemplate = null;

	
	public TSRestAPI(String baseURL) {
		this.baseURL = baseURL;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new TSRestResponseException());

	}

	public T postReturnObject(Class<T> classe, String url, T object) {

		T retorno = null;

		HttpEntity<T> entity = null;

		try {
			entity = new HttpEntity<T>(object);
			
			retorno = (T) restTemplate.postForObject(this.getBaseURL() + url, entity, classe);
			
			TSMainFaces.addInfoMessage("Operação realizada com sucesso!");

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	@SuppressWarnings("unchecked")
	public List<T> postReturnList(String url, T object) {

		List<T> retorno = null;

		HttpEntity<T> entity = null;

		try {
			entity = new HttpEntity<T>(object);

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, List.class);

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;

	}

	public List<T> postReturnList(String url) {

		return this.postReturnList(url, null);

	}

	private void handlerException(RuntimeException e) {
		if (e instanceof TSApplicationException) {

			TSApplicationException tsApplicationException = (TSApplicationException) e;

			if (tsApplicationException.getTSType().equals(TSType.BUSINESS)) {

				TSMainFaces.addInfoMessage(e.getMessage());

			} else {

				TSMainFaces.addErrorMessage(e.getMessage());
			}

		} else {

			log.error(e.getMessage());

			TSMainFaces.addErrorMessage(e.getMessage());

		}

	}

}
