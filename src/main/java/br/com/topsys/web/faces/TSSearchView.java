
package br.com.topsys.web.faces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import br.com.topsys.base.model.TSMainModel;
import br.com.topsys.base.util.TSUtil;
import br.com.topsys.service.main.TSMainService;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("serial")
public abstract class TSSearchView<T extends TSMainModel> extends TSMainView {

	protected T searchModel;

	protected LazyDataModel<T> lazyList;

	protected abstract TSMainService<T> getService();

	public void init() {
		super.init();
		this.lazyList = null;
	}

	public void find() {
		
		if (validFields()) {
			return;
		}
		
		this.lazyList = new LazyList(this.searchModel);
	}

	protected boolean validFields() {
		
		return false;
	}

	class LazyList extends LazyDataModel<T> {

		private T model;
		private List<T> grid;
		private int first;

		public LazyList(T model) {

			this.model = model;

			this.first = -1;

		}

		@Override
		public T getRowData(String rowKey) {

			Optional<T> optional = grid.stream().filter(item -> Long.valueOf(rowKey).equals(item.getId())).findFirst();

			if (optional.isPresent()) {
				return optional.get();
			}

			return null;

		}

		@Override
		public String getRowKey(T object) {
			return String.valueOf(object.getId());
		}

		@Override
		public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {

			if (first != this.first || !TSUtil.isEmpty(sortBy)) {
				this.first = first;
				this.grid = getService().find(model, first, pageSize);
			}

			return this.grid;

		}

		@Override
		public int count(Map<String, FilterMeta> filterBy) {
			return getService().rowCount(model);
		}

	}

}
