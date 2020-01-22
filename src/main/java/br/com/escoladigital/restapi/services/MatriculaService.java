package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.controllers.MatriculaController;
import br.com.escoladigital.restapi.dtos.MatriculaDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.models.Matricula;
import br.com.escoladigital.restapi.models.Mensalidade;
import br.com.escoladigital.restapi.repositories.AlunoRepository;
import br.com.escoladigital.restapi.repositories.MatriculaRepository;
import br.com.escoladigital.restapi.repositories.MensalidadeRepository;
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
public class MatriculaService {

    private static final Logger logger = LogManager.getLogger(MatriculaController.class);

    private MatriculaRepository repository;
    private AlunoRepository repositoryAluno;
    private StatusRepository repositoryStatus;
    private MensalidadeRepository repositoryMensalidade;

    private List<String> meses = null;

    @Autowired
    public MatriculaService(MatriculaRepository repository, AlunoRepository repositoryAluno, StatusRepository repositoryStatus, MensalidadeRepository repositoryMensalidade) {
        super();
        this.repository = repository;
        this.repositoryAluno = repositoryAluno;
        this.repositoryStatus = repositoryStatus;
        this.repositoryMensalidade = repositoryMensalidade;

        meses = new ArrayList<String>();
        this.meses.add("Fevereiro");
        this.meses.add("Março");
        this.meses.add("Abril");
        this.meses.add("Maio");
        this.meses.add("Junho");
        this.meses.add("Julho");
        this.meses.add("Agosto");
        this.meses.add("Setembro");
        this.meses.add("Outubro");
        this.meses.add("Novembro");
        this.meses.add("Dezembro");
    }

    public MatriculaDTO salvar(MatriculaDTO matriculaDto) {
        logger.info("Salvando Matricula do Aluno " + matriculaDto.getAluno().getNome() + ".");
        String validacao = validarMatricula(matriculaDto, true);
        if (validacao.equals(ValidacaoEnum.OK.getDescricao())) {
            return validarMatriculaExistente(matriculaDto);
        } else {
            return montarDTOErro(matriculaDto, validacao);
        }
    }

    public MatriculaDTO listarPorAluno(long id) {
        logger.info("Listando a Matrícula do Aluno pelo ID.");
        Aluno aluno = repositoryAluno.findById(id);
        Matricula matricula = repository.findByAluno(aluno);
        if (aluno == null || matricula == null) {
            return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            return montarDTOSucesso(matricula, false, null);
        }
    }

    public MatriculaDTO editar(long id, MatriculaDTO matriculaDto) {
        Matricula matriculaEditar = repository.findById(id);
        if (matriculaEditar == null) {
            return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarMatricula(matriculaDto, false) == ValidacaoEnum.OK.getDescricao()) {
                logger.info("Editando Matricula do Aluno " + matriculaDto.getAluno().getNome() + ".");
                matriculaEditar.setAluno(matriculaDto.getAluno());
                matriculaEditar.setValor(matriculaDto.getValor());
                matriculaEditar.setValorMensalidade(matriculaDto.getValorMensalidade());
                matriculaEditar.setVencimentoMensalidade(matriculaDto.getVencimentoMensalidade());
                matriculaEditar.setStatus(repositoryStatus.findById(matriculaDto.getStatus().getId()));
                return montarDTOSucesso(repository.save(matriculaEditar), true, ValidacaoEnum.EDITAR.getDescricao());
            } else {
                return montarDTOErro(null, validarMatricula(matriculaDto, false));
            }
        }
    }

    public MatriculaDTO deletar(long id) {
        Matricula matricula = repository.findById(id);
        if (matricula == null) {
            return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarDeletar(matricula)) {
                logger.info("Deletando Matrícula do aluno " + matricula.getAluno().getNome() + ".");
                repository.deleteById(id);
                return montarDTOSucesso(null, true, ValidacaoEnum.DELETAR.getDescricao());
            }
            return montarDTOErro(null, ValidacaoEnum.FK.getDescricao());
        }
    }

    public MatriculaDTO montarDTOSucesso(Matricula matricula, Boolean preencheMsgSucesso, String validacao) {
        logger.info("Montando objeto de sucesso.");
        MatriculaDTO dto = new MatriculaDTO();
        if (matricula != null) {
            dto.setId(matricula.getId());
            dto.setAluno(matricula.getAluno());
            dto.setDataMatricula(matricula.getDataMatricula().toString());
            dto.setValor(matricula.getValor());
            dto.setValorMensalidade(matricula.getValorMensalidade());
            dto.setVencimentoMensalidade(matricula.getVencimentoMensalidade());
            dto.setStatus(matricula.getStatus());
            if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
                dto.setMsgSucesso("Matrícula cadastrada com sucesso para o aluno " + matricula.getAluno().getNome() + ".");
            } else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
                dto.setMsgSucesso("Matrícula editada com sucesso para o aluno " + matricula.getAluno().getNome() + ".");
            }
        } else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
            dto.setMsgSucesso("Matrícula deletada com sucesso.");
        }
        return dto;
    }

    public MatriculaDTO montarDTOErro(MatriculaDTO matriculaDto, String validacao) {
        logger.info("Montando objeto de erro.");
        MatriculaDTO dto = new MatriculaDTO();
        if (validacao.equals(ValidacaoEnum.EXISTE.getDescricao())) {
            dto.setMsgErro("Já existe uma Matrícula para o aluno " + matriculaDto.getAluno().getNome() + ".");
        }
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

    public MatriculaDTO validarMatriculaExistente(MatriculaDTO matriculaDto) {
        logger.info("Validando se o Aluno " + matriculaDto.getAluno().getNome() + " já possui Matrícula.");
        Matricula matriculaValidada = repository.findByAluno(matriculaDto.getAluno());
        if (matriculaValidada != null) {
            return montarDTOErro(matriculaDto, ValidacaoEnum.EXISTE.getDescricao());
        } else {
            Matricula matricula = new Matricula();
            matricula.setAluno(repositoryAluno.findById(matriculaDto.getAluno().getId()));
            matricula.setDataMatricula(LocalDate.now());
            matricula.setValor(matriculaDto.getValor());
            matricula.setValorMensalidade(matriculaDto.getValorMensalidade());
            matricula.setVencimentoMensalidade(matriculaDto.getVencimentoMensalidade());
            matricula.setStatus(repositoryStatus.findById(matriculaDto.getStatus().getId()));
            matricula = repository.save(matricula);

            Mensalidade mensalidade = null;
            for(int cont = 0; cont < meses.size(); cont++) {
                if(LocalDate.now().getMonth().equals("JUNE")) {
                    cont = 5;
                }
                mensalidade = new Mensalidade();
                mensalidade.setMatricula(matricula);
                mensalidade.setMesAno(meses.get(cont) + " - " + LocalDate.now().getYear());
                mensalidade.setValor(matricula.getValorMensalidade());
                mensalidade.setVencimento(matricula.getVencimentoMensalidade());
                mensalidade.setStatus(repositoryStatus.findByNemotecnico("PENDENTE"));
                repositoryMensalidade.save(mensalidade);
            }
            return montarDTOSucesso(matricula, true, ValidacaoEnum.SALVAR.getDescricao());
        }
    }

    public String validarMatricula(MatriculaDTO matriculaDto, boolean salvar) {
        logger.info("Validando campos da Matrícula.");
        try {
            if (salvar) {
                if (matriculaDto.getAluno() == null || matriculaDto.getValor() == null
                        || matriculaDto.getStatus() == null) {
                    return ValidacaoEnum.BRANCO.getDescricao();
                } else {
                    return ValidacaoEnum.OK.getDescricao();
                }
            } else {
                if (matriculaDto.getAluno() == null || matriculaDto.getValor() == null
                        || matriculaDto.getStatus() == null) {
                    return ValidacaoEnum.BRANCO.getDescricao();
                } else {
                    return ValidacaoEnum.OK.getDescricao();
                }
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
