package br.com.escoladigital.restapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.models.Status;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

	Aluno findByNome(String nome);

	Aluno findById(long id);

	List<Aluno> findByStatus(Status status);
}
