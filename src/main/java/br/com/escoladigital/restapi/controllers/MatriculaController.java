package br.com.escoladigital.restapi.controllers;

import br.com.escoladigital.restapi.dtos.MatriculaDTO;
import br.com.escoladigital.restapi.services.MatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/escoladigital/matricula")
public class MatriculaController {

    @Autowired
    private MatriculaService service;

    @PostMapping("/salvar")
    public MatriculaDTO salvar(@RequestBody MatriculaDTO matriculaDto) {
        return service.salvar(matriculaDto);
    }

    @GetMapping("/listarPorAluno/{id}")
    public MatriculaDTO listarPorId(@PathVariable(value = "id") long id) {
        return service.listarPorAluno(id);
    }

    @PutMapping("/editar/{id}")
    public MatriculaDTO editar(@PathVariable(value = "id") long id, @RequestBody MatriculaDTO matriculaDto) {
        return service.editar(id, matriculaDto);
    }

    @DeleteMapping("/deletar/{id}")
    public MatriculaDTO deletar(@PathVariable(value = "id") long id) {
        return service.deletar(id);
    }

}
