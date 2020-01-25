package br.com.escoladigital.restapi.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RematriculaDTO {

    private long id;
    private String dataMatricula;
    private MatriculaDTO matriculaDto;
    private String dataRematricula;
    private BigDecimal valor;
    private BigDecimal valorMensalidade;
    private Integer vencimentoMensalidade;
    private StatusDTO statusDto;
    private String msgSucesso;
    private String msgErro;

}
