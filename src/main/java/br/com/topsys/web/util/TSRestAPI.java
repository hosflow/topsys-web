package br.com.topsys.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
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

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, classe);
			

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	@SuppressWarnings("unchecked")
	public List<T> postReturnList(Class<T> classe, String url, T object) {

		List<T> retorno = null;

		HttpEntity<T> entity = null;

		try {
			entity = new HttpEntity<T>(object);

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, List.class);

			ObjectMapper objectMapper = new ObjectMapper();
			CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classe);

			retorno = objectMapper.convertValue(retorno, listType);

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;

	}

	private void handlerException(RuntimeException e) {
		String ERRO_INTERNO = "Ocorreu um erro interno, entre em contato com a TI!";

		if (e instanceof TSApplicationException) {

			TSApplicationException tsApplicationException = (TSApplicationException) e;

			if (tsApplicationException.getTSType().equals(TSType.BUSINESS)) {

				TSMainFaces.addInfoMessage(e.getMessage());

			} else {

				TSMainFaces.addErrorMessage(e.getMessage());
			}
		} else {

			throw new TSSystemException(ERRO_INTERNO,e);
		}

	}

}
