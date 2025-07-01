
package br.com.topsys.web.faces;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.primefaces.event.SelectEvent;


import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSType;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.service.main.TSMainBusiness;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("serial")
public abstract class TSRecordView<T extends TSMainModel> extends TSMainView implements Serializable {

	protected T model;

	protected T selectionModel;

	public void init() {
		super.init();

	}

	protected abstract TSMainBusiness<T> getService();

	protected boolean validaCampos() {
		return true;
	}

	public void save() {

		if (!validaCampos()) {
			return;
		}

		this.model.setAccessControlModel(getAccessControl());

		this.preSave();

		if (TSUtil.isEmpty(this.model.getId())) {

			this.executeInsert();

		} else {

			this.executeUpdate();

		}

		super.addInfoMessage("Operação realizada com sucesso");

	}

	protected void executeInsert() {
		this.getService().insert(this.model);
	}

	protected void executeUpdate() {
		this.getService().update(this.model);
	}

	

	public void detail(Long id) {

		T model = this.getService().get(id);

		if (model == null) {
			throw new TSApplicationException("Registro não encontrado", TSType.ERROR);
		}

		this.model = model;

		this.posDetail();

	}

	

	public void detailSelection() {

		if (this.selectionModel == null) {
			return;
		}

		this.detail(this.selectionModel.getId());
	}

	public void detailSelectionEvent(SelectEvent<T> event) {

		if (TSUtil.isEmptyId(event.getObject())) {
			return;
		}

		this.detail(event.getObject().getId());
	}

	public void delete(Long id) {

		T model = this.getService().get(id);

		if (model == null) {
			throw new TSApplicationException("Registro não encontrado", TSType.ERROR);
		}

		model.setDataAtualizacao(OffsetDateTime.now());
		model.setAccessControlModel(this.getAccessControl());
		
		this.getService().delete(model);

		super.addInfoMessage("Operação realizada com sucesso");

	}
	
	protected void posDetail() {
		// TODO Auto-generated method stub

	}
	
	protected void preSave() {
		// TODO Auto-generated method stub

	}

}
