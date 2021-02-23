package br.com.topsys.web.faces;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.exception.TSSystemException;
import br.com.topsys.base.model.TSLazyModel;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.web.util.TSRestAPI;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TSSearchFaces<T extends TSMainModel> extends TSMainFaces {

	@Autowired
	private transient TSRestAPI<T> restAPI;

	private T model;

	protected abstract Class<T> getModelClass();

	protected abstract String getURL();

	protected abstract void initFields();

	private List<T> table;
	private LazyDataModel<T> tablePagination;

	@PostConstruct
	protected void init() {
		this.setClearFields(true);
		initFields();
	}

	public void find() {

		if (!isValidFields()) {
			return;
		}

		try {

			this.table = this.getRestAPI().postList(this.getModelClass(), this.getURL() + "/find", this.getModel(),
					super.getToken());

			this.addResultMessage(table);
		} catch (TSSystemException e) {
			this.addErrorMessage(e.getMessage());
		}
	}

	public void findPagination() {

		if (!isValidFields()) {
			return;
		}

		this.tablePagination = new LazyList();

	}

	public void delete() {
		try {

			this.getRestAPI().post(this.getModelClass(), this.getURL() + "/delete", this.getModel(), super.getToken());

			this.addInfoMessage(OPERACAO_OK);

			this.find();

		} catch (TSApplicationException e) {
			this.addInfoMessage(e.getMessage());

		}
	}

	private class LazyList extends LazyDataModel<T> {

		public LazyList() {
			setRowCount(
					(Integer) getRestAPI().postObject(Integer.class, getURL() + "/rowcount", getModel(), getToken()));
			addResultMessage(getRowCount());
		}

		@Override
		public T getRowData(String rowKey) {

			Long id = Long.valueOf(rowKey);

			T modelRow = null;

			try {

				if (TSUtil.isEmpty(getModel().getId()) || !getModel().getId().equals(id)) {
					modelRow = getModelClass().getDeclaredConstructor().newInstance();
					modelRow.setId(id);
				}

			} catch (Exception e) {
				throw new TSSystemException(e);

			}

			return modelRow;

		}

		@Override
		public String getRowKey(T model) {
			return String.valueOf(model.getId());
		}

		@Override
		public List<T> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

			return getRestAPI().postList(getModelClass(), getURL() + "/find-lazy",
					new TSLazyModel<T>(getModel(), offset, pageSize), getToken());

		}

	}

}
