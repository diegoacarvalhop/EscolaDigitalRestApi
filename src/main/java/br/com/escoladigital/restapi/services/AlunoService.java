package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.controllers.AlunoController;
import br.com.escoladigital.restapi.dtos.AlunoDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.repositories.AlunoRepository;
import br.com.escoladigital.restapi.repositories.StatusRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlunoService {

    private static final Logger logger = LogManager.getLogger(AlunoController.class);

    private AlunoRepository repository;
    private StatusRepository repositoryStatus;

    @Autowired
    private MatriculaService serviceMatricula;

    @Autowired
    private ContatoService serviceContato;

    @Autowired
    private EnderecoService serviceEndereco;

    @Autowired
    private StatusService serviceStatus;

    @Autowired
    public AlunoService(AlunoRepository repository, StatusRepository repositoryStatus) {
        super();
        this.repository = repository;
        this.repositoryStatus = repositoryStatus;
    }

    public AlunoDTO salvar(AlunoDTO alunoDto) {
        logger.info("Salvando Aluno.");
        if (validarAluno(alunoDto).equals(ValidacaoEnum.OK.getDescricao())) {
            return validarAlunoExistente(alunoDto);
        } else {
            return montarDTOErro(validarAluno(alunoDto));
        }
    }

    public List<AlunoDTO> listarTodos() {
        logger.info("Listando todos os Alunos.");
        List<Aluno> todosAlunos = repository.findAll();
        List<AlunoDTO> alunosDto = new ArrayList<>();
        for (Aluno aluno : todosAlunos) {
            alunosDto.add(montarDTOSucesso(aluno, null));
        }
        return alunosDto;
    }

    public AlunoDTO listarPorId(long id) {
        logger.info("Listando Aluno pelo ID.");
        Aluno aluno = repository.findById(id);
        if (aluno == null) {
            return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            return montarDTOSucesso(aluno, null);
        }
    }

    public AlunoDTO editar(long id, AlunoDTO alunoDto) {
        Aluno alunoEditar = repository.findById(id);
        if (alunoEditar == null) {
            return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarAluno(alunoDto) == ValidacaoEnum.OK.getDescricao()) {
                logger.info("Editando Aluno.");
                alunoEditar.setNome(alunoDto.getNome());
                alunoEditar.setDataNascimento(
                        LocalDate.parse(alunoDto.getDataNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                alunoEditar.setNomeMae(alunoDto.getNomeMae());
                alunoEditar.setNomePai(alunoDto.getNomePai());
                alunoEditar.setEndereco(serviceEndereco.montarEndereco(serviceEndereco.salvar(alunoDto.getEnderecoDto())));
                alunoEditar.setContato(serviceContato.montarContato(serviceContato.salvar(alunoDto.getContatoDto())));
                alunoEditar.setStatus(repositoryStatus.findById(alunoDto.getStatusDto().getId()));
                return montarDTOSucesso(repository.save(alunoEditar), ValidacaoEnum.EDITAR.getDescricao());
            } else {
                return montarDTOErro(validarAluno(alunoDto));
            }
        }
    }

    public AlunoDTO deletar(long id) {
        Aluno aluno = repository.findById(id);
        if (aluno == null) {
            return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
        } else {
            if (validarDeletar(aluno)) {
                logger.info("Deletando Aluno.");
                repository.deleteById(id);
                return montarDTOSucesso(null, ValidacaoEnum.DELETAR.getDescricao());
            }
            return montarDTOErro(ValidacaoEnum.FK.getDescricao());
        }
    }

    public AlunoDTO montarDTO(Aluno aluno) {
        String dia = String.valueOf(aluno.getDataNascimento().getDayOfMonth()).length() == 1 ? "0" + String.valueOf(aluno.getDataNascimento().getDayOfMonth()) : String.valueOf(aluno.getDataNascimento().getDayOfMonth());
        String mes = String.valueOf(aluno.getDataNascimento().getMonthValue()).length() == 1 ? "0" + String.valueOf(aluno.getDataNascimento().getMonthValue()) : String.valueOf(aluno.getDataNascimento().getMonthValue());
        String ano = String.valueOf(aluno.getDataNascimento().getYear());
        String aniversario = Integer.parseInt(dia) == LocalDate.now().getDayOfMonth() && Integer.parseInt(mes) == LocalDate.now().getMonthValue() ? " (Aniversário)" : "";
        AlunoDTO alunoDto = new AlunoDTO();
        alunoDto.setId(aluno.getId());
        alunoDto.setNome(aluno.getNome());
        alunoDto.setNomeMae(aluno.getNomeMae());
        alunoDto.setNomePai(aluno.getNomePai());
        alunoDto.setContatoDto(serviceContato.montarDto(aluno.getContato()));
        alunoDto.setDataNascimento(dia + "/" + mes + "/" + ano + aniversario);
        alunoDto.setEnderecoDto(serviceEndereco.montarDTO(aluno.getEndereco()));
        alunoDto.setMatriculaDto(serviceMatricula.montarDto(aluno.getMatricula()));
        alunoDto.setStatusDto(serviceStatus.montarDTO(aluno.getStatus()));
        return alunoDto;
    }

    public AlunoDTO montarDTOSucesso(Aluno aluno, String validacao) {
        logger.info("Montando objeto de sucesso.");
        AlunoDTO alunoDto = montarDTO(aluno);;
        if(validacao != null) {
            if (alunoDto != null) {
                if (validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
                    alunoDto.setMsgSucesso("Aluno " + aluno.getNome() + " cadastrado com sucesso.");
                } else if (validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
                    alunoDto.setMsgSucesso("Aluno " + aluno.getNome() + " editado com sucesso.");
                }
            } else if (validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
                alunoDto.setMsgSucesso("Aluno deletado com sucesso.");
            }
        }
        return alunoDto;
    }

    public AlunoDTO montarDTOErro(String validacao) {
        logger.info("Montando objeto de erro.");
        AlunoDTO alunoDto = new AlunoDTO();
        if (validacao.equals(ValidacaoEnum.EXISTE.getDescricao())) {
            alunoDto.setMsgErro("Já existe um Aluno com esse nome e com a mesma mãe.");
        }
        if (validacao.equals(ValidacaoEnum.BRANCO.getDescricao())) {
            alunoDto.setMsgErro("Não pode existir campos em branco.");
        }
        if (validacao.equals(ValidacaoEnum.NULL.getDescricao())) {
            alunoDto.setMsgErro("Não pode conter valores nulos.");
        }
        if (validacao.equals(ValidacaoEnum.NAO_EXISTE.getDescricao())) {
            alunoDto.setMsgErro("O Aluno não existe.");
        }
        if (validacao.equals(ValidacaoEnum.FK.getDescricao())) {
            alunoDto.setMsgErro("O Aluno não pode ser deletado por estar associado a outro retistro.");
        }
        return alunoDto;
    }

    public AlunoDTO validarAlunoExistente(AlunoDTO alunoDto) {
        logger.info("Validando o Aluno.");
        Aluno alunoValidado = repository.findByNome(alunoDto.getNome());
        if (alunoValidado != null && alunoValidado.getNomeMae().equals(alunoDto.getNomeMae())) {
            return montarDTOErro(ValidacaoEnum.EXISTE.getDescricao());
        } else {
            alunoValidado = new Aluno();
            alunoValidado.setNome(alunoDto.getNome());
            alunoValidado.setDataNascimento(
                    LocalDate.parse(alunoDto.getDataNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            alunoValidado.setNomeMae(alunoDto.getNomeMae());
            alunoValidado.setNomePai(alunoDto.getNomePai());
            alunoValidado.setMatricula(serviceMatricula.montarMatricula(serviceMatricula.salvar(alunoDto.getMatriculaDto())));
            alunoValidado.setEndereco(serviceEndereco.montarEndereco(serviceEndereco.salvar(alunoDto.getEnderecoDto())));
            alunoValidado.setContato(serviceContato.montarContato(serviceContato.salvar(alunoDto.getContatoDto())));
            alunoValidado.setStatus(repositoryStatus.findById(alunoDto.getStatusDto().getId()));
            return montarDTOSucesso(repository.save(alunoValidado), ValidacaoEnum.SALVAR.getDescricao());
        }
    }

    public String validarAluno(AlunoDTO alunoDto) {
        logger.info("Validando campos do Aluno.");
        try {
            if (alunoDto.getNome().isEmpty()
                    || alunoDto.getNomeMae().isEmpty()
                    || alunoDto.getNomePai().isEmpty()
                    || alunoDto.getDataNascimento().isEmpty()) {
                return ValidacaoEnum.BRANCO.getDescricao();
            } else {
                return ValidacaoEnum.OK.getDescricao();
            }
        } catch (NullPointerException erro) {
            return ValidacaoEnum.NULL.getDescricao();
        }
    }

    public boolean validarDeletar(Aluno aluno) {
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
