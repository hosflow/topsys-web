package br.com.topsys.web.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.topsys.web.faces.TSMainFaces;

@SuppressWarnings("serial")
@ControllerAdvice
@Component
public class TSWebException extends TSMainFaces{

	@ExceptionHandler(Exception.class)
	 public void systemException(Exception e){
		 handlerException(e);
	 }
	 
	  
}
