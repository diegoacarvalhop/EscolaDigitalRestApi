package br.com.escoladigital.restapi.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CONTATO")
@Getter
@Setter
public class Contato implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "telefone_fixo")
	private String telefoneFixo;

	@Column(name = "telefone_pai")
	private String telefonePai;

	@Column(name = "telefone_mae")
	private String telefoneMae;

	@Column(name = "email_mae")
	private String emailMae;

	@Column(name = "email_pai")
	private String emailPai;

}
