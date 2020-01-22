package br.com.escoladigital.restapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.escoladigital.restapi.models.Contato;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

	Contato findById(long id);
}
