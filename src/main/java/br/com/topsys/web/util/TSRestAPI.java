package br.com.topsys.web.util;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSRetornoModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.exception.TSRestResponseException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
public final class TSRestAPI<T extends Serializable> {

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

	@SuppressWarnings("unchecked")
	public TSRetornoModel<T> post(Class<T> classe, String url, Object object) {

		TSRetornoModel<T> retorno = null;

		HttpEntity<Object> entity = null;

		ObjectMapper objectMapper = null;

		CollectionType listType = null;

		try {
			if (TSUtil.isEmpty(object)) {
				throw new TSSystemException("O objeto passado por parâmetro do método post não pode ser nulo!");
			}
			entity = new HttpEntity<Object>(object);

			retorno = restTemplate.postForObject(this.getBaseURL() + url, entity, TSRetornoModel.class);

			if (!TSUtil.isEmpty(retorno)) {

				objectMapper = new ObjectMapper();

				if (!TSUtil.isEmpty(retorno.getList())) {

					listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, classe);

					retorno.setList(objectMapper.convertValue(retorno.getList(), listType));

				} else {

					retorno.setModel(objectMapper.convertValue(retorno.getModel(), classe));

				}

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
