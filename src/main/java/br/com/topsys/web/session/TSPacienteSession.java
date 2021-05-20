package br.com.topsys.web.session;

import javax.servlet.http.HttpSession;

import br.com.topsys.base.model.TSPacienteModel;

public final class TSPacienteSession {

	private HttpSession httpSession;

	public TSPacienteSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public TSPacienteModel getTSPaciente() {
		TSPacienteModel pacienteModel = (TSPacienteModel) httpSession
				.getAttribute(TSTypeSession.PACIENTE_SESSION_MODEL.name());

		if (pacienteModel == null) {
			pacienteModel = new TSPacienteModel();
			httpSession.setAttribute(TSTypeSession.PACIENTE_SESSION_MODEL.name(), pacienteModel);
		}

		return pacienteModel;
	}

}
