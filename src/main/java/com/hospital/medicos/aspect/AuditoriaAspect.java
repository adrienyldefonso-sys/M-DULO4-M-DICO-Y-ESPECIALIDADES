package com.hospital.medicos.aspect;

import com.hospital.medicos.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AuditoriaAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);

    private final AuditoriaService auditoriaService;

    public AuditoriaAspect(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // Métodos que modifican datos, en cualquier Service, excepto el propio de auditoría
    @Pointcut("(execution(* com.hospital.medicos.service.impl.*.registrar*(..))"
            + " || execution(* com.hospital.medicos.service.impl.*.actualizar*(..))"
            + " || execution(* com.hospital.medicos.service.impl.*.cambiarEstado(..))"
            + " || execution(* com.hospital.medicos.service.impl.*.asociar*(..))"
            + " || execution(* com.hospital.medicos.service.impl.*.asignar*(..))"
            + " || execution(* com.hospital.medicos.service.impl.*.bloquear*(..))"
            + " || execution(* com.hospital.medicos.service.impl.*.eliminar*(..)))"
            + " && !within(com.hospital.medicos.service.impl.AuditoriaServiceImpl)")
    public void operacionesDeEscritura() {}

    // Solo se ejecuta si el método terminó bien (si lanzó una excepción, no se audita)
    @AfterReturning(pointcut = "operacionesDeEscritura()", returning = "resultado")
    public void auditar(JoinPoint jp, Object resultado) {
        try {
            String metodo = jp.getSignature().getName();
            String claseServicio = jp.getTarget().getClass().getSimpleName();

            auditoriaService.guardarRegistro(
                    usuarioActual(),
                    operacionDe(metodo),
                    entidadDe(resultado, claseServicio),
                    idDe(resultado, jp.getArgs()),
                    claseServicio + "." + metodo);
        } catch (Exception e) {
            // Un fallo de auditoría nunca debe romper la operación del usuario
            log.warn("No se pudo registrar la auditoría: {}", e.getMessage());
        }
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "SISTEMA";
        }
        return auth.getName();
    }

    private String operacionDe(String metodo) {
        if (metodo.startsWith("registrar") || metodo.startsWith("bloquear")) return "REGISTRO";
        if (metodo.startsWith("eliminar")) return "ELIMINACION";
        return "MODIFICACION";
    }

    // Si el método devuelve una entidad se usa su nombre; si es void (eliminar), se deduce del Service
    private String entidadDe(Object resultado, String claseServicio) {
        if (resultado != null && !(resultado instanceof Collection)) {
            return resultado.getClass().getSimpleName();
        }
        return claseServicio.replace("ServiceImpl", "");
    }

    // Id del registro: del objeto devuelto, o en su defecto del primer argumento Long (ej. eliminar(id))
    private Long idDe(Object resultado, Object[] args) {
        if (resultado != null) {
            try {
                Object id = resultado.getClass().getMethod("getId").invoke(resultado);
                if (id instanceof Long l) return l;
            } catch (Exception ignored) { }
        }
        for (Object a : args) {
            if (a instanceof Long l) return l;
        }
        return null;
    }
}