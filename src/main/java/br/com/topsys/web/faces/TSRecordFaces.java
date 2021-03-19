package br.com.topsys.web.faces;

import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.model.TSRestModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.util.TSRestAPI;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TSRecordFaces<T extends TSMainModel> extends TSMainFaces {

	protected static final String OPERACAO_DUPLICATE_OK = "Clone realizado com sucesso! Após gravar, as informação serão inseridas.";

	@Autowired
	private transient TSRestAPI restAPI;

	private T model;

	private List<T> tableHistory;

	protected abstract Class<T> getModelClass();

	protected abstract String getURL();

	private String historyActiveTabIndex;

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

	@SuppressWarnings("unchecked")
	public void get() {

		try {

			this.setModel((T) this.getRestAPI().post(TSRestModel.builder()
											.modelClass(this.getModelClass())
											.model(this.getModel())
											.url(this.getURL() + "/get")
											.token(super.getToken())
											.build()));

			this.afterGet();

		} catch (Exception e) {
			handlerException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public void getHistory(T modelHistory) {

		try {

			T history = (T) this.getRestAPI().post(TSRestModel.builder()
											.modelClass(this.getModelClass())
											.model(modelHistory)
											.url(this.getURL() + "/get-history")
											.token(super.getToken())
											.build());

			this.tableHistory.set(this.tableHistory.indexOf(history), history);

		} catch (Exception e) {
			handlerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void findHistory() {

		try {

			this.tableHistory = this.getRestAPI().postList(TSRestModel.builder()
												.modelClass(this.getModelClass())
												.model(this.getModel())
												.url(this.getURL() + "/find-history")
												.token(super.getToken())
												.build());

			setHistoryActiveTabIndex("-1");

		} catch (Exception e) {
			handlerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void insert() {

		if (!isValidFields()) {
			return;
		}

		try {

			this.beforePersist();

			this.beforeInsert();

			this.setModel((T) this.getRestAPI().post(TSRestModel.builder()
												.modelClass(this.getModelClass())
												.model(this.getModel())
												.url(this.getURL() + "/insert")
												.token(super.getToken())
												.build()));

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

			this.getRestAPI().post(TSRestModel.builder()
							.modelClass(this.getModelClass())
							.model(this.getModel())
							.url(this.getURL() + "/update")
							.token(super.getToken())
							.build());
					
					

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
