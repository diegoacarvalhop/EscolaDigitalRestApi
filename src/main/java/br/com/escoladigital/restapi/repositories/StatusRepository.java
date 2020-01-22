package br.com.escoladigital.restapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.escoladigital.restapi.models.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {

	Status findByNemotecnico(String nemotecnico);

	Status findById(long id);
}
