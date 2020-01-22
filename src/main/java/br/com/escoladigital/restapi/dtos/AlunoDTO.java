package br.com.escoladigital.restapi.dtos;

import br.com.escoladigital.restapi.models.Contato;
import br.com.escoladigital.restapi.models.Endereco;
import br.com.escoladigital.restapi.models.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlunoDTO {

	private long id;
	private String nome;
	private String dataNascimento;
	private String nomeMae;
	private String nomePai;
	private Endereco endereco;
	private Contato contato;
	private Status status;
	private String msgSucesso;
	private String msgErro;
}
