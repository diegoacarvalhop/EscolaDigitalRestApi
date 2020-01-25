package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.dtos.ContatoDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Contato;
import br.com.escoladigital.restapi.repositories.ContatoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContatoService {

    private static final Logger logger = LogManager.getLogger(ContatoService.class);

    private ContatoRepository repository;

    @Autowired
    public ContatoService(ContatoRepository repository) {
        super();
        this.repository = repository;
    }

    public ContatoDTO salvar(ContatoDTO contatoDto) {
        logger.info("Salvando Contato.");
            Contato contato = new Contato();
            contato.setTelefoneFixo(contatoDto.getTelefoneFixo());
            contato.setTelefoneMae(contatoDto.getTelefoneMae());
            contato.setTelefonePai(contatoDto.getTelefonePai());
            contato.setEmailMae(contatoDto.getEmailMae());
            contato.setEmailPai(contatoDto.getEmailPai());
            return montarDTOSucesso(repository.save(contato), ValidacaoEnum.SALVAR.getDescricao());
    }

    public ContatoDTO editar(ContatoDTO contatoDto) {
        Contato contatoEditar = repository.findById(contatoDto.getId());
        logger.info("Editando Matricula.");
        contatoEditar.setTelefoneMae(contatoDto.getTelefoneMae());
        contatoEditar.setTelefonePai(contatoDto.getTelefonePai());
        contatoEditar.setTelefoneFixo(contatoDto.getTelefoneFixo());
        contatoEditar.setEmailPai(contatoDto.getEmailPai());
        contatoEditar.setEmailMae(contatoDto.getEmailMae());
        return montarDTOSucesso(repository.save(contatoEditar), ValidacaoEnum.EDITAR.getDescricao());
    }

    public ContatoDTO montarDto(Contato contato) {
        ContatoDTO contatoDto = new ContatoDTO();
        contatoDto.setId(contato.getId());
        contatoDto.setTelefoneFixo(contato.getTelefoneFixo());
        contatoDto.setTelefoneMae(contato.getTelefoneMae());
        contatoDto.setTelefonePai(contato.getTelefonePai());
        contatoDto.setEmailMae(contato.getEmailMae());
        contatoDto.setEmailPai(contato.getEmailPai());
        return contatoDto;
    }

    public Contato montarContato(ContatoDTO contatoDto) {
        return repository.findById(contatoDto.getId());
    }

    public ContatoDTO montarDTOSucesso(Contato contato, String validacao) {
        logger.info("Montando objeto de sucesso.");
        ContatoDTO contatoDto = montarDto(contato);
        if (validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
            contatoDto.setMsgSucesso("Matrícula cadastrada com sucesso.");
        } else if (validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
            contatoDto.setMsgSucesso("Matrícula editada com sucesso.");
        }
        return contatoDto;
    }

}
