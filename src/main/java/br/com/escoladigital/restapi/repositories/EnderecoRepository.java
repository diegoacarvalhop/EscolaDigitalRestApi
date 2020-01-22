package br.com.escoladigital.restapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.escoladigital.restapi.models.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

	Endereco findById(long id);
}
