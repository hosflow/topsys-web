package br.com.topsys.web.session;



import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.model.TSAccessControlModel;
import jakarta.servlet.http.HttpSession;

public class TSAccessControlSession {

 
	private HttpSession httpSession;
	
	public TSAccessControlSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public TSAccessControlModel getTSControleAcesso() { 
		
		if(httpSession == null) {
			throw new TSApplicationException("Sessão expirada!");
		}
		
		TSAccessControlModel controleAcesso = (TSAccessControlModel) httpSession.getAttribute(TSTypeSession.ACCESS_CONTROL_SESSION_MODEL.name());
		
		if (controleAcesso == null) {
			controleAcesso = new TSAccessControlModel();
			httpSession.setAttribute(TSTypeSession.ACCESS_CONTROL_SESSION_MODEL.name(), controleAcesso);
		}
		
		return controleAcesso;
	}

}
