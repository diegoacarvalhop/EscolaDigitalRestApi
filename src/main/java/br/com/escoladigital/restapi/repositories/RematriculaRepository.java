package br.com.escoladigital.restapi.repositories;

import br.com.escoladigital.restapi.models.Matricula;
import br.com.escoladigital.restapi.models.Rematricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RematriculaRepository extends JpaRepository<Rematricula, Long> {

    Matricula findByMatricula(Matricula matricula);

    Rematricula findById(long id);

}
