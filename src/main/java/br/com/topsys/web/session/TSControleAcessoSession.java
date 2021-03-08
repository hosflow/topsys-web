package br.com.topsys.web.session;

import javax.servlet.http.HttpSession;

import br.com.topsys.base.model.TSControleAcesso;


public final class TSControleAcessoSession {


	private HttpSession httpSession;
	
	public TSControleAcessoSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public TSControleAcesso getTSControleAcesso() {
		TSControleAcesso controleAcesso = (TSControleAcesso) httpSession.getAttribute(TSTypeSession.OBJECT_SESSION_MODEL.name());
		
		if (controleAcesso == null) {
			controleAcesso = new TSControleAcesso();
			httpSession.setAttribute(TSTypeSession.OBJECT_SESSION_MODEL.name(), controleAcesso);
		}
		
		return controleAcesso;
	}

}
