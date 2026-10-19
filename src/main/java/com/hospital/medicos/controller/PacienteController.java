package com.hospital.medicos.controller;

import com.hospital.medicos.entity.Paciente;
import com.hospital.medicos.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<Paciente> registrar(@Valid @RequestBody Paciente paciente) {
        return new ResponseEntity<>(pacienteService.registrarPaciente(paciente), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable Long id, @Valid @RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.actualizarPaciente(id, paciente));
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Paciente> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        return ResponseEntity.ok(pacienteService.cambiarEstado(id, estado));
    }
}