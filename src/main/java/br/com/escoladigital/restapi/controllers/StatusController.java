package br.com.escoladigital.restapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.escoladigital.restapi.dtos.StatusDTO;
import br.com.escoladigital.restapi.models.Status;
import br.com.escoladigital.restapi.services.StatusService;

@RestController
@RequestMapping(value = "/escoladigital/status")
public class StatusController {

	@Autowired
	private StatusService service;

	@PostMapping("/salvar")
	public StatusDTO salvar(@RequestBody StatusDTO statusDto) {
		return service.salvar(statusDto);
	}

	@GetMapping("/listarTodos")
	public List<StatusDTO> listarTodos() {
		List<StatusDTO> dtos = service.listarTodos();
		return dtos;
	}

	@GetMapping("/listar/{id}")
	public StatusDTO listarPorId(@PathVariable(value = "id") long id) {
		return service.listarPorId(id);
	}

	@PutMapping("/editar/{id}")
	public StatusDTO editar(@PathVariable(value = "id") long id, @RequestBody StatusDTO statusDto) {
		return service.editar(id, statusDto);
	}

	@DeleteMapping("/deletar/{id}")
	public StatusDTO deletar(@PathVariable(value = "id") long id) {
		return service.deletar(id);
	}

}
