package br.com.topsys.web.util;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
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
public class TSRestAPI {

	private static final String NAO_PODE_SER_NULO = "O objeto passado por parâmetro do método post não pode ser nulo!";

	@Autowired
	private HttpSession httpSession;

	@Value("${topsys.base.url}")
	private String baseURL;

	private RestTemplate restTemplate;

	public TSRestAPI() {
		this.restTemplate = new RestTemplate();
		this.restTemplate.setErrorHandler(new TSRestResponseException());
	}

	public <T> T post(Class<T> classe, TSRestModel restModel) {

		T retorno = null;

		try {

			Object model = accessControl(restModel.getModel());

			retorno = postForObject(classe, restModel, model);

			retorno = convertObject(classe, retorno);

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}


	public <T> List<T> postList(Class<T> classe, TSRestModel restModel) {

		List<T> retorno = null;

		try {

			Object model = accessControl(restModel.getModel());

			retorno = postForList(restModel, model);

			retorno = convertList(classe, retorno);

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}

	public <T> T get(Class<T> classe, TSRestModel restModel) {

		T retorno = null;

		try {
			retorno = getForObject(classe, restModel);

			retorno = convertObject(classe, retorno);

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}

	public <T> List<T> getList(Class<T> classe, TSRestModel restModel) {

		List<T> retorno = null;

		try {

			retorno = getForList(restModel);

			retorno = convertList(classe, retorno);

		} catch (Exception e) {
			this.handlerException(e);
		}

		return retorno;

	}
	
	private <T> T postForObject(Class<T> classe, TSRestModel restModel, Object model) {
		
		HttpEntity<Object> entity = getHttpEntity(model, restModel.getToken());

		return TSUtil.isEmpty(restModel.getBaseUrl()) ?
		       restTemplate.postForObject(this.getBaseURL() + restModel.getUrl(), entity, classe) :
	           restTemplate.postForObject(restModel.getBaseUrl() + restModel.getUrl(), entity, classe);
	
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> postForList(TSRestModel restModel, Object model) {

		HttpEntity<Object> entity = getHttpEntity(model, restModel.getToken());

		return TSUtil.isEmpty(restModel.getBaseUrl())
				? restTemplate.postForObject(this.getBaseURL() + restModel.getUrl(), entity, List.class)
				: restTemplate.postForObject(restModel.getBaseUrl() + restModel.getUrl(), entity, List.class);

	}

	private <T> T getForObject(Class<T> classe, TSRestModel restModel) {

		return TSUtil.isEmpty(restModel.getBaseUrl())
				? restTemplate.getForObject(this.getBaseURL() + restModel.getUrl(), classe)
				: restTemplate.getForObject(restModel.getBaseUrl() + restModel.getUrl(), classe);

	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getForList(TSRestModel restModel) {

		return TSUtil.isEmpty(restModel.getBaseUrl())
				? restTemplate.getForObject(this.getBaseURL() + restModel.getUrl(), List.class)
				: restTemplate.getForObject(restModel.getBaseUrl() + restModel.getUrl(), List.class);

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
			model.getModel()
					.setControleAcesso(new TSControleAcessoSession(this.getHttpSession()).getTSControleAcesso());
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
		objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		
		return objectMapper; 
	}

	private <T> T convertObject(Class<T> classe, T retorno) {

		if (!TSUtil.isEmpty(retorno)) {

			retorno = getObjectMapper().convertValue(retorno, classe);
		}

		return retorno;
	}

	private <T> List<T> convertList(Class<T> classe, List<T> retorno) {

		if (!TSUtil.isEmpty(retorno)) {

			ObjectMapper objectMapper = getObjectMapper();

			CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classe);

			retorno = objectMapper.convertValue(retorno, listType);
		}

		return retorno;
	}

	private void handlerException(Exception e) {
		String erroInterno = "Ocorreu um erro interno, entre em contato com a TI!";

		if (e instanceof TSApplicationException) {

			throw new TSApplicationException(e.getMessage(), ((TSApplicationException) e).getTSType());

		}else if (e instanceof ResourceAccessException) {
			
			throw new TSSystemException("Serviço indisponível!");
			
		} else {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new TSSystemException(erroInterno, e);
		}

	}
	
	/*
	private RestTemplate createRestTemplateSslWithPooling() {
        SSLContext sslContext = new SSLContextBuilder()
            .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
            .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
            .<ConnectionSocketFactory> create()
            .register("https", socketFactory)
            .build();
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnManager.setMaxTotal(50);
        poolingConnManager.setDefaultMaxPerRoute(50);
        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(socketFactory)
            .setConnectionManager(poolingConnManager)
            .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
    */

}
