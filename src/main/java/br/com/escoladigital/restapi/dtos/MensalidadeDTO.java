package br.com.escoladigital.restapi.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MensalidadeDTO {

    private long id;
    private MatriculaDTO matriculaDto;
    private RematriculaDTO rematriculaDto;
    private BigDecimal valor;
    private Integer vencimento;
    private String mes;
    private String ano;
    private StatusDTO statusDto;
    private String msgSucesso;
    private String msgErro;

}
