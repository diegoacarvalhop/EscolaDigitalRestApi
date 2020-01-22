package br.com.escoladigital.restapi.repositories;

import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.models.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Matricula findByAluno(Aluno aluno);

    Matricula findById(long id);

}
