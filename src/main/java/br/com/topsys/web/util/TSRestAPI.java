package br.com.topsys.web.util;

import java.io.Serializable;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSLazyModel;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.model.TSRestModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.exception.TSRestResponseException;
import br.com.topsys.web.session.TSControleAcessoSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
public final class TSRestAPI {

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

	

	public Object post(TSRestModel restModel) {

		Object model = accessControl(restModel.getModel());

		Object retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		try {

			entity = getHttpEntity(model, restModel.getToken());

			if (TSUtil.isEmpty(restModel.getBaseUrl())) {
				retorno = restTemplate.postForObject(this.getBaseURL() + restModel.getUrl(), entity, restModel.getModelClass());
			} else {
				retorno = restTemplate.postForObject(restModel.getBaseUrl() + restModel.getUrl(), entity, restModel.getModelClass());
			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				retorno = objectMapper.convertValue(retorno, restModel.getModelClass());
			}

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}

	
	public Object get(TSRestModel restModel) {

		Object retorno = null;

		ObjectMapper objectMapper = null;

		try {
			if (TSUtil.isEmpty(restModel.getBaseUrl())) {
				retorno = restTemplate.getForObject(this.getBaseURL() +  restModel.getUrl(),  restModel.getModelClass());
			} else {
				retorno = restTemplate.getForObject(restModel.getBaseUrl() +  restModel.getUrl(),  restModel.getModelClass());
			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				retorno = objectMapper.convertValue(retorno,  restModel.getModelClass());
			}

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}


	

	@SuppressWarnings("rawtypes")
	public List postList(TSRestModel restModel) {

		Object model = accessControl(restModel.getModel());

		List retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		CollectionType listType = null;

		try {

			entity = getHttpEntity(model, restModel.getToken());

			if (TSUtil.isEmpty(restModel.getBaseUrl())) {
				retorno = restTemplate.postForObject(this.getBaseURL() + restModel.getUrl(), entity, List.class);
			} else {
				retorno = restTemplate.postForObject(restModel.getBaseUrl() + restModel.getUrl(), entity, List.class);

			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, restModel.getModelClass());

				retorno = objectMapper.convertValue(retorno, listType);
			}

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}

	

	
	@SuppressWarnings("rawtypes")
	public List getList(TSRestModel restModel) {

		List retorno = null;

		ObjectMapper objectMapper = null;

		CollectionType listType = null;

		try {

			if (TSUtil.isEmpty(restModel.getBaseUrl())) {
				retorno = restTemplate.getForObject(this.getBaseURL() + restModel.getUrl(), List.class);
			} else {
				retorno = restTemplate.getForObject(restModel.getBaseUrl() + restModel.getUrl(), List.class);

			}

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = getObjectMapper();

				listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, restModel.getModelClass());

				retorno = objectMapper.convertValue(retorno, listType);
			}

		} catch (Exception e) {
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
			TSLazyModel<? extends TSMainModel> model = (TSLazyModel<? extends TSMainModel>) object;
			model.getModel().setControleAcesso(new TSControleAcessoSession(this.getHttpSession()).getTSControleAcesso());
			return model;
		}

		if (object instanceof TSMainModel) {
			TSMainModel model = (TSMainModel) object;
			model.setControleAcesso(new TSControleAcessoSession(this.getHttpSession()).getTSControleAcesso());
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

	private void handlerException(Exception e) {
		String erroInterno = "Ocorreu um erro interno, entre em contato com a TI!";

		if (e instanceof TSApplicationException) {

			throw new TSApplicationException(e.getMessage(),((TSApplicationException) e).getTSType());

		} else {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new TSSystemException(erroInterno, e);
		}

	}

}
