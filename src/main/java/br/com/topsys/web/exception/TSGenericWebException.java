package br.com.topsys.web.exception;


import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.util.TSType;
import br.com.topsys.web.util.TSMessageFaces;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TSGenericWebException extends TSMessageFaces{

	protected void handlerException(Exception e) {
		
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
