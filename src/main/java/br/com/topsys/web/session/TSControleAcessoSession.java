package br.com.topsys.web.session;

import javax.servlet.http.HttpSession;


public class TSControleAcessoSession {


	private HttpSession httpSession;

	private TSControleAcesso controleAcesso = null;

	public TSControleAcessoSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	
	

	public TSControleAcesso getTSControleAcesso() {
		controleAcesso = (TSControleAcesso) httpSession.getAttribute(TSTypeSession.OBJECT_SESSION_MODEL.name());
		
		if (controleAcesso == null) {
			this.controleAcesso = new TSControleAcesso();
			httpSession.setAttribute(TSTypeSession.OBJECT_SESSION_MODEL.name(), this.controleAcesso);
		}
		
		return this.controleAcesso;
	}

}
