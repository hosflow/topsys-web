package br.com.topsys.web.session;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TSControleAcessoSession {

	@Autowired
	private HttpSession httpSession;
	

	private TSControleAcesso controleAcesso = null;
	
	public TSControleAcessoSession() {
		
		controleAcesso = (TSControleAcesso) httpSession.getAttribute(TSTypeSession.OBJECT_SESSION_MODEL.name());
		
		if(controleAcesso == null) {
			this.controleAcesso = new TSControleAcesso();
			httpSession.setAttribute(TSTypeSession.OBJECT_SESSION_MODEL.name(), this.controleAcesso);
		}
		
	}
	
	public TSControleAcesso getTSControleAcesso() {
		return this.controleAcesso;
	}
	
	
	
	
	
	
	
}
