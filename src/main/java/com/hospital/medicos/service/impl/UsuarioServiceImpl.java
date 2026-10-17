package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Rol;
import com.hospital.medicos.entity.Usuario;
import com.hospital.medicos.repository.RolRepository;
import com.hospital.medicos.repository.UsuarioRepository;
import com.hospital.medicos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 6 caracteres");
        }
    }

    // Recibe el rol como {"id": X}, lo busca en la BD y exige que esté activo
    private Rol obtenerRolActivo(Rol referencia) {
        if (referencia == null || referencia.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }
        Rol rol = rolRepository.findById(referencia.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rol no encontrado con ID: " + referencia.getId()));
        if (!Boolean.TRUE.equals(rol.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol seleccionado está inactivo");
        }
        return rol;
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        String username = usuario.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe el usuario: " + username);
        }
        validarPassword(usuario.getPassword());

        usuario.setId(null);
        usuario.setUsername(username);
        usuario.setRol(obtenerRolActivo(usuario.getRol()));
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstado(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario detalles) {
        Usuario usuario = obtenerPorId(id);

        String username = detalles.getUsername().trim();
        if (!username.equals(usuario.getUsername()) && usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe el usuario: " + username);
        }
        usuario.setUsername(username);

        // La contraseña es opcional al editar: si no viene, se conserva la actual
        if (detalles.getPassword() != null && !detalles.getPassword().isBlank()) {
            validarPassword(detalles.getPassword());
            usuario.setPassword(passwordEncoder.encode(detalles.getPassword()));
        }

        // El rol también es opcional al editar
        if (detalles.getRol() != null && detalles.getRol().getId() != null) {
            usuario.setRol(obtenerRolActivo(detalles.getRol()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Usuario cambiarEstado(Long id, Boolean estado) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(estado);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario asignarRol(Long usuarioId, Long rolId) {
        Usuario usuario = obtenerPorId(usuarioId);
        Rol referencia = new Rol();
        referencia.setId(rolId);
        usuario.setRol(obtenerRolActivo(referencia));
        return usuarioRepository.save(usuario);
    }
}