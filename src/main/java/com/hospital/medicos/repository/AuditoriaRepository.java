package com.hospital.medicos.repository;

import com.hospital.medicos.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findAllByOrderByFechaHoraDesc();
    List<Auditoria> findByEntidadOrderByFechaHoraDesc(String entidad);
}