package com.hospital.medicos.service;

import com.hospital.medicos.entity.Auditoria;
import java.util.List;

public interface AuditoriaService {
    void guardarRegistro(String usuario, String operacion, String entidad, Long entidadId, String detalle);
    List<Auditoria> listar(String entidad);
}