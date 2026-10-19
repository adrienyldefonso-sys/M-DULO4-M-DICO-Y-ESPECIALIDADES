package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.*;
import com.hospital.medicos.repository.*;
import com.hospital.medicos.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class CitaServiceImpl implements CitaService {

    private static final Set<String> ESTADOS = Set.of("PROGRAMADA", "ATENDIDA", "CANCELADA");

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final HorarioRepository horarioRepository;

    public CitaServiceImpl(CitaRepository citaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoRepository medicoRepository,
                           EspecialidadRepository especialidadRepository,
                           HorarioRepository horarioRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.horarioRepository = horarioRepository;
    }

    private ResponseStatusException error(HttpStatus status, String mensaje) {
        return new ResponseStatusException(status, mensaje);
    }

    private Long idDe(Object entidad, String nombre) {
        if (entidad == null) throw error(HttpStatus.BAD_REQUEST, "El campo " + nombre + " es obligatorio");
        try {
            Object id = entidad.getClass().getMethod("getId").invoke(entidad);
            if (id instanceof Long l) return l;
        } catch (Exception ignored) { }
        throw error(HttpStatus.BAD_REQUEST, "Falta el id de " + nombre);
    }

    // Valida las relaciones y copia los datos de 'datos' hacia 'cita'
    private void aplicar(Cita cita, Cita datos, Long idExcluir) {
        Paciente paciente = pacienteRepository.findById(idDe(datos.getPaciente(), "paciente"))
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
        Medico medico = medicoRepository.findById(idDe(datos.getMedico(), "médico"))
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Médico no encontrado"));
        Especialidad especialidad = especialidadRepository.findById(idDe(datos.getEspecialidad(), "especialidad"))
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Especialidad no encontrada"));

        if (!Boolean.TRUE.equals(paciente.getEstado()))
            throw error(HttpStatus.BAD_REQUEST, "El paciente está inactivo");
        if (!Boolean.TRUE.equals(medico.getEstado()))
            throw error(HttpStatus.BAD_REQUEST, "El médico está inactivo");
        if (!Boolean.TRUE.equals(especialidad.getEstado()))
            throw error(HttpStatus.BAD_REQUEST, "La especialidad está inactiva");

        boolean ejerce = medico.getEspecialidades().stream()
                .anyMatch(e -> e.getId().equals(especialidad.getId()));
        if (!ejerce)
            throw error(HttpStatus.BAD_REQUEST, "El médico no tiene asignada la especialidad seleccionada");

        Horario horario = null;
        if (datos.getHorario() != null && datos.getHorario().getId() != null) {
            horario = horarioRepository.findById(datos.getHorario().getId())
                    .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Horario no encontrado"));
            if (!horario.getMedico().getId().equals(medico.getId()))
                throw error(HttpStatus.BAD_REQUEST, "El horario no pertenece a ese médico");
        }

        if (!datos.getHoraFin().isAfter(datos.getHoraInicio()))
            throw error(HttpStatus.BAD_REQUEST, "La hora de fin debe ser posterior a la hora de inicio");
        if (datos.getFecha().isBefore(LocalDate.now()))
            throw error(HttpStatus.BAD_REQUEST, "No se puede programar una cita en una fecha pasada");

        boolean cruce = citaRepository.findByMedicoIdAndFechaOrderByHoraInicio(medico.getId(), datos.getFecha())
                .stream()
                .filter(c -> !"CANCELADA".equals(c.getEstado()))
                .filter(c -> idExcluir == null || !c.getId().equals(idExcluir))
                .anyMatch(c -> c.getHoraInicio().isBefore(datos.getHoraFin())
                        && datos.getHoraInicio().isBefore(c.getHoraFin()));
        if (cruce)
            throw error(HttpStatus.CONFLICT, "El médico ya tiene una cita en ese rango de horas");

        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setEspecialidad(especialidad);
        cita.setHorario(horario);
        cita.setFecha(datos.getFecha());
        cita.setHoraInicio(datos.getHoraInicio());
        cita.setHoraFin(datos.getHoraFin());
        cita.setMotivo(datos.getMotivo());
    }

    @Override
    @Transactional
    public Cita registrarCita(Cita datos) {
        Cita cita = new Cita();
        aplicar(cita, datos, null);
        cita.setEstado("PROGRAMADA");
        return citaRepository.save(cita);
    }

    @Override
    @Transactional
    public Cita actualizarCita(Long id, Cita datos) {
        Cita cita = obtenerPorId(id);
        if (!"PROGRAMADA".equals(cita.getEstado()))
            throw error(HttpStatus.BAD_REQUEST, "Solo se pueden modificar citas programadas");
        aplicar(cita, datos, id);
        return citaRepository.save(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Cita no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId);
    }

    @Override
    @Transactional
    public Cita cambiarEstado(Long id, String estado) {
        String nuevo = estado == null ? "" : estado.trim().toUpperCase();
        if (!ESTADOS.contains(nuevo))
            throw error(HttpStatus.BAD_REQUEST, "Estado inválido. Usa: PROGRAMADA, ATENDIDA o CANCELADA");
        Cita cita = obtenerPorId(id);
        cita.setEstado(nuevo);
        return citaRepository.save(cita);
    }
}