package com.hospital.medicos.controller;

import com.hospital.medicos.entity.VacacionPermiso;
import com.hospital.medicos.service.VacacionPermisoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacaciones")
public class VacacionPermisoController {

    private final VacacionPermisoService vacacionService;

    public VacacionPermisoController(VacacionPermisoService vacacionService) {
        this.vacacionService = vacacionService;
    }

    @PostMapping("/medico/{medicoId}")
    public ResponseEntity<VacacionPermiso> registrar(@PathVariable Long medicoId,
                                                     @Valid @RequestBody VacacionPermiso vacacion) {
        return new ResponseEntity<>(vacacionService.registrarVacacion(medicoId, vacacion), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacacionPermiso> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody VacacionPermiso vacacion) {
        return ResponseEntity.ok(vacacionService.actualizarVacacion(id, vacacion));
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<VacacionPermiso>> obtenerPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(vacacionService.obtenerPorMedico(medicoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacacionPermiso> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vacacionService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vacacionService.eliminarVacacion(id);
        return ResponseEntity.noContent().build();
    }
}