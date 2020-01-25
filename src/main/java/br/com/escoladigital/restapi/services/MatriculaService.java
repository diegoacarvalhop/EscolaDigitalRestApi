package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.controllers.MatriculaController;
import br.com.escoladigital.restapi.dtos.MatriculaDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Matricula;
import br.com.escoladigital.restapi.repositories.MatriculaRepository;
import br.com.escoladigital.restapi.repositories.StatusRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MatriculaService {

    private static final Logger logger = LogManager.getLogger(MatriculaController.class);

    private MatriculaRepository repository;
    private StatusRepository repositoryStatus;

    @Autowired
    private StatusService serviceStatus;

    @Autowired
    private MensalidadeService serviceMensalidade;

    @Autowired
    public MatriculaService(MatriculaRepository repository, StatusRepository repositoryStatus) {
        super();
        this.repository = repository;
        this.repositoryStatus = repositoryStatus;
    }

    public MatriculaService() {
        super();
    }

    public MatriculaDTO salvar(MatriculaDTO matriculaDto) {
        logger.info("Salvando Matricula.");
        if (validarMatricula(matriculaDto).equals(ValidacaoEnum.OK.getDescricao())) {
            Matricula matricula = new Matricula();
            matricula.setDataMatricula(LocalDate.now());
            matricula.setValor(matriculaDto.getValor());
            matricula.setStatus(repositoryStatus.findByNemotecnico("ativo".toUpperCase()));
            return montarDTOSucesso(repository.save(matricula), matriculaDto, ValidacaoEnum.SALVAR.getDescricao());
        } else {
            return montarDTOErro(validarMatricula(matriculaDto));
        }
    }

    public MatriculaDTO editar(long id, MatriculaDTO matriculaDto) {
        Matricula matriculaEditar = repository.findById(id);
        if (matriculaEditar == null) {
            return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarMatricula(matriculaDto) == ValidacaoEnum.OK.getDescricao()) {
                logger.info("Editando Matricula.");
                matriculaEditar.setValor(matriculaDto.getValor());
                matriculaEditar.setStatus(repositoryStatus.findById(matriculaDto.getStatusDto().getId()));
                return montarDTOSucesso(repository.save(matriculaEditar), null, ValidacaoEnum.EDITAR.getDescricao());
            } else {
                return montarDTOErro(validarMatricula(matriculaDto));
            }
        }
    }

    public MatriculaDTO deletar(long id) {
        Matricula matricula = repository.findById(id);
        if (matricula == null) {
            return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarDeletar(matricula)) {
                logger.info("Deletando Matrícula");
                repository.deleteById(id);
                return montarDTOSucesso(null, null, ValidacaoEnum.DELETAR.getDescricao());
            }
            return montarDTOErro(ValidacaoEnum.FK.getDescricao());
        }
    }

    public MatriculaDTO montarDto(Matricula matricula) {
        MatriculaDTO matriculaDto = new MatriculaDTO();
        if(matricula.getDataMatricula() != null){
            matriculaDto.setId(matricula.getId());
            matriculaDto.setDataMatricula(matricula.getDataMatricula().getDayOfMonth() + "/" + matricula.getDataMatricula().getMonthValue() + "/" + matricula.getDataMatricula().getYear());
            matriculaDto.setStatusDto(serviceStatus.montarDTO(matricula.getStatus()));
        }
        matriculaDto.setValor(matricula.getValor());
        return matriculaDto;
    }

    public Matricula montarMatricula(MatriculaDTO matriculaDto) {
        return repository.findById(matriculaDto.getId());
    }

    public MatriculaDTO montarDTOSucesso(Matricula matricula, MatriculaDTO matriculaDto, String validacao) {
        logger.info("Montando objeto de sucesso.");
        if (matricula != null) {
            if(matriculaDto != null) {
                matriculaDto.setId(matricula.getId());
                serviceMensalidade.salvar(matriculaDto, null);
            }
            if (validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
                matriculaDto.setMsgSucesso("Matrícula cadastrada com sucesso.");
            } else if (validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
                matriculaDto.setMsgSucesso("Matrícula editada com sucesso.");
            }
        } else if (validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
            matriculaDto = new MatriculaDTO();
            matriculaDto.setMsgSucesso("Matrícula deletada com sucesso.");
        }
        return matriculaDto;
    }

    public MatriculaDTO montarDTOErro(String validacao) {
        logger.info("Montando objeto de erro.");
        MatriculaDTO dto = new MatriculaDTO();
        if (validacao.equals(ValidacaoEnum.BRANCO.getDescricao())) {
            dto.setMsgErro("Não pode existir campos em branco.");
        }
        if (validacao.equals(ValidacaoEnum.NULL.getDescricao())) {
            dto.setMsgErro("Não pode conter valores nulos.");
        }
        if (validacao.equals(ValidacaoEnum.NAO_EXISTE.getDescricao())) {
            dto.setMsgErro("A Matrícula não existe.");
        }
        if (validacao.equals(ValidacaoEnum.FK.getDescricao())) {
            dto.setMsgErro("A Matrícula não pode ser deletada por estar associada a outro retistro.");
        }
        return dto;
    }

    public String validarMatricula(MatriculaDTO matriculaDto) {
        logger.info("Validando campos da Matrícula.");
        try {
            if (matriculaDto.getValor() == null
                    || matriculaDto.getValorMensalidade() == null
                    || matriculaDto.getVencimentoMensalidade() == null) {
                return ValidacaoEnum.NULL.getDescricao();
            } else {
                return ValidacaoEnum.OK.getDescricao();
            }
        } catch (NullPointerException erro) {
            return ValidacaoEnum.NULL.getDescricao();
        }
    }

    public boolean validarDeletar(Matricula matricula) {
//		logger.info("Validando se o Status " + status.getDescricao() + " está sendo usado.");
//		List<Publisher> publishers = repositoryPublisher.findByStatus(status);
//		List<Author> authors = repositoryAuthor.findByStatus(status);
//		if (publishers.size() != 0 || authors.size() != 0) {
//			return false;
//		} else {
//			return true;
//		}
        return true;
    }

}
