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

import br.com.escoladigital.restapi.dtos.AlunoDTO;
import br.com.escoladigital.restapi.services.AlunoService;

@RestController
@RequestMapping(value = "/escoladigital/aluno")
public class AlunoController {

	@Autowired
	private AlunoService service;

	@PostMapping("/salvar")
	public AlunoDTO salvar(@RequestBody AlunoDTO alunoDto) {
		return service.salvar(alunoDto);
	}

	@GetMapping("/listarTodos")
	public List<AlunoDTO> listarTodos() {
		List<AlunoDTO> dtos = service.listarTodos();
		return dtos;
	}

	@GetMapping("/listar/{id}")
	public AlunoDTO listarPorId(@PathVariable(value = "id") long id) {
		return service.listarPorId(id);
	}

	@PutMapping("/editar/{id}")
	public AlunoDTO editar(@PathVariable(value = "id") long id, @RequestBody AlunoDTO alunoDto) {
		return service.editar(id, alunoDto);
	}

	@DeleteMapping("/deletar/{id}")
	public AlunoDTO deletar(@PathVariable(value = "id") long id) {
		return service.deletar(id);
	}

}
