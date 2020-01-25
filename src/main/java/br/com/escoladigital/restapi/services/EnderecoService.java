package br.com.escoladigital.restapi.services;

import br.com.escoladigital.restapi.dtos.EnderecoDTO;
import br.com.escoladigital.restapi.enuns.ValidacaoEnum;
import br.com.escoladigital.restapi.models.Endereco;
import br.com.escoladigital.restapi.repositories.EnderecoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

	private static final Logger logger = LogManager.getLogger(EnderecoService.class);

	private EnderecoRepository repository;

	@Autowired
	public EnderecoService(EnderecoRepository repository) {
		super();
		this.repository = repository;
	}

	public EnderecoDTO salvar(EnderecoDTO enderecoDto) {
		logger.info("Salvando Endereco.");
		if (validarEndereco(enderecoDto).equals(ValidacaoEnum.OK.getDescricao())) {
			Endereco endereco = new Endereco();
			endereco.setLogradouro(enderecoDto.getLogradouro());
			endereco.setNumero(enderecoDto.getNumero());
			endereco.setComplemento(enderecoDto.getComplemento());
			endereco.setBairro(enderecoDto.getBairro());
			endereco.setCidade(enderecoDto.getCidade());
			endereco.setCep(enderecoDto.getCep());
			endereco.setEstado(enderecoDto.getEstado());
			return montarDTOSucesso(repository.save(endereco), ValidacaoEnum.SALVAR.getDescricao());
		} else {
			return montarDTOErro(validarEndereco(enderecoDto));
		}
	}

	public EnderecoDTO listarPorId(long id) {
		logger.info("Listando Endereço pelo ID.");
		Endereco endereco = repository.findById(id);
		if (endereco == null) {
			return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			return montarDTOSucesso(endereco, null);
		}
	}

	public EnderecoDTO editar(long id, EnderecoDTO enderecoDto) {
		Endereco enderecoEditar = repository.findById(id);
		if (enderecoEditar == null) {
			return montarDTOErro(ValidacaoEnum.NAO_EXISTE.getDescricao());
		} else {
			if (validarEndereco(enderecoDto).equals(ValidacaoEnum.OK.getDescricao())) {
				logger.info("Editando Endereco.");
				enderecoEditar.setLogradouro(enderecoDto.getLogradouro());
				enderecoEditar.setNumero(enderecoDto.getNumero());
				enderecoEditar.setBairro(enderecoDto.getBairro());
				enderecoEditar.setCidade(enderecoDto.getCidade());
				enderecoEditar.setComplemento(enderecoDto.getComplemento());
				enderecoEditar.setCep(enderecoDto.getCep());
				enderecoEditar.setEstado(enderecoDto.getEstado());
				return montarDTOSucesso(repository.save(enderecoEditar), ValidacaoEnum.EDITAR.getDescricao());
			} else {
				return montarDTOErro(validarEndereco(enderecoDto));
			}
		}
	}

	public EnderecoDTO montarDTO(Endereco endereco) {
		EnderecoDTO enderecoDto = new EnderecoDTO();
		enderecoDto.setId(endereco.getId());
		enderecoDto.setLogradouro(endereco.getLogradouro());
		enderecoDto.setNumero(endereco.getNumero());
		enderecoDto.setBairro(endereco.getBairro());
		enderecoDto.setCidade(endereco.getCidade());
		enderecoDto.setComplemento(endereco.getComplemento());
		enderecoDto.setCep(endereco.getCep());
		enderecoDto.setEstado(endereco.getEstado());
		return enderecoDto;
	}

	public Endereco montarEndereco(EnderecoDTO enderecoDto) {
		return repository.findById(enderecoDto.getId());
	}

	public EnderecoDTO montarDTOSucesso(Endereco endereco, String validacao) {
		logger.info("Montando objeto de sucesso.");
		EnderecoDTO enderecoDto = null;
		if (endereco != null) {
			enderecoDto = montarDTO(endereco);
			if (validacao.equals(ValidacaoEnum.SALVAR.getDescricao())) {
				enderecoDto.setMsgSucesso("Endereco cadastrado com sucesso.");
			} else if (validacao.equals(ValidacaoEnum.EDITAR.getDescricao())) {
				enderecoDto.setMsgSucesso("Endereco editado com sucesso.");
			}
		}
		return enderecoDto;
	}

	public EnderecoDTO montarDTOErro(String validacao) {
		logger.info("Montando objeto de erro.");
		EnderecoDTO enderecoDto = new EnderecoDTO();
		if (validacao.equals(ValidacaoEnum.BRANCO.getDescricao())) {
			enderecoDto.setMsgErro("Não pode existir campos em branco.");
		}
		if (validacao.equals(ValidacaoEnum.NULL.getDescricao())) {
			enderecoDto.setMsgErro("Não pode conter valores nulos.");
		}
		if (validacao.equals(ValidacaoEnum.NAO_EXISTE.getDescricao())) {
			enderecoDto.setMsgErro("O Endereço não existe.");
		}
		return enderecoDto;
	}

	public String validarEndereco(EnderecoDTO enderecoDto) {
		logger.info("Validando campos do Endereco.");
		try {
			if (enderecoDto.getLogradouro().isEmpty()
					|| enderecoDto.getNumero().isEmpty()
					|| enderecoDto.getBairro().isEmpty()
					|| enderecoDto.getCidade().isEmpty()
					|| enderecoDto.getCep().isEmpty()
					|| enderecoDto.getEstado().isEmpty()) {
				return ValidacaoEnum.BRANCO.getDescricao();
			} else {
				return ValidacaoEnum.OK.getDescricao();
			}
		} catch (NullPointerException erro) {
			return ValidacaoEnum.NULL.getDescricao();
		}
	}

}
