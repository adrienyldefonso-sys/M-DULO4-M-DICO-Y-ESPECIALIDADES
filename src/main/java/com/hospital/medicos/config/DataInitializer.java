package com.hospital.medicos.config;

import com.hospital.medicos.entity.Rol;
import com.hospital.medicos.entity.Usuario;
import com.hospital.medicos.repository.RolRepository;
import com.hospital.medicos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (String nombre : List.of("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")) {
            if (!rolRepository.existsByNombre(nombre)) {
                Rol rol = new Rol();
                rol.setNombre(nombre);
                rol.setDescripcion("Rol " + nombre);
                rol.setEstado(true);
                rolRepository.save(rol);
            }
        }

        if (!usuarioRepository.existsByUsername("admin")) {
            Rol admin = rolRepository.findByNombre("ADMINISTRADOR").orElseThrow();
            Usuario u = new Usuario();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setEstado(true);
            u.setRol(admin);
            usuarioRepository.save(u);
        }
    }
}