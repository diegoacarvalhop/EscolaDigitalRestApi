package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.controllers.MatriculaController;
import br.com.escoladigital.restapi.dtos.MatriculaDTO;
import br.com.escoladigital.restapi.dtos.MensalidadeDTO;
import br.com.escoladigital.restapi.dtos.RematriculaDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Matricula;
import br.com.escoladigital.restapi.models.Mensalidade;
import br.com.escoladigital.restapi.models.Rematricula;
import br.com.escoladigital.restapi.repositories.MatriculaRepository;
import br.com.escoladigital.restapi.repositories.MensalidadeRepository;
import br.com.escoladigital.restapi.repositories.RematriculaRepository;
import br.com.escoladigital.restapi.repositories.StatusRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MensalidadeService {

    private static final Logger logger = LogManager.getLogger(MatriculaController.class);

    private MensalidadeRepository repository;
    private StatusRepository repositoryStatus;
    private MatriculaRepository repositoryMatricula;
    private RematriculaRepository repositoryRematricula;

    private List<String> meses = Arrays.asList("Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro");

    @Autowired
    private MatriculaService serviceMatricula;

    @Autowired
    private StatusService serviceStatus;

    @Autowired
    public MensalidadeService(RematriculaRepository repositoryRematricula, MatriculaRepository repositoryMatricula, StatusRepository repositoryStatus, MensalidadeRepository repository) {
        super();
        this.repository = repository;
        this.repositoryStatus = repositoryStatus;
        this.repositoryMatricula = repositoryMatricula;
        this.repositoryRematricula = repositoryRematricula;
    }

    public MensalidadeService() {
        super();
    }

    public List<MensalidadeDTO> salvar(MatriculaDTO matriculaDto, RematriculaDTO rematriculaDto) {
        logger.info("Salvando as Mensalidades.");
        List<MensalidadeDTO> mensalidadesDto = null;
        Mensalidade mensalidade = null;
        Matricula matricula = null;
        Rematricula rematricula = null;
        boolean semestre = true;
        if (matriculaDto != null && rematriculaDto == null) {
            mensalidadesDto = new ArrayList<>();
            matricula = repositoryMatricula.findById(matriculaDto.getId());
            for(int i = 0; i < meses.size(); i++) {
                if(LocalDate.now().getMonth().name().equals("june".toUpperCase()) && semestre) {
                    i = 5;
                    semestre = false;
                }
                mensalidade = new Mensalidade();
                mensalidade.setMatricula(matricula);
                mensalidade.setRematricula(null);
                mensalidade.setMes(meses.get(i));
                mensalidade.setAno(LocalDate.now().getYear());
                mensalidade.setValor(matriculaDto.getValorMensalidade());
                mensalidade.setVencimento(matriculaDto.getVencimentoMensalidade());
                mensalidade.setStatus(repositoryStatus.findByNemotecnico("EM_DIA"));
                mensalidadesDto.add(montarDTOSucesso(repository.save(mensalidade), ValidacaoEnum.OK.getDescricao()));
            }
            return mensalidadesDto;
        } else if(matriculaDto == null && rematriculaDto != null) {
            mensalidadesDto = new ArrayList<>();
            mensalidade = new Mensalidade();
            matricula = repositoryMatricula.findById(matriculaDto.getId());
            rematricula = repositoryRematricula.findById(rematriculaDto.getId());
            for(int i = 0; i < meses.size(); i++) {
                if(LocalDate.now().getMonth().equals("june".toUpperCase()) && semestre) {
                    i = 5;
                    semestre = false;
                }
                mensalidade.setMatricula(matricula);
                mensalidade.setRematricula(rematricula);
                mensalidade.setMes(meses.get(i));
                mensalidade.setAno(LocalDate.now().getYear());
                mensalidade.setValor(rematriculaDto.getValorMensalidade());
                mensalidade.setVencimento(rematriculaDto.getVencimentoMensalidade());
                mensalidade.setStatus(repositoryStatus.findByNemotecnico("EM_DIA"));
                mensalidadesDto.add(montarDTOSucesso(repository.save(mensalidade), ValidacaoEnum.OK.getDescricao()));
            }
            return mensalidadesDto;
        } else {
            mensalidadesDto = new ArrayList<>();
            mensalidadesDto.add(montarDTOErro(ValidacaoEnum.ERRO.getDescricao()));
            return mensalidadesDto;
        }
    }

    public MensalidadeDTO editar(MensalidadeDTO mensalidadeDto) {
        Mensalidade mensalidadeEditar = repository.findById(mensalidadeDto.getId());
        if (mensalidadeEditar == null) {
            return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarMensalidade(mensalidadeDto) == ValidacaoEnum.OK.getDescricao()) {
                logger.info("Editando Mensalidade.");
                mensalidadeEditar.setValor(mensalidadeDto.getValor());
                mensalidadeEditar.setVencimento(mensalidadeDto.getVencimento());
                mensalidadeEditar.setStatus(repositoryStatus.findById(mensalidadeDto.getStatusDto().getId()));
                return montarDTOSucesso(repository.save(mensalidadeEditar), ValidacaoEnum.EDITAR.getDescricao());
            } else {
                return montarDTOErro(validarMensalidade(mensalidadeDto));
            }
        }
    }

    public MensalidadeDTO montarDTO(Mensalidade mensalidade) {
        MensalidadeDTO mensalidadeDto = new MensalidadeDTO();
        mensalidadeDto.setId(mensalidade.getId());
        mensalidadeDto.setMatriculaDto(serviceMatricula.montarDto(mensalidade.getMatricula()));
        mensalidadeDto.setRematriculaDto(null);
        mensalidadeDto.setValor(mensalidade.getValor());
        mensalidadeDto.setVencimento(mensalidade.getVencimento());
        mensalidadeDto.setMes(mensalidade.getMes());
        mensalidadeDto.setAno(mensalidade.getAno().toString());
        mensalidadeDto.setStatusDto(serviceStatus.montarDTO(mensalidade.getStatus()));
        return mensalidadeDto;
    }

    public MensalidadeDTO montarDTOSucesso(Mensalidade mensalidade, String validacao) {
        logger.info("Montando objeto de sucesso.");
        MensalidadeDTO mensalidadeDto = null;
        if (mensalidade != null) {
            mensalidadeDto = montarDTO(mensalidade);
            if (validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
                mensalidadeDto.setMsgSucesso("Mensalidade cadastrada com sucesso.");
            }
            if (validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
                mensalidadeDto.setMsgSucesso("Mensalidade editada com sucesso.");
            }
            if (validacao.equals(ValidacaoEnum.ZERO.getDescricao())) {
                mensalidadeDto.setMsgSucesso("O vencimento da mensalidade não pode ser 0 (Zero).");
            }
            if (validacao.equals(ValidacaoEnum.NULL.getDescricao())) {
                mensalidadeDto.setMsgSucesso("Não pode conter campos nulos.");
            }
        } else if (validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
            mensalidadeDto = new MensalidadeDTO();
            mensalidadeDto.setMsgSucesso("Mensalidade deletada com sucesso.");
        }
        return mensalidadeDto;
    }

    public MensalidadeDTO montarDTOErro(String validacao) {
        logger.info("Montando objeto de erro.");
        MensalidadeDTO mensalidadeDto = new MensalidadeDTO();
        if (validacao.equals(ValidacaoEnum.ERRO.getDescricao())) {
            mensalidadeDto.setMsgErro("Ocorreu um erro ao criar as Mensalidades.");
        }
        if (validacao.equals(ValidacaoEnum.NAO_EXISTE.getDescricao())) {
            mensalidadeDto.setMsgErro("Mensalidade selecionada não existe.");
        }
        return mensalidadeDto;
    }

    public String validarMensalidade(MensalidadeDTO mensalidadeDto) {
        logger.info("Validando campos da Mensalidade.");
        try {
            if (mensalidadeDto.getValor() == null
                    || mensalidadeDto.getVencimento() == null) {
                if (mensalidadeDto.getVencimento() == 0) {
                    return ValidacaoEnum.ZERO.getDescricao();
                } else {
                    return ValidacaoEnum.NULL.getDescricao();
                }
            } else {
                return ValidacaoEnum.OK.getDescricao();
            }
        } catch (NullPointerException erro) {
            return ValidacaoEnum.NULL.getDescricao();
        }
    }

}
