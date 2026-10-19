package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Medico;
import com.hospital.medicos.entity.VacacionPermiso;
import com.hospital.medicos.repository.MedicoRepository;
import com.hospital.medicos.repository.VacacionPermisoRepository;
import com.hospital.medicos.service.VacacionPermisoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VacacionPermisoServiceImpl implements VacacionPermisoService {

    private final VacacionPermisoRepository vacacionRepository;
    private final MedicoRepository medicoRepository;

    public VacacionPermisoServiceImpl(VacacionPermisoRepository vacacionRepository,
                                      MedicoRepository medicoRepository) {
        this.vacacionRepository = vacacionRepository;
        this.medicoRepository = medicoRepository;
    }

    private void validarFechas(VacacionPermiso v) {
        if (v.getFechaFin().isBefore(v.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    @Override
    @Transactional
    public VacacionPermiso registrarVacacion(Long medicoId, VacacionPermiso vacacion) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Médico no encontrado con ID: " + medicoId));
        validarFechas(vacacion);
        vacacion.setId(null);
        vacacion.setMedico(medico);
        vacacion.setEstado("NO_DISPONIBLE");
        return vacacionRepository.save(vacacion);
    }

    @Override
    @Transactional
    public VacacionPermiso actualizarVacacion(Long id, VacacionPermiso datos) {
        VacacionPermiso vacacion = obtenerPorId(id);
        validarFechas(datos);
        vacacion.setFechaInicio(datos.getFechaInicio());
        vacacion.setFechaFin(datos.getFechaFin());
        vacacion.setMotivo(datos.getMotivo());
        return vacacionRepository.save(vacacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacacionPermiso> obtenerPorMedico(Long medicoId) {
        return vacacionRepository.findByMedicoId(medicoId);
    }

    @Override
    @Transactional(readOnly = true)
    public VacacionPermiso obtenerPorId(Long id) {
        return vacacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Vacación/permiso no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void eliminarVacacion(Long id) {
        VacacionPermiso vacacion = obtenerPorId(id);
        vacacionRepository.delete(vacacion);
    }
}