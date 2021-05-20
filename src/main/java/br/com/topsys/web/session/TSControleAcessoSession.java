package br.com.topsys.web.session;

import javax.servlet.http.HttpSession;

import br.com.topsys.base.model.TSControleAcessoModel;


public final class TSControleAcessoSession {


	private HttpSession httpSession;
	
	public TSControleAcessoSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public TSControleAcessoModel getTSControleAcesso() {
		TSControleAcessoModel controleAcesso = (TSControleAcessoModel) httpSession.getAttribute(TSTypeSession.CONTROLE_ACESSO_SESSION_MODEL.name());
		
		if (controleAcesso == null) {
			controleAcesso = new TSControleAcessoModel();
			httpSession.setAttribute(TSTypeSession.CONTROLE_ACESSO_SESSION_MODEL.name(), controleAcesso);
		}
		
		return controleAcesso;
	}

}
