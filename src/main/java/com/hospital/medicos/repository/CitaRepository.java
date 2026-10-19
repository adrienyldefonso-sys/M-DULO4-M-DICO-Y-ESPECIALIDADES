package com.hospital.medicos.repository;

import com.hospital.medicos.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);
    List<Cita> findByMedicoIdAndFechaOrderByHoraInicio(Long medicoId, LocalDate fecha);
}