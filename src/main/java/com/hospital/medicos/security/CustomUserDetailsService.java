package com.hospital.medicos.security;

import com.hospital.medicos.entity.Usuario;
import com.hospital.medicos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // @Transactional es necesario: el rol se carga de forma diferida (LAZY) y este código
    // corre dentro del filtro de seguridad, donde la sesión de Hibernate aún no está abierta
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Un usuario no puede entrar si él o su rol están desactivados
        boolean activo = Boolean.TRUE.equals(usuario.getEstado())
                && Boolean.TRUE.equals(usuario.getRol().getEstado());

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre()) // Spring agrega el prefijo ROLE_ automáticamente
                .disabled(!activo)
                .build();
    }
}