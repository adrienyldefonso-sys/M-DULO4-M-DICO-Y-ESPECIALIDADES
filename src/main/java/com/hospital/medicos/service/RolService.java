package com.hospital.medicos.service;

import com.hospital.medicos.entity.Rol;
import java.util.List;

public interface RolService {
    Rol registrarRol(Rol rol);
    Rol actualizarRol(Long id, Rol rol);
    List<Rol> obtenerTodos();
    Rol obtenerPorId(Long id);
    Rol cambiarEstado(Long id, Boolean estado);
}