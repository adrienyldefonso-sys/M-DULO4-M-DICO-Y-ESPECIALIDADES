package com.hospital.medicos.service;

import com.hospital.medicos.entity.Cita;
import java.util.List;

public interface CitaService {
    Cita registrarCita(Cita cita);
    Cita actualizarCita(Long id, Cita cita);
    List<Cita> obtenerTodas();
    Cita obtenerPorId(Long id);
    List<Cita> obtenerPorPaciente(Long pacienteId);
    Cita cambiarEstado(Long id, String estado);
}