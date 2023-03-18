package br.com.topsys.web.session;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.model.TSControleAcessoModel;
import jakarta.servlet.http.HttpSession;


public class TSControleAcessoSession {


	private HttpSession httpSession;
	
	public TSControleAcessoSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public TSControleAcessoModel getTSControleAcesso() { 
		
		if(httpSession == null) {
			throw new TSApplicationException("Sessão expirada!");
		}
		
		TSControleAcessoModel controleAcesso = (TSControleAcessoModel) httpSession.getAttribute(TSTypeSession.CONTROLE_ACESSO_SESSION_MODEL.name());
		
		if (controleAcesso == null) {
			controleAcesso = new TSControleAcessoModel();
			httpSession.setAttribute(TSTypeSession.CONTROLE_ACESSO_SESSION_MODEL.name(), controleAcesso);
		}
		
		return controleAcesso;
	}

}
