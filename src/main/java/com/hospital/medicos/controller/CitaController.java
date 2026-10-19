package com.hospital.medicos.controller;

import com.hospital.medicos.entity.Cita;
import com.hospital.medicos.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<Cita> registrar(@Valid @RequestBody Cita cita) {
        return new ResponseEntity<>(citaService.registrarCita(cita), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(@PathVariable Long id, @Valid @RequestBody Cita cita) {
        return ResponseEntity.ok(citaService.actualizarCita(id, cita));
    }

    @GetMapping
    public ResponseEntity<List<Cita>> obtenerTodas() {
        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Cita>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.obtenerPorPaciente(pacienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Cita> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, estado));
    }
}