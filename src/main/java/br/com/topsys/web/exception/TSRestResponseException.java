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

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSResponseExceptionModel;
import br.com.topsys.base.util.TSType;
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
		String body = toString(response.getBody());
		ObjectMapper mapper = new ObjectMapper();
		TSResponseExceptionModel model = mapper.readValue(body, TSResponseExceptionModel.class);
 
		log.error("ResponseBody: {}", model.getMensagem());

	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		String body = toString(response.getBody());
		ObjectMapper mapper = new ObjectMapper();
		TSResponseExceptionModel model = mapper.readValue(body, TSResponseExceptionModel.class);
		
		String ERROR_LOG = "URL: {}, HttpMethod: {}, ResponseBody: {}";
		
		if (model.getCodigo() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            
			log.error(ERROR_LOG, url, method, body);
			
			throw new TSSystemException(model.getMensagem());

		}else if(model.getCodigo() == HttpStatus.BAD_REQUEST.value()) {
			
			log.error(ERROR_LOG, url, method, body);
			
			throw new TSApplicationException(model.getMensagem(),TSType.ERROR);
		}

		throw new TSApplicationException(model.getMensagem());

	}

	private String toString(InputStream inputStream) {
		try (Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
			return s.hasNext() ? s.next() : "";
		}
	}

}
