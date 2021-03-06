package br.com.topsys.web.exception;


import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.util.TSType;
import br.com.topsys.web.util.TSMessageFaces;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TSGenericWebException {

	protected void handlerException(Exception e) {
		
		if (e instanceof TSApplicationException) {

			TSApplicationException tsApplicationException = (TSApplicationException) e;

			if (TSType.ERROR.equals(tsApplicationException.getTSType())) {
                log.info(e.getMessage());
				new TSMessageFaces().addErrorMessage(e.getMessage());

			} else {

				new TSMessageFaces().addInfoMessage(e.getMessage());

			}

		} else {
			e.printStackTrace();
			new TSMessageFaces().addErrorMessage(e.getMessage());

		}

	}
	
}
