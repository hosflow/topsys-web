package br.com.topsys.web.session;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public final class TSControleAcesso implements Serializable {
	
	private Long usuarioId;
	private Long moduloId;
	private Long usuarioFuncaoId;
	private Long funcaoId;
	private Long origemId;
	private Long menuAtualId;
	
	private String nomeUsuario;
	private String nomeFuncao;
	private String nomeModulo;
	private String nomeOrigem;
	
	private String slug;

}
