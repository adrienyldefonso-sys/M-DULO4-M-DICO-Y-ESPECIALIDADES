package com.hospital.medicos.controller;

import com.hospital.medicos.entity.Auditoria;
import com.hospital.medicos.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // Solo consulta: la bitácora no se edita ni se borra desde la API
    @GetMapping
    public ResponseEntity<List<Auditoria>> listar(@RequestParam(required = false) String entidad) {
        return ResponseEntity.ok(auditoriaService.listar(entidad));
    }
}