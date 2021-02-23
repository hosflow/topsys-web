package br.com.topsys.web.faces;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.util.TSRestAPI;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TSRecordFaces<T extends TSMainModel> extends TSMainFaces {

	@Autowired
	private transient TSRestAPI<T> restAPI;

	private T model;

	protected abstract Class<T> getModelClass();

	protected abstract String getURL();

	protected abstract void initFields();

	@PostConstruct
	protected void init() {
		this.setClearFields(true);
		initFields();
	}

	@SuppressWarnings("unchecked")
	public void onRowSelect(SelectEvent<T> event) {

		if (!TSUtil.isEmpty(event.getObject())) {

			setModel(event.getObject());

			setTabActive(0);

			this.get();

		}

	}

	public void get() {

		this.setModel(this.getRestAPI().post(this.getModelClass(), this.getURL() + "/obter", this.getModel(),
				super.getToken()));

	}

	public void insert() {

		try {

			this.setModel(this.getRestAPI().post(this.getModelClass(), this.getURL() + "/inserir", this.getModel(),
					super.getToken()));

			this.addInfoMessage(OPERACAO_OK);

			if (super.isClearFields()) {
				this.initFields();
			}

		} catch (TSApplicationException e) {
			this.addInfoMessage(e.getMessage());
		} catch (TSSystemException e) {
			this.addErrorMessage(e.getMessage());
		}

	}

	public void update() {
		try {

			this.getRestAPI().post(this.getModelClass(), this.getURL() + "/alterar", this.getModel(), super.getToken());

			this.addInfoMessage(OPERACAO_OK);

		} catch (TSApplicationException e) {
			this.addInfoMessage(e.getMessage());

		}

	}

	public boolean isFlagUpdate() {
		return !TSUtil.isEmpty(this.getModel().getId());
	}

}
