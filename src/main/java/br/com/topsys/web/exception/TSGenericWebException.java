package br.com.topsys.web.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.util.TSType;
import br.com.topsys.web.util.TSMessageFaces;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class TSGenericWebException extends TSMessageFaces{

	@ExceptionHandler(Exception.class)
	public void handlerException(Exception e) {
		
		if (e instanceof TSApplicationException) {

			TSApplicationException tsApplicationException = (TSApplicationException) e;

			if (TSType.ERROR.equals(tsApplicationException.getTSType())) {
                log.info(e.getMessage());
				this.addErrorMessage(e.getMessage());

			} else {

				this.addInfoMessage(e.getMessage());

			}

		} else {
			e.printStackTrace();
			this.addErrorMessage(e.getMessage());

		}

	}
	
}
