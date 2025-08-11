
package br.com.topsys.web.faces;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.primefaces.event.SelectEvent;


import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSType;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.service.main.TSMainService;
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

	protected abstract TSMainService<T> getService();

	protected boolean validFields() {
		return true;
	}

	public void save() {

		if (!validFields()) {
			return;
		}

		this.initParameters();
		
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

		this.initParameters();
		
		this.getService().delete(model);

		super.addInfoMessage("Operação realizada com sucesso");

	}
	
	protected void posDetail() {
		// TODO Auto-generated method stub

	}
	
	protected void preSave() {
		// TODO Auto-generated method stub

	}
	
	protected void initParameters() {
		this.model.setAccessControlModel(getAccessControl());
		
		this.model.setDataOperacao(OffsetDateTime.now());
	}

}
