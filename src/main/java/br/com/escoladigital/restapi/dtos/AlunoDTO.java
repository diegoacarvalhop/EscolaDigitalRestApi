package br.com.escoladigital.restapi.dtos;

import br.com.escoladigital.restapi.models.Contato;
import br.com.escoladigital.restapi.models.Endereco;
import br.com.escoladigital.restapi.models.Matricula;
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
	private MatriculaDTO matriculaDto;
	private EnderecoDTO enderecoDto;
	private ContatoDTO contatoDto;
	private StatusDTO statusDto;
	private String msgSucesso;
	private String msgErro;
}
