package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Paciente;
import com.hospital.medicos.repository.PacienteRepository;
import com.hospital.medicos.service.PacienteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    @Transactional
    public Paciente registrarPaciente(Paciente paciente) {
        String dni = paciente.getDni().trim();
        if (pacienteRepository.existsByDni(dni)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un paciente con el DNI: " + dni);
        }
        paciente.setId(null);
        paciente.setDni(dni);
        paciente.setEstado(true);
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public Paciente actualizarPaciente(Long id, Paciente datos) {
        Paciente paciente = obtenerPorId(id);
        String dni = datos.getDni().trim();
        if (!dni.equals(paciente.getDni()) && pacienteRepository.existsByDni(dni)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un paciente con el DNI: " + dni);
        }
        paciente.setDni(dni);
        paciente.setNombres(datos.getNombres());
        paciente.setApellidos(datos.getApellidos());
        paciente.setFechaNacimiento(datos.getFechaNacimiento());
        paciente.setTelefono(datos.getTelefono());
        paciente.setCorreo(datos.getCorreo());
        paciente.setDireccion(datos.getDireccion());
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Paciente cambiarEstado(Long id, Boolean estado) {
        Paciente paciente = obtenerPorId(id);
        paciente.setEstado(estado);
        return pacienteRepository.save(paciente);
    }
}