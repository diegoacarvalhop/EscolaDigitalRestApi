package br.com.escoladigital.restapi.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ALUNO")
@Getter
@Setter
public class Aluno implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String nome;

	@Column(name = "data_nascimento", nullable = false)
	private LocalDate dataNascimento;

	@Column(name = "nome_mae", nullable = false)
	private String nomeMae;

	@Column(name = "nome_pai")
	private String nomePai;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "matricula_id", nullable = false)
	private Matricula matricula;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "endereco_id", nullable = false)
	private Endereco endereco;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "contato_id", nullable = false)
	private Contato contato;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status_id", nullable = false)
	private Status status;

}
