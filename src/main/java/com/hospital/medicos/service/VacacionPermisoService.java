package com.hospital.medicos.service;

import com.hospital.medicos.entity.VacacionPermiso;
import java.util.List;

public interface VacacionPermisoService {
    VacacionPermiso registrarVacacion(Long medicoId, VacacionPermiso vacacion);
    VacacionPermiso actualizarVacacion(Long id, VacacionPermiso vacacion);
    List<VacacionPermiso> obtenerPorMedico(Long medicoId);
    VacacionPermiso obtenerPorId(Long id);
    void eliminarVacacion(Long id);
}