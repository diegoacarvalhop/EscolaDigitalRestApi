package br.com.escoladigital.restapi.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusDTO {

	private long id;
	private String descricao;
	private String nemotecnico;
	private String ativo;
	private String msgSucesso;
	private String msgErro;
}
