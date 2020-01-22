package br.com.escoladigital.restapi.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.escoladigital.restapi.controllers.StatusController;
import br.com.escoladigital.restapi.dtos.StatusDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.models.Status;
import br.com.escoladigital.restapi.repositories.AlunoRepository;
import br.com.escoladigital.restapi.repositories.StatusRepository;

@Service
public class StatusService {

	private static final Logger logger = LogManager.getLogger(StatusController.class);

	private StatusRepository repository;
	private AlunoRepository repositoryAluno;

	@Autowired
	public StatusService(StatusRepository repository, AlunoRepository repositoryAluno) {
		super();
		this.repository = repository;
		this.repositoryAluno = repositoryAluno;
	}

	public StatusDTO salvar(Status status) {
		logger.info("Salvando Status " + status.getDescricao() + ".");
		String validacao = validarStatus(status, true);
		if (validacao.equals(ValidacaoEnum.OK.getDescricao())) {
			status.setNemotecnico(status.getNemotecnico().toUpperCase());
			status.setAtivo(ValidacaoEnum.SIM.getDescricao());
			return validarStatusExistente(status);
		} else {
			return montarDTOErro(status, validacao);
		}
	}

	public List<StatusDTO> listarTodos() {
		logger.info("Listando todos os Status.");
		List<Status> todosStatus = repository.findAll();
		List<StatusDTO> dtos = new ArrayList<StatusDTO>();
		for (Status status : todosStatus) {
			dtos.add(montarDTOSucesso(status, false, null));
		}
		return dtos;
	}

	public StatusDTO listarPorId(long id) {
		logger.info("Listando Status pelo ID.");
		Status status = repository.findById(id);
		if (status == null) {
			return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			return montarDTOSucesso(status, false, null);
		}
	}

	public StatusDTO editar(long id, Status status) {
		Status statusEditar = repository.findById(id);
		if (statusEditar == null) {
			return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarStatus(status, false) == ValidacaoEnum.OK.getDescricao()) {
				logger.info("Editando Status ." + status.getDescricao() + ".");
				statusEditar.setDescricao(status.getDescricao());
				statusEditar.setNemotecnico(status.getNemotecnico().toUpperCase());
				statusEditar.setAtivo(status.getAtivo());
				return montarDTOSucesso(repository.save(statusEditar), true, ValidacaoEnum.EDITAR.getDescricao());
			} else {
				return montarDTOErro(null, validarStatus(status, false));
			}
		}
	}

	public StatusDTO deletar(long id) {
		Status status = repository.findById(id);
		if (status == null) {
			return montarDTOErro(null, ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarDeletar(status)) {
				logger.info("Deletando Status " + status.getDescricao() + ".");
				repository.deleteById(id);
				return montarDTOSucesso(null, true, ValidacaoEnum.DELETAR.getDescricao());
			}
			return montarDTOErro(null, ValidacaoEnum.FK.getDescricao());
		}
	}

	public StatusDTO montarDTOSucesso(Status status, Boolean preencheMsgSucesso, String validacao) {
		logger.info("Montando objeto de sucesso.");
		StatusDTO dto = new StatusDTO();
		if (status != null) {
			dto.setId(status.getId());
			dto.setDescricao(status.getDescricao());
			dto.setNemotecnico(status.getNemotecnico());
			dto.setAtivo(status.getAtivo());
			if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
				dto.setMsgSucesso("Status " + status.getDescricao() + " cadastrado com sucesso.");
			} else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
				dto.setMsgSucesso("Status " + status.getDescricao() + " editado com sucesso.");
			}
		} else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
			dto.setMsgSucesso("Status deletado com sucesso.");
		}
		return dto;
	}

	public StatusDTO montarDTOErro(Status status, String validacao) {
		logger.info("Montando objeto de erro.");
		StatusDTO dto = new StatusDTO();
		if (validacao.equals(ValidacaoEnum.EXISTE.getDescricao())) {
			dto.setMsgErro("Já existe um Status com o nemotécnico " + status.getNemotecnico() + ".");
		}
		if (validacao.equals(ValidacaoEnum.BRANCO.getDescricao())) {
			dto.setMsgErro("Não pode existir campos em branco.");
		}
		if (validacao.equals(ValidacaoEnum.NULL.getDescricao())) {
			dto.setMsgErro("Não pode conter valores nulos.");
		}
		if (validacao.equals(ValidacaoEnum.NAO_EXISTE.getDescricao())) {
			dto.setMsgErro("O Status não existe.");
		}
		if (validacao.equals(ValidacaoEnum.FK.getDescricao())) {
			dto.setMsgErro("O Status não pode ser deletado por estar associado a outro retistro.");
		}
		return dto;
	}

	public StatusDTO validarStatusExistente(Status status) {
		logger.info("Validando se o Status " + status.getDescricao() + " já existe.");
		Status statusValidado = repository.findByNemotecnico(status.getNemotecnico());
		if (statusValidado != null) {
			return montarDTOErro(status, ValidacaoEnum.EXISTE.getDescricao());
		} else {
			return montarDTOSucesso(repository.save(status), true, ValidacaoEnum.SALVAR.getDescricao());
		}
	}

	public String validarStatus(Status status, boolean salvar) {
		logger.info("Validando campos do Status.");
		try {
			if (salvar) {
				if (status.getDescricao().trim().isEmpty() || status.getNemotecnico().trim().isEmpty()) {
					return ValidacaoEnum.BRANCO.getDescricao();
				} else {
					return ValidacaoEnum.OK.getDescricao();
				}
			} else {
				if (status.getDescricao().trim().isEmpty() || status.getNemotecnico().trim().isEmpty()
						|| status.getAtivo().isEmpty()) {
					return ValidacaoEnum.BRANCO.getDescricao();
				} else {
					return ValidacaoEnum.OK.getDescricao();
				}
			}
		} catch (NullPointerException erro) {
			return ValidacaoEnum.NULL.getDescricao();
		}
	}

	public boolean validarDeletar(Status status) {
		logger.info("Validando se o Status " + status.getDescricao() + " está sendo usado.");
		List<Aluno> alunos = repositoryAluno.findByStatus(status);
		if (alunos.size() != 0) {
			return false;
		} else {
			return true;
		}
	}

}
