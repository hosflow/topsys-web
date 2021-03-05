package br.com.topsys.web.faces;

import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

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

	private List<T> tableHistory;

	protected abstract Class<T> getModelClass();

	protected abstract String getURL();

	private String historyActiveTabIndex;

	public void initFields() {

		try {

			this.setModel(getModelClass().getDeclaredConstructor().newInstance());

		} catch (Exception e) {
			throw new TSSystemException(e);

		}

	}

	@PostConstruct
	protected void init() {
		this.setClearFields(true);
		initFields();
	}

	public void onRowSelect(SelectEvent<T> event) {

		if (!TSUtil.isEmpty(event.getObject())) {

			setModel(event.getObject());

			setTabActive(0);

			this.get();

		}

	}

	protected void afterGet() {
	}

	protected void beforeInsert() {
	}

	protected void beforeUpdate() {
	}

	protected void beforePersist() {
	}

	public void get() {
		this.setModel(this.getRestAPI().post(this.getModelClass(), this.getURL() + "/get", this.getModel(),
				super.getToken()));

		this.afterGet();

	}

	public void getHistory(T modelHistory) {

		T history = this.getRestAPI().post(this.getModelClass(), this.getURL() + "/get-history", modelHistory,
				super.getToken());

		this.tableHistory.set(this.tableHistory.indexOf(history), history);
	}

	public void findHistory() {

		this.tableHistory = this.getRestAPI().postList(this.getModelClass(), this.getURL() + "/find-history",
				this.getModel(), super.getToken());

		setHistoryActiveTabIndex("-1");

	}

	public void insert() {

		if (!isValidFields()) {
			return;
		}

		this.beforePersist();

		this.beforeInsert();

		this.setModel(this.getRestAPI().post(this.getModelClass(), this.getURL() + "/insert", this.getModel(),
				super.getToken()));

		this.addInfoMessage(OPERACAO_OK);

		if (super.isClearFields()) {
			this.initFields();
		}

	}

	public void update() {

		if (!isValidFields()) {
			return;
		}

		this.beforePersist();

		this.beforeUpdate();

		this.getRestAPI().post(this.getModelClass(), this.getURL() + "/update", this.getModel(), super.getToken());

		this.addInfoMessage(OPERACAO_OK);

	}

	public boolean isFlagUpdate() {
		return !TSUtil.isEmpty(this.getModel().getId());
	}

}
