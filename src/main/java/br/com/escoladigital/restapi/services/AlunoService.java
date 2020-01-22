package br.com.escoladigital.restapi.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.escoladigital.restapi.controllers.AlunoController;
import br.com.escoladigital.restapi.dtos.AlunoDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.repositories.AlunoRepository;
import br.com.escoladigital.restapi.repositories.ContatoRepository;
import br.com.escoladigital.restapi.repositories.EnderecoRepository;
import br.com.escoladigital.restapi.repositories.StatusRepository;

@Service
public class AlunoService {

	private static final Logger logger = LogManager.getLogger(AlunoController.class);

	private AlunoRepository repository;
	private EnderecoRepository repositoryEndereco;
	private ContatoRepository repositoryContato;
	private StatusRepository repositoryStatus;

	@Autowired
	public AlunoService(AlunoRepository repository, EnderecoRepository repositoryEndereco,
			ContatoRepository repositoryContato, StatusRepository repositoryStatus) {
		super();
		this.repository = repository;
		this.repositoryEndereco = repositoryEndereco;
		this.repositoryContato = repositoryContato;
		this.repositoryStatus = repositoryStatus;
	}

	public AlunoDTO salvar(AlunoDTO alunoDto) {
		logger.info("Salvando Aluno " + alunoDto.getNome() + ".");
		String validacao = validarAluno(alunoDto, true);
		if (validacao.equals(ValidacaoEnum.OK.getDescricao())) {
			return validarAlunoExistente(alunoDto);
		} else {
			return montarDTOErro(alunoDto, validacao);
		}
	}

	public List<AlunoDTO> listarTodos() {
		logger.info("Listando todos os Alunos.");
		List<Aluno> todosAlunos = repository.findAll();
		List<AlunoDTO> dtos = new ArrayList<AlunoDTO>();
		for (Aluno aluno : todosAlunos) {
			dtos.add(montarDTOSucesso(aluno, false, null));
		}
		return dtos;
	}

	public AlunoDTO listarPorId(long id) {
		logger.info("Listando Aluno pelo ID.");
		Aluno aluno = repository.findById(id);
		if (aluno == null) {
			return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			return montarDTOSucesso(aluno, false, null);
		}
	}

	public AlunoDTO editar(long id, AlunoDTO alunoDto) {
		Aluno alunoEditar = repository.findById(id);
		if (alunoEditar == null) {
			return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarAluno(alunoDto, false) == ValidacaoEnum.OK.getDescricao()) {
				logger.info("Editando Aluno " + alunoDto.getNome() + ".");
				alunoEditar.setNome(alunoDto.getNome());
				alunoEditar.setDataNascimento(
						LocalDate.parse(alunoDto.getDataNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				alunoEditar.setNomeMae(alunoDto.getNomeMae());
				alunoEditar.setNomePai(alunoDto.getNomePai());
				alunoEditar.setEndereco(repositoryEndereco.save(alunoDto.getEndereco()));
				alunoEditar.setContato(repositoryContato.save(alunoDto.getContato()));
				alunoEditar.setStatus(repositoryStatus.findById(alunoDto.getStatus().getId()));
				return montarDTOSucesso(repository.save(alunoEditar), true, ValidacaoEnum.EDITAR.getDescricao());
			} else {
				return montarDTOErro(null, validarAluno(alunoDto, false));
			}
		}
	}

	public AlunoDTO deletar(long id) {
		Aluno aluno = repository.findById(id);
		if (aluno == null) {
			return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarDeletar(aluno)) {
				logger.info("Deletando Aluno " + aluno.getNome() + ".");
				repository.deleteById(id);
				return montarDTOSucesso(null, true, ValidacaoEnum.DELETAR.getDescricao());
			}
			return montarDTOErro(null, ValidacaoEnum.FK.getDescricao());
		}
	}

	public AlunoDTO montarDTOSucesso(Aluno aluno, Boolean preencheMsgSucesso, String validacao) {
		logger.info("Montando objeto de sucesso.");
		AlunoDTO dto = new AlunoDTO();
		if (aluno != null) {
			dto.setId(aluno.getId());
			dto.setNome(aluno.getNome());
			dto.setDataNascimento(aluno.getDataNascimento().toString());
			dto.setNomeMae(aluno.getNomeMae());
			dto.setNomePai(aluno.getNomePai());
			dto.setEndereco(aluno.getEndereco());
			dto.setContato(aluno.getContato());
			dto.setStatus(aluno.getStatus());
			if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
				dto.setMsgSucesso("Aluno " + aluno.getNome() + " cadastrado com sucesso.");
			} else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
				dto.setMsgSucesso("Aluno " + aluno.getNome() + " editado com sucesso.");
			}
		} else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
			dto.setMsgSucesso("Aluno deletado com sucesso.");
		}
		return dto;
	}

	public AlunoDTO montarDTOErro(AlunoDTO alunoDto, String validacao) {
		logger.info("Montando objeto de erro.");
		AlunoDTO dto = new AlunoDTO();
		if (validacao.equals(ValidacaoEnum.EXISTE.getDescricao())) {
			dto.setMsgErro("Já existe um Aluno com o nome " + alunoDto.getNome() + ".");
		}
		if (validacao.equals(ValidacaoEnum.BRANCO.getDescricao())) {
			dto.setMsgErro("Não pode existir campos em branco.");
		}
		if (validacao.equals(ValidacaoEnum.NULL.getDescricao())) {
			dto.setMsgErro("Não pode conter valores nulos.");
		}
		if (validacao.equals(ValidacaoEnum.NAO_EXISTE.getDescricao())) {
			dto.setMsgErro("O Aluno não existe.");
		}
		if (validacao.equals(ValidacaoEnum.FK.getDescricao())) {
			dto.setMsgErro("O Aluno não pode ser deletado por estar associado a outro retistro.");
		}
		return dto;
	}

	public AlunoDTO validarAlunoExistente(AlunoDTO alunoDto) {
		logger.info("Validando se o Aluno " + alunoDto.getNome() + " já existe.");
		Aluno alunoValidado = repository.findByNome(alunoDto.getNome());
		if (alunoValidado != null) {
			return montarDTOErro(alunoDto, ValidacaoEnum.EXISTE.getDescricao());
		} else {
 			Aluno aluno = new Aluno();
			aluno.setNome(alunoDto.getNome());
			aluno.setDataNascimento(
					LocalDate.parse(alunoDto.getDataNascimento(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			aluno.setNomeMae(alunoDto.getNomeMae());
			aluno.setNomePai(alunoDto.getNomePai());
			aluno.setEndereco(repositoryEndereco.save(alunoDto.getEndereco()));
			aluno.setContato(repositoryContato.save(alunoDto.getContato()));
			aluno.setStatus(repositoryStatus.findById(alunoDto.getStatus().getId()));
			return montarDTOSucesso(repository.save(aluno), true, ValidacaoEnum.SALVAR.getDescricao());
		}
	}

	public String validarAluno(AlunoDTO alunoDto, boolean salvar) {
		logger.info("Validando campos do Aluno.");
		try {
			if (salvar) {
				if (alunoDto.getNome().trim().isEmpty() || alunoDto.getNomeMae().trim().isEmpty()
						|| alunoDto.getNomePai().trim().isEmpty()
						|| alunoDto.getEndereco().getLogradouro().trim().isEmpty()
						|| alunoDto.getEndereco().getBairro().trim().isEmpty()
						|| alunoDto.getEndereco().getCidade().trim().isEmpty()
						|| alunoDto.getEndereco().getCep().trim().isEmpty()
						|| alunoDto.getEndereco().getEstado().trim().isEmpty() || alunoDto.getStatus() == null
						|| alunoDto.getDataNascimento().trim().isEmpty()) {
					return ValidacaoEnum.BRANCO.getDescricao();
				} else {
					return ValidacaoEnum.OK.getDescricao();
				}
			} else {
				if (alunoDto.getNome().trim().isEmpty() || alunoDto.getNomeMae().trim().isEmpty()
						|| alunoDto.getNomePai().trim().isEmpty()
						|| alunoDto.getEndereco().getLogradouro().trim().isEmpty()
						|| alunoDto.getEndereco().getBairro().trim().isEmpty()
						|| alunoDto.getEndereco().getCidade().trim().isEmpty()
						|| alunoDto.getEndereco().getCep().trim().isEmpty()
						|| alunoDto.getEndereco().getEstado().trim().isEmpty() || alunoDto.getStatus() == null
						|| alunoDto.getDataNascimento().trim().isEmpty()) {
					return ValidacaoEnum.BRANCO.getDescricao();
				} else {
					return ValidacaoEnum.OK.getDescricao();
				}
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
