package br.com.topsys.web.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.web.util.TSMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class TSAspectWebException {

	private static final String ERRO_INTERNO = "Ocorreu um erro interno, entre em contato com a TI!";

	@Around("execution(public * br.com.topsys.web..*(..)) || execution(public * br.com..*view..*(..))")
	public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {

		try {

			return joinPoint.proceed();

		} catch (Exception ex) {

			handle(ex);

		}
		return null;

	}

	public void handle(Exception ex) {

		if (ex instanceof TSApplicationException) {

			log.debug(ex.getMessage());

			TSMessage.addErrorMessage(ex.getMessage());

		} else {

			ex.printStackTrace();

			TSMessage.addErrorMessage(ERRO_INTERNO);

		}

	}

}
