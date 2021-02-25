package br.com.topsys.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSLazyModel;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.exception.TSRestResponseException;
import br.com.topsys.web.session.TSControleAcessoSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
public final class TSRestAPI<T extends TSMainModel> {

	private static final String NAO_PODE_SER_NULO = "O objeto passado por parâmetro do método post não pode ser nulo!";

	@Autowired
	private HttpSession httpSession;

	@Value("${topsys.base.url}")
	private String baseURL;

	private RestTemplate restTemplate;

	public TSRestAPI(String baseURL) {
		this.baseURL = baseURL;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new TSRestResponseException());
	}

	public TSRestAPI() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new TSRestResponseException());
	}

	public T post(Class<T> classe, String url, final Object object, String token) {

		return this.post(classe, null, url, object, token);

	}

	public T post(Class<T> classe, String baseUrl, String url, final Object object, String token) {

		Object model = accessControl(object);

		T retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		try {

			entity = getHttpEntity(model, token);

			if (TSUtil.isEmpty(baseUrl)) {
				retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, classe);
			} else {
				retorno = restTemplate.postForObject(baseUrl + url, entity, classe);
			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				retorno = objectMapper.convertValue(retorno, classe);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	public T get(Class<T> classe, String url) {

		return this.get(classe, null, url);

	}

	public T get(Class<T> classe, String baseUrl, String url) {

		T retorno = null;

		ObjectMapper objectMapper = null;

		try {

			if (TSUtil.isEmpty(baseUrl)) {
				retorno = restTemplate.getForObject(this.getBaseURL() + url, classe);
			} else {
				retorno = restTemplate.getForObject(baseUrl + url, classe);
			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				retorno = objectMapper.convertValue(retorno, classe);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;
	}

	public Object postObject(Class<?> classe, String url, final Object object, String token) {

		return this.postObject(classe, null, url, object, token);
	}

	public Object postObject(Class<?> classe, String baseUrl, String url, final Object object, String token) {

		Object model = accessControl(object);

		Object retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		try {

			entity = getHttpEntity(model, token);

			if (TSUtil.isEmpty(baseUrl)) {
				retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, classe);
			} else {
				retorno = restTemplate.postForObject(baseUrl + url, entity, classe);
			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				retorno = objectMapper.convertValue(retorno, classe);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;

	}

	public Object getObject(Class<?> classe, String url) {

		return this.getObject(classe, null, url);
	}

	public Object getObject(Class<?> classe, String baseUrl, String url) {

		Object retorno = null;

		ObjectMapper objectMapper = null;

		try {
			if (TSUtil.isEmpty(baseUrl)) {
				retorno = restTemplate.getForObject(this.getBaseURL() + url, classe);
			} else {
				retorno = restTemplate.getForObject(baseUrl + url, classe);
			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				retorno = objectMapper.convertValue(retorno, classe);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;

	}

	public List<T> postList(Class<T> classe, String url, Object object, String token) {

		return this.postList(classe, null, url, object, token);

	}

	@SuppressWarnings("unchecked")
	public List<T> postList(Class<T> classe, String baseUrl, String url, Object object, String token) {

		Object model = accessControl(object);

		List<T> retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		CollectionType listType = null;

		try {

			entity = getHttpEntity(model, token);

			if (TSUtil.isEmpty(baseUrl)) {
				retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, List.class);
			} else {
				retorno = restTemplate.postForObject(baseUrl + url, entity, List.class);

			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classe);

				retorno = objectMapper.convertValue(retorno, listType);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;

	}

	public List<T> getList(Class<T> classe, String url) {
		return this.getList(classe, null, url);

	}

	@SuppressWarnings("unchecked")
	public List<T> getList(Class<T> classe, String baseUrl, String url) {

		List<T> retorno = null;

		ObjectMapper objectMapper = null;

		CollectionType listType = null;

		try {

			if (TSUtil.isEmpty(baseUrl)) {
				retorno = restTemplate.getForObject(this.getBaseURL() + url, List.class);
			} else {
				retorno = restTemplate.getForObject(baseUrl + url, List.class);

			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classe);

				retorno = objectMapper.convertValue(retorno, listType);
			}

		} catch (RuntimeException e) {
			this.handlerException(e);
		}

		return retorno;

	}

	private HttpEntity<Object> getHttpEntity(Object object, String token) {

		if (TSUtil.isEmpty(object)) {
			throw new TSSystemException(NAO_PODE_SER_NULO);
		}

		HttpEntity<Object> entity;

		if (!TSUtil.isEmpty(token)) {

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(token);
			entity = new HttpEntity<>(object, headers);

		} else {
			entity = new HttpEntity<>(object);
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	private Object accessControl(final Object object) {

		if (object instanceof TSLazyModel) {
			TSLazyModel<T> model = (TSLazyModel<T>) object;
			model.getModel().setControleAcesso(new TSControleAcessoSession(this.httpSession).getTSControleAcesso());
			return model;
		}

		if (object instanceof TSMainModel) {
			TSMainModel model = (TSMainModel) object;
			model.setControleAcesso(new TSControleAcessoSession(this.httpSession).getTSControleAcesso());
			return model;
		}

		return object;
	}

	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new JavaTimeModule());	
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		return objectMapper;
	}

	private void handlerException(RuntimeException e) {
		String erroInterno = "Ocorreu um erro interno, entre em contato com a TI!";

		if (e instanceof TSApplicationException) {

			throw new TSApplicationException(e.getMessage(), e);

		} else {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new TSSystemException(erroInterno, e);
		}

	}

}
