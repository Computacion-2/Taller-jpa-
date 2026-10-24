package edu.co.icesi.finanzas.exception;

import edu.co.icesi.finanzas.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "edu.co.icesi.finanzas.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MvcExceptionHandler {
    @ModelAttribute
    public void navegacion(HttpServletRequest request, Model model) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        model.addAttribute("rutaActual", ruta);
        model.addAttribute("seccionActual", ruta.startsWith("/usuarios") ? "Usuarios" :
            ruta.startsWith("/roles") ? "Roles" : ruta.startsWith("/permisos") ? "Permisos" : "Inicio");
    }

    @ExceptionHandler(BusinessException.class)
    public String business(BusinessException ex, HttpServletRequest request, HttpServletResponse response,
                           Model model, RedirectAttributes flash) {
        return manejar(ex.getStatus().value(), ex.getMessage(), request, response, model, flash);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String datoInvalido(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes flash) {
        return manejar(400, "Revise los valores de búsqueda o la dirección solicitada.", request, response, model, flash);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String integridad(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes flash) {
        return manejar(409, "No se puede completar la operación porque el registro conserva datos asociados.", request, response, model, flash);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String denegado(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes flash) {
        return manejar(403, "Tu cuenta no tiene permiso para acceder a esta función.", request, response, model, flash);
    }

    private String manejar(int status, String mensaje, HttpServletRequest request, HttpServletResponse response,
                           Model model, RedirectAttributes flash) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            flash.addFlashAttribute("error", mensaje);
            return "redirect:/";
        }
        response.setStatus(status);
        model.addAttribute("codigo", status); model.addAttribute("mensaje", mensaje);
        return "error/general";
    }
}
