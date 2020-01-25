package br.com.escoladigital.restapi.enuns;

import lombok.Getter;

@Getter
public enum ValidacaoEnum {

	EXISTE("existe"), 
	NAO_EXISTE("naoExiste"), 
	NULL("nulo"), 
	BRANCO("branco"), 
	OK("ok"), 
	SALVAR("salvar"),
	EDITAR("editar"), 
	DELETAR("deletar"), 
	SIM("S"), 
	NAO("N"), 
	FK("chaveEstrangeira"), 
	ZERO("zero"),
	STATUS_NAO_EXISTE("statusNaoExiste"),
	ERRO("erro");

	private String descricao;

	ValidacaoEnum(String descricao) {
		this.descricao = descricao;
	}

}
