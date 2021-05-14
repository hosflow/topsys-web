package br.com.topsys.web.faces;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import br.com.topsys.base.model.TSLazyModel;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.model.TSRestModel;
import br.com.topsys.base.util.TSUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TSSearchFaces<T extends TSMainModel> extends TSMainFaces {

	private T model;

	protected abstract Class<T> getModelClass(); 

	protected abstract String getURL();

	private List<T> table;
	private LazyDataModel<T> tablePagination;

	public void initFields() { 

		try {

			this.setModel(getModelClass().getDeclaredConstructor().newInstance());
			this.setTable(Collections.emptyList());
			this.tablePagination = null;

		} catch (Exception e) {
			handlerException(e);

		}

	}


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

			this.table = super.getRestAPI().postList(this.getModelClass(),
													TSRestModel.builder()
																.model(this.getModel())
																.url(this.getURL() + "/find")
																.token(this.getToken())
																.build());			
					

			this.addResultMessage(table);
			
		} catch (Exception e) {
			handlerException(e);
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

			super.getRestAPI().post(this.getModelClass(),
									TSRestModel.builder()
									.url(this.getURL() + "/delete")
									.model(this.getModel())
									.token(this.getToken())
									.build());	
				
			this.addInfoMessage(OPERACAO_OK);

			this.find();

		} catch (Exception e) {
			handlerException(e);
		}
	}

	private class LazyList extends LazyDataModel<T> {

		public LazyList() {
			setRowCount(getRestAPI().post(Integer.class,
											TSRestModel.builder()
											.model(getModel())
											.url(getURL() + "/rowcount")
											.token(getToken())
											.build()));
							
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
				handlerException(e);

			}

			return modelRow;

		}

		@Override
		public String getRowKey(T model) {
			return String.valueOf(model.getId());
		}

		@Override
		public List<T> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
			List<T> retorno = null;
			try {
				retorno =  getRestAPI().postList(getModelClass(), 
												TSRestModel.builder()
															.model(new TSLazyModel<T>(getModel(), offset, pageSize))
															.url(getURL() + "/find-lazy")
															.token(getToken())
															.build());
					

			} catch (Exception e) {
				handlerException(e);
			}

			return retorno;

		}

	}

}
