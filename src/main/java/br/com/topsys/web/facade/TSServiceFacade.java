package br.com.topsys.web.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.topsys.base.model.TSRestModel;
import br.com.topsys.web.util.TSRestAPI;
import lombok.Data;

@Data
public abstract class TSServiceFacade {
	
	@Autowired
	private transient TSRestAPI restAPI;
	
	private String token;
	
	public TSServiceFacade() {
		
	}
	
	public TSServiceFacade(String token) {
		this.token = token;
	}
	
	
	public <T> T post(Class<T> classe, Object model, String endpoint) {

		return this.getRestAPI().post(classe, TSRestModel.builder().model(model).url(endpoint).token(this.getToken()).build());

	}
	
	public <T> List<T> list(Class<T> classe, Object model, String endpoint) {

		return this.getRestAPI().postList(classe, TSRestModel.builder().model(model).url(endpoint).token(this.getToken()).build());


	}

}
