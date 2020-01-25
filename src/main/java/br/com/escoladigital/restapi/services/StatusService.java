package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.controllers.StatusController;
import br.com.escoladigital.restapi.dtos.StatusDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Aluno;
import br.com.escoladigital.restapi.models.Status;
import br.com.escoladigital.restapi.repositories.AlunoRepository;
import br.com.escoladigital.restapi.repositories.StatusRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public StatusService() {
        super();
    }

    public StatusDTO salvar(StatusDTO statusDto) {
		logger.info("Salvando Status " + statusDto.getDescricao() + ".");
		if (validarStatus(statusDto).equals(ValidacaoEnum.OK.getDescricao())) {
			statusDto.setNemotecnico(statusDto.getNemotecnico().toUpperCase());
			statusDto.setAtivo(ValidacaoEnum.SIM.getDescricao());
			return validarStatusExistente(statusDto);
		} else {
			return montarDTOErro(validarStatus(statusDto));
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
			return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			return montarDTOSucesso(status, false, null);
		}
	}

	public StatusDTO editar(long id, StatusDTO statusDto) {
		Status statusEditar = repository.findById(id);
		if (statusEditar == null) {
			return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarStatus(statusDto) == ValidacaoEnum.OK.getDescricao()) {
				logger.info("Editando Status ." + statusDto.getDescricao() + ".");
				statusEditar.setDescricao(statusDto.getDescricao());
				statusEditar.setNemotecnico(statusDto.getNemotecnico().toUpperCase());
				statusEditar.setAtivo(statusDto.getAtivo());
				return montarDTOSucesso(repository.save(statusEditar), true, ValidacaoEnum.EDITAR.getDescricao());
			} else {
				return montarDTOErro(validarStatus(statusDto));
			}
		}
	}

	public StatusDTO deletar(long id) {
		Status status = repository.findById(id);
		if (status == null) {
			return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarDeletar(status)) {
				logger.info("Deletando Status " + status.getDescricao() + ".");
				repository.deleteById(id);
				return montarDTOSucesso(null, true, ValidacaoEnum.DELETAR.getDescricao());
			}
			return montarDTOErro(ValidacaoEnum.FK.getDescricao());
		}
	}

	public StatusDTO montarDTO(Status status) {
		StatusDTO statusDto = new StatusDTO();
		statusDto.setId(status.getId());
		statusDto.setDescricao(status.getDescricao());
		statusDto.setNemotecnico(status.getNemotecnico());
		statusDto.setAtivo(status.getAtivo());
		return statusDto;
	}

	public Status montarStatus(StatusDTO statusDto) {
		return repository.findById(statusDto.getId());
	}

	public StatusDTO montarDTOSucesso(Status status, Boolean preencheMsgSucesso, String validacao) {
		logger.info("Montando objeto de sucesso.");
		StatusDTO statusDto = null;
		if (status != null) {
			statusDto = montarDTO(status);
			if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
				statusDto.setMsgSucesso("Status " + status.getDescricao() + " cadastrado com sucesso.");
			} else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
				statusDto.setMsgSucesso("Status " + status.getDescricao() + " editado com sucesso.");
			}
		} else if (preencheMsgSucesso && validacao.equals(ValidacaoEnum.DELETAR.getDescricao())) {
			statusDto = new StatusDTO();
			statusDto.setMsgSucesso("Status deletado com sucesso.");
		}
		return statusDto;
	}

	public StatusDTO montarDTOErro(String validacao) {
		logger.info("Montando objeto de erro.");
		StatusDTO dto = new StatusDTO();
		if (validacao.equals(ValidacaoEnum.EXISTE.getDescricao())) {
			dto.setMsgErro("Já existe um Status com esse nemotécnico.");
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

	public StatusDTO validarStatusExistente(StatusDTO statusDto) {
		logger.info("Validando se o Status " + statusDto.getDescricao() + " já existe.");
		Status statusValidado = repository.findByNemotecnico(statusDto.getNemotecnico());
		if (statusValidado != null) {
			return montarDTOErro(ValidacaoEnum.EXISTE.getDescricao());
		} else {
			statusValidado = new Status();
			statusValidado.setDescricao(statusDto.getDescricao());
			statusValidado.setNemotecnico(statusDto.getNemotecnico());
			statusValidado.setAtivo(statusDto.getAtivo());
			return montarDTOSucesso(repository.save(statusValidado), true, ValidacaoEnum.SALVAR.getDescricao());
		}
	}

	public String validarStatus(StatusDTO statusDto) {
		logger.info("Validando campos do Status.");
		try {
			if (statusDto.getDescricao().trim().isEmpty() || statusDto.getNemotecnico().trim().isEmpty()) {
				return ValidacaoEnum.BRANCO.getDescricao();
			} else {
				return ValidacaoEnum.OK.getDescricao();
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
