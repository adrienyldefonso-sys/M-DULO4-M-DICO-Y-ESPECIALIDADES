package com.hospital.medicos.controller;

import com.hospital.medicos.entity.Medico;
import com.hospital.medicos.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    @Autowired
    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // RF-MED-01: Registrar médico
    @PostMapping
    public ResponseEntity<Medico> registrarMedico(@RequestBody Medico medico) {
        return new ResponseEntity<>(medicoService.registrarMedico(medico), HttpStatus.CREATED);
    }

    // RF-MED-02: Modificar datos del médico
    @PutMapping("/{id}")
    public ResponseEntity<Medico> actualizarMedico(@PathVariable Long id, @RequestBody Medico medico) {
        return ResponseEntity.ok(medicoService.actualizarMedico(id, medico));
    }

    // RF-MED-03: Consultar todos los médicos
    @GetMapping
    public ResponseEntity<List<Medico>> obtenerTodos() {
        return ResponseEntity.ok(medicoService.obtenerTodos());
    }

    // RF-MED-03: Consultar médico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medico> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.obtenerPorId(id));
    }

    // RF-MED-04: Activar o desactivar médico
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Medico> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        return ResponseEntity.ok(medicoService.cambiarEstado(id, estado));
    }

    // RF-MED-06: Asociar una o más especialidades a un médico
    @PostMapping("/{medicoId}/especialidades")
    public ResponseEntity<Medico> asociarEspecialidades(
            @PathVariable Long medicoId,
            @RequestBody List<Long> especialidadIds) {
        return ResponseEntity.ok(medicoService.asociarEspecialidades(medicoId, especialidadIds));
    }
}