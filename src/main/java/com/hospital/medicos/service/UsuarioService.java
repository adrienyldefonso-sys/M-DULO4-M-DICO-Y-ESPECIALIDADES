package com.hospital.medicos.service;

import com.hospital.medicos.entity.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario registrarUsuario(Usuario usuario);
    Usuario actualizarUsuario(Long id, Usuario usuario);
    List<Usuario> obtenerTodos();
    Usuario obtenerPorId(Long id);
    Usuario cambiarEstado(Long id, Boolean estado);
    Usuario asignarRol(Long usuarioId, Long rolId);
}