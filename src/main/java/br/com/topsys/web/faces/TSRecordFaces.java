package br.com.topsys.web.faces;

import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;

import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.model.TSRestModel;
import br.com.topsys.base.util.TSUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TSRecordFaces<T extends TSMainModel> extends TSMainFaces {

	protected static final String OPERACAO_DUPLICATE_OK = "Clone realizado com sucesso! Após gravar, as informação serão inseridas.";

	private T model;

	private List<T> tableHistory;

	protected abstract Class<T> getModelClass();

	protected abstract String getURL();

	private String historyActiveTabIndex;

	private static final String DASHBOARD = "dashboard?faces-redirect=true";

	public void initFields() {

		try {

			this.setModel(getModelClass().getDeclaredConstructor().newInstance());

		} catch (Exception e) {
			handlerException(e);

		}

	}

	@PostConstruct
	protected void init() {
		this.setClearFields(true);
		initFields();
	}

	@SuppressWarnings("unchecked")
	public void onRowSelect(SelectEvent<?> event) {

		setModel((T)event.getObject());

		setTabActive(0);

		this.get();

	}

	public String clearFields() {

		initFields();

		return DASHBOARD;
	}

	/*public String onRowSelect() {

		setTabActive(0);

		this.get();

		return null;

	}*/

	protected void afterGet() {
	}

	protected void beforeInsert() {
	}

	protected void beforeUpdate() {
	}

	protected void beforePersist() {
	}

	protected void afterInsert() {
	}

	protected void afterUpdate() {
	}

	protected void afterPersist() {
	}

	public void get() {

		try {

			this.setModel(super.getRestAPI().post(this.getModelClass(), TSRestModel.builder().model(this.getModel())
					.url(this.getURL() + "/get").token(super.getToken()).build()));

			this.afterGet();

		} catch (Exception e) {
			handlerException(e);
		}

	}

	public void getHistory(T modelHistory) {

		try {

			T history = super.getRestAPI().post(this.getModelClass(), TSRestModel.builder().model(modelHistory)
					.url(this.getURL() + "/get-history").token(super.getToken()).build());

			this.tableHistory.set(this.tableHistory.indexOf(history), history);

		} catch (Exception e) {
			handlerException(e);
		}
	}

	public void findHistory() {

		try {

			this.tableHistory = super.getRestAPI().postList(this.getModelClass(), TSRestModel.builder()
					.model(this.getModel()).url(this.getURL() + "/find-history").token(super.getToken()).build());

			setHistoryActiveTabIndex("-1");

		} catch (Exception e) {
			handlerException(e);
		}
	}

	public void insert() {

		if (!isValidFields()) {
			return;
		}

		try {

			this.beforePersist();

			this.beforeInsert();

			this.setModel(super.getRestAPI().post(this.getModelClass(), TSRestModel.builder().model(this.getModel())
					.url(this.getURL() + "/insert").token(super.getToken()).build()));

			this.afterPersist();

			this.afterInsert();

			this.addInfoMessage(OPERACAO_OK);

			if (super.isClearFields()) {
				this.initFields();
			}

		} catch (Exception e) {
			handlerException(e);
		}

	}

	public void update() {

		if (!isValidFields()) {
			return;
		}

		try {

			this.beforePersist();

			this.beforeUpdate();

			super.getRestAPI().post(this.getModelClass(), TSRestModel.builder().model(this.getModel())
					.url(this.getURL() + "/update").token(super.getToken()).build());

			this.afterPersist();

			this.afterInsert();

			this.addInfoMessage(OPERACAO_OK);

		} catch (Exception e) {
			handlerException(e);
		}

	}

	public void duplicate() {

		if (!TSUtil.isEmpty(getModel())) {
			getModel().setId(null);
			this.addInfoMessage(OPERACAO_DUPLICATE_OK);
		}

	}

	public boolean isFlagUpdate() {
		return !TSUtil.isEmpty(this.getModel().getId());
	}

}
