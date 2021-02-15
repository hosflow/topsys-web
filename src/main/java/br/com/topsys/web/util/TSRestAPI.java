package br.com.topsys.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.exception.TSRestResponseException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
public final class TSRestAPI<T extends Serializable> {

	private static final String NAO_PODE_SER_NULO = "O objeto passado por parâmetro do método post não pode ser nulo!";

	private String baseURL;

	private RestTemplate restTemplate;

	public TSRestAPI(String baseURL) {
		this.baseURL = baseURL;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new TSRestResponseException());
	}

	public T post(Class<T> classe, String url, Object object, String token) {

		T retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		try {
			if (TSUtil.isEmpty(object)) {
				throw new TSSystemException(NAO_PODE_SER_NULO);
			}

			if (!TSUtil.isEmpty(token)) {
				
				HttpHeaders headers = new HttpHeaders();
				headers.setBearerAuth(token);
				entity = new HttpEntity<Object>(object, headers);
			
			} else {
				entity = new HttpEntity<Object>(object);
			}

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, classe);

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = new ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 

				retorno = objectMapper.convertValue(retorno, classe);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	public Object postObject(Class<?> classe, String url, Object object, String token) {

		Object retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		try {
			if (TSUtil.isEmpty(object)) {
				throw new TSSystemException(NAO_PODE_SER_NULO);
			}

			if (!TSUtil.isEmpty(token)) {
				
				HttpHeaders headers = new HttpHeaders();
				headers.setBearerAuth(token);
				entity = new HttpEntity<Object>(object, headers);
			
			} else {
				entity = new HttpEntity<Object>(object);
			}

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, classe);

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = new ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 


				retorno = objectMapper.convertValue(retorno, classe);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	@SuppressWarnings("unchecked")
	public List<T> postList(Class<T> classe, String url, Object object, String token) {

		List<T> retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		CollectionType listType = null;

		try {
			if (TSUtil.isEmpty(object)) {
				throw new TSSystemException(NAO_PODE_SER_NULO);
			}

			if (!TSUtil.isEmpty(token)) {
				
				HttpHeaders headers = new HttpHeaders();
				headers.setBearerAuth(token);
				entity = new HttpEntity<Object>(object, headers);
			
			} else {
				entity = new HttpEntity<Object>(object);
			}

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, List.class);

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = new ObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); 


				listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classe);

				retorno = objectMapper.convertValue(retorno, listType);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	private void handlerException(RuntimeException e) {
		String ERRO_INTERNO = "Ocorreu um erro interno, entre em contato com a TI!";

		if (e instanceof TSApplicationException) {

			throw new TSApplicationException(e.getMessage(), e);

		} else {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new TSSystemException(ERRO_INTERNO, e);
		}

	}

}
