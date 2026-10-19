package com.hospital.medicos.service;

import com.hospital.medicos.entity.Paciente;
import java.util.List;

public interface PacienteService {
    Paciente registrarPaciente(Paciente paciente);
    Paciente actualizarPaciente(Long id, Paciente paciente);
    List<Paciente> obtenerTodos();
    Paciente obtenerPorId(Long id);
    Paciente cambiarEstado(Long id, Boolean estado);
}