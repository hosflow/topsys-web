package br.com.topsys.web.faces;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import br.com.topsys.base.exception.TSApplicationException;
import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class TSSearchFaces<T extends TSMainModel> extends TSMainFaces {

	private T model;

	protected abstract Class<T> getModelClass(); 

	private List<T> table;
	private LazyDataModel<T> tablePagination;
	
	protected void beforeDelete() {
	}
	
	protected void afterDelete() {
	}

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

			this.table = handleFind();		
					

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

			this.beforeDelete();
			
			handleDelete();
			
			this.afterDelete();
				
			this.addInfoMessage(OPERACAO_OK);

			this.find();

		} catch (Exception e) {
			handlerException(e);
		}
	}
	
	
	protected List<T> handleFind() {
		return null;
	}
	
	protected List<T> handleFindLazy() {
		return null;
	}
	
	protected void handleDelete() throws TSApplicationException {
		
	}
	
	protected Integer handleRowCount() {
		return null;
	}
	

	private class LazyList extends LazyDataModel<T> {

		private int count = 0;
		
		public LazyList() {
			count = handleRowCount();
			
			setRowCount(count);
							
			addResultMessage(count);
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
				
				retorno =  handleFindLazy();
					
			} catch (Exception e) {
				handlerException(e);
			}

			return retorno;

		}

		@Override
		public int count(Map<String, FilterMeta> filterBy) {
			return count;
		}

	}
	
	

}
