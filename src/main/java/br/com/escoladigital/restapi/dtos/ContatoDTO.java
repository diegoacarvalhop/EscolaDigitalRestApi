package br.com.escoladigital.restapi.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContatoDTO {

    private long id;
    private String telefoneFixo;
    private String telefonePai;
    private String telefoneMae;
    private String emailMae;
    private String emailPai;
    private String msgSucesso;
    private String msgErro;

}
