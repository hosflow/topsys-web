package br.com.topsys.web.faces;

import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
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
public abstract class TSCrudFaces<T extends TSMainModel> extends TSMainFaces {

	private static final String OPERACAO_OK = "Operação realizada com sucesso!";

	private T model;

	private Integer tabAtiva;

	private boolean limparCampos;

	private List<T> tabelaPesquisa;
	private LazyDataModel<T> tabelaPaginacao;

	@Autowired
	private transient TSRestAPI<T> restAPI;

	protected abstract String getURL();

	protected abstract void inicializarCampos();

	protected abstract Class<T> getCrudClass();

	public TSCrudFaces() {
		limparCampos = true;
	}

	@PostConstruct
	protected void init() {
		inicializarCampos();
	}

	public void onRowSelect(SelectEvent<T> event) {

		if (!TSUtil.isEmpty(event.getObject())) {

			setModel(event.getObject());

			setTabAtiva(0);

			this.obter();

		}

	}

	public void pesquisar() {
		try {
			this.tabelaPesquisa = this.getRestAPI().postList(this.getCrudClass(), this.getURL() + "/pesquisar", this.getModel(), super.getToken());

			this.addResultMessage(tabelaPesquisa);
		} catch (TSSystemException e) {
			this.addErrorMessage(e.getMessage());
		}
	}

	public void obter() {

		this.setModel(this.getRestAPI().post(this.getCrudClass(), this.getURL() + "/obter", this.getModel(), super.getToken()));

	}

	public void inserir() {

		try {

			this.setModel(this.getRestAPI().post(this.getCrudClass(), this.getURL() + "/inserir", this.getModel(), super.getToken()));

			this.addInfoMessage(OPERACAO_OK);

			if (limparCampos) {
				this.inicializarCampos();
			}

		} catch (TSApplicationException e) {
			this.addInfoMessage(e.getMessage());
		} catch (TSSystemException e) {
			this.addErrorMessage(e.getMessage());
		}

	}

	public void alterar() {
		try {

			this.getRestAPI().post(this.getCrudClass(), this.getURL() + "/alterar", this.getModel(), super.getToken());

			this.addInfoMessage(OPERACAO_OK);

		} catch (TSApplicationException e) {
			this.addInfoMessage(e.getMessage());

		}

	}

	public void excluir() {
		try {

			this.getRestAPI().post(this.getCrudClass(), this.getURL() + "/excluir", this.getModel(), super.getToken());

			this.addInfoMessage(OPERACAO_OK);

			this.pesquisar();

		} catch (TSApplicationException e) {
			this.addInfoMessage(e.getMessage());

		}
	}

	public boolean isFlagAlterar() {
		return !TSUtil.isEmpty(this.getModel().getId());
	}

}
