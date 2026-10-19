package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Especialidad;
import com.hospital.medicos.entity.Medico;
import com.hospital.medicos.entity.Usuario;
import com.hospital.medicos.repository.EspecialidadRepository;
import com.hospital.medicos.repository.MedicoRepository;
import com.hospital.medicos.repository.UsuarioRepository;
import com.hospital.medicos.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MedicoServiceImpl(MedicoRepository medicoRepository,
                             EspecialidadRepository especialidadRepository,
                             UsuarioRepository usuarioRepository) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Medico registrarMedico(Medico medico) {
        if (medicoRepository.existsByCmp(medico.getCmp())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un médico con el CMP: " + medico.getCmp());
        }
        medico.setEstado(true);
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public Medico actualizarMedico(Long id, Medico medicoDetalles) {
        Medico medico = obtenerPorId(id);
        medico.setNombre(medicoDetalles.getNombre());
        medico.setApellido(medicoDetalles.getApellido());
        medico.setCmp(medicoDetalles.getCmp());
        medico.setTelefono(medicoDetalles.getTelefono());
        medico.setEmail(medicoDetalles.getEmail());
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> obtenerTodos() {
        return medicoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Medico obtenerPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Médico no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Medico cambiarEstado(Long id, Boolean estado) {
        Medico medico = obtenerPorId(id);
        medico.setEstado(estado);
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public Medico asociarEspecialidades(Long medicoId, List<Long> especialidadIds) {
        Medico medico = obtenerPorId(medicoId);
        List<Especialidad> especialidades = especialidadRepository.findAllById(especialidadIds);

        if (especialidades.size() != especialidadIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Una o más especialidades no existen");
        }

        for (Especialidad esp : especialidades) {
            if (!medico.getEspecialidades().contains(esp)) {
                medico.getEspecialidades().add(esp);
            }
        }
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public Medico asociarUsuario(Long medicoId, Long usuarioId) {
        Medico medico = obtenerPorId(medicoId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado con ID: " + usuarioId));

        if (!"MEDICO".equals(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener el rol MEDICO");
        }

        boolean yaEsDeEste = medico.getUsuario() != null && medico.getUsuario().getId().equals(usuarioId);
        if (!yaEsDeEste && medicoRepository.existsByUsuarioId(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese usuario ya está asociado a otro médico");
        }

        medico.setUsuario(usuario);
        return medicoRepository.save(medico);
    }
}