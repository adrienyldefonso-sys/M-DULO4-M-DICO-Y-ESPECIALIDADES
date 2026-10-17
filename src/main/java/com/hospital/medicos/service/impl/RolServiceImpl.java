package com.hospital.medicos.service.impl;

import com.hospital.medicos.entity.Rol;
import com.hospital.medicos.repository.RolRepository;
import com.hospital.medicos.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Autowired
    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional
    public Rol registrarRol(Rol rol) {
        String nombre = rol.getNombre().trim().toUpperCase();
        if (rolRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con el nombre: " + nombre);
        }
        rol.setId(null);
        rol.setNombre(nombre);
        rol.setEstado(true);
        return rolRepository.save(rol);
    }

    @Override
    @Transactional
    public Rol actualizarRol(Long id, Rol detalles) {
        Rol rol = obtenerPorId(id);
        String nombre = detalles.getNombre().trim().toUpperCase();
        if (!nombre.equals(rol.getNombre()) && rolRepository.existsByNombre(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con el nombre: " + nombre);
        }
        rol.setNombre(nombre);
        rol.setDescripcion(detalles.getDescripcion());
        return rolRepository.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Rol obtenerPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Rol cambiarEstado(Long id, Boolean estado) {
        Rol rol = obtenerPorId(id);
        rol.setEstado(estado);
        return rolRepository.save(rol);
    }
}