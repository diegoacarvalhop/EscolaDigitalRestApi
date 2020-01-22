package br.com.escoladigital.restapi.repositories;

import br.com.escoladigital.restapi.models.Matricula;
import br.com.escoladigital.restapi.models.Mensalidade;
import br.com.escoladigital.restapi.models.Rematricula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    Matricula findByMatricula(Matricula matricula);

    Rematricula findByRematricula(Rematricula rematricula);

    Mensalidade findById(long id);

}
