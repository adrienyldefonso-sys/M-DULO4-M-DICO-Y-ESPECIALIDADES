package com.hospital.medicos.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 401: no autenticado. Responde JSON sin el encabezado WWW-Authenticate,
        // para que el navegador no abra su ventana emergente de usuario y contraseña
        AuthenticationEntryPoint entryPoint = (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":401,\"error\":\"Credenciales inválidas o usuario inactivo\"}");
        };

        // 403: autenticado pero sin permiso para ese recurso
        AccessDeniedHandler deniedHandler = (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\":403,\"error\":\"No tienes permiso para esta operación\"}");
        };

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.authenticationEntryPoint(entryPoint))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Preflight de CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Cualquier usuario autenticado puede consultar quién es
                        .requestMatchers("/api/auth/**").authenticated()

                        // Solo administrador
                        .requestMatchers("/api/usuarios/**", "/api/roles/**", "/api/auditoria/**").hasRole("ADMINISTRADOR")

                        // Médicos y su agenda: consulta para los 3 roles
                        .requestMatchers(HttpMethod.GET,
                                "/api/medicos/**", "/api/especialidades/**", "/api/horarios/**",
                                "/api/consultorios/**", "/api/disponibilidad/**")
                        .hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")

                        // ...pero crear, editar y reportes solo el administrador
                        .requestMatchers("/api/medicos/**", "/api/especialidades/**", "/api/horarios/**",
                                "/api/consultorios/**", "/api/disponibilidad/**", "/api/reportes/**")
                        .hasRole("ADMINISTRADOR")

                        // Módulos de otros integrantes
                        .requestMatchers("/api/pacientes/**").hasAnyRole("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA")
                        .requestMatchers("/api/citas/**").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")

                        // Cualquier otra ruta de la API: solo administrador
                        .requestMatchers("/api/**").hasRole("ADMINISTRADOR")

                        // Archivos del frontend (HTML, CSS, JS): públicos; lo protegido es la API
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}