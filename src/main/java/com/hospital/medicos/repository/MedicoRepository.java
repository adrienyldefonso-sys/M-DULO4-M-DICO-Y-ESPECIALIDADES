package com.hospital.medicos.repository;

import com.hospital.medicos.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Al heredar de JpaRepository, ya cuentas con los métodos CRUD básicos (save, findAll, findById, deleteById, etc.)
}