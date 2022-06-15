package br.com.topsys.web.facade;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.topsys.web.util.TSRestAPI;
import lombok.Data;

@Data
public abstract class TSServiceFacade {
	
	@Autowired
	private transient TSRestAPI restAPI;

}
