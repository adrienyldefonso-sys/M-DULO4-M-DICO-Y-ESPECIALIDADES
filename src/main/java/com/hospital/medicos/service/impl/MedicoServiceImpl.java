package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Especialidad;
import com.hospital.medicos.entity.Medico;
import com.hospital.medicos.repository.EspecialidadRepository;
import com.hospital.medicos.repository.MedicoRepository;
import com.hospital.medicos.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;

    @Autowired // Inyección de Dependencias (DI)
    public MedicoServiceImpl(MedicoRepository medicoRepository, EspecialidadRepository especialidadRepository) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
    }

    // RF-MED-01: Registrar Médico (Sin validación de CMP duplicado)
    @Override
    public Medico registrarMedico(Medico medico) {
        medico.setEstado(true);
        return medicoRepository.save(medico);
    }

    // RF-MED-02: Modificar Médico
    @Override
    public Medico actualizarMedico(Long id, Medico medicoDetalles) {
        Medico medico = obtenerPorId(id);
        medico.setNombre(medicoDetalles.getNombre());
        medico.setApellido(medicoDetalles.getApellido());
        medico.setCmp(medicoDetalles.getCmp());
        medico.setTelefono(medicoDetalles.getTelefono());
        medico.setEmail(medicoDetalles.getEmail());
        return medicoRepository.save(medico);
    }

    // RF-MED-03: Consultar Médicos
    @Override
    public List<Medico> obtenerTodos() {
        return medicoRepository.findAll();
    }

    @Override
    public Medico obtenerPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));
    }

    // RF-MED-04: Activar / Desactivar Médico
    @Override
    public Medico cambiarEstado(Long id, Boolean estado) {
        Medico medico = obtenerPorId(id);
        medico.setEstado(estado);
        return medicoRepository.save(medico);
    }

    // RF-MED-06: Asociar una o más especialidades a un médico (@ManyToMany)
    @Override
    public Medico asociarEspecialidades(Long medicoId, List<Long> especialidadIds) {
        Medico medico = obtenerPorId(medicoId);
        List<Especialidad> especialidades = especialidadRepository.findAllById(especialidadIds);

        // Agregar las nuevas especialidades sin duplicar
        for (Especialidad esp : especialidades) {
            if (!medico.getEspecialidades().contains(esp)) {
                medico.getEspecialidades().add(esp);
            }
        }
        return medicoRepository.save(medico);
    }
}