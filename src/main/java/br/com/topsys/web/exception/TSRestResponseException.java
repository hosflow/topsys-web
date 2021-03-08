package br.com.topsys.web.exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSResponseExceptionModel;
import br.com.topsys.base.util.TSType;
import br.com.topsys.base.util.TSUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component  
public class TSRestResponseException implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {

		return response.getStatusCode().series() == Series.CLIENT_ERROR
				|| response.getStatusCode().series() == Series.SERVER_ERROR;
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		
		log.error("ResponseBody: {}", getObjectMapper(toString(response.getBody())).getMessage());

	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		
		String body = toString(response.getBody());
	
		TSResponseExceptionModel model = getObjectMapper(body);
		
		String erroLog = "URL: {}, HttpMethod: {}, ResponseBody: {}";
		
		if (model.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			
			throw new TSSystemException(model.getMessage());

		}else if(model.getStatus() == HttpStatus.BAD_REQUEST.value()
				|| model.getStatus() == HttpStatus.FORBIDDEN.value()
				|| model.getStatus() == HttpStatus.NOT_FOUND.value()) {
			
			log.error(erroLog, url, method, body);
			
			if(!TSUtil.isEmpty(model.getTrace())) {
				log.error("Trace: {}", model.getTrace());
			}
			
			throw new TSApplicationException(model.getMessage(),TSType.ERROR);
		
		}

		throw new TSApplicationException(model.getMessage());

	}

	private String toString(InputStream inputStream) {
		try (Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}
	
	private TSResponseExceptionModel getObjectMapper(String body) throws IOException {
	
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		return objectMapper.readValue(body, TSResponseExceptionModel.class);
		
	}

}
