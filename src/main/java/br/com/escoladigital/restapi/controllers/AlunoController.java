package br.com.escoladigital.restapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.com.escoladigital.restapi.dtos.AlunoDTO;
import br.com.escoladigital.restapi.services.AlunoService;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
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
