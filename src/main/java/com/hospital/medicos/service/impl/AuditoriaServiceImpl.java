package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Auditoria;
import com.hospital.medicos.repository.AuditoriaRepository;
import com.hospital.medicos.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // REQUIRES_NEW: el registro de auditoría se guarda en su propia transacción,
    // independiente de la operación que lo originó
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void guardarRegistro(String usuario, String operacion, String entidad, Long entidadId, String detalle) {
        Auditoria a = new Auditoria();
        a.setUsuario(usuario);
        a.setOperacion(operacion);
        a.setEntidad(entidad);
        a.setEntidadId(entidadId);
        a.setDetalle(detalle);
        auditoriaRepository.save(a);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Auditoria> listar(String entidad) {
        if (entidad == null || entidad.isBlank()) {
            return auditoriaRepository.findAllByOrderByFechaHoraDesc();
        }
        return auditoriaRepository.findByEntidadOrderByFechaHoraDesc(entidad);
    }
}