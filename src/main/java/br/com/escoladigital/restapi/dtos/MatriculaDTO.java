package br.com.escoladigital.restapi.dtos;

import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.models.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MatriculaDTO {

    private long id;
    private Aluno aluno;
    private String dataMatricula;
    private BigDecimal valor;
    private BigDecimal valorMensalidade;
    private Integer vencimentoMensalidade;
    private Status status;
    private String msgSucesso;
    private String msgErro;

}
