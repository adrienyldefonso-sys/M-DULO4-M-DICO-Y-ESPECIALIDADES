package com.hospital.medicos.repository;

import com.hospital.medicos.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByDni(String dni);
}