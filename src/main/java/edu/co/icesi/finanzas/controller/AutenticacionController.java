package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.dto.RegistroRequest;
import edu.co.icesi.finanzas.exception.BusinessException;
import edu.co.icesi.finanzas.repo.MonedaRepository;
import edu.co.icesi.finanzas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AutenticacionController {
    private final UsuarioService usuarios;
    private final MonedaRepository monedas;

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        if (!model.containsAttribute("registro")) {
            model.addAttribute("registro", new RegistroRequest(null, null, null, null));
        }
        model.addAttribute("monedas", monedas.findAll(Sort.by("codigo")));
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registro") RegistroRequest registro,
            BindingResult resultado, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                usuarios.registrar(registro);
                redirect.addFlashAttribute("exito", "Tu cuenta fue creada. Ya puedes iniciar sesión.");
                return "redirect:/login";
            } catch (BusinessException error) {
                resultado.reject("registro.invalido", error.getMessage());
            }
        }
        guardarErroresSinPassword(registro, resultado, redirect);
        return "redirect:/registro";
    }

    private void guardarErroresSinPassword(RegistroRequest registro, BindingResult resultado,
            RedirectAttributes redirect) {
        var seguro = new RegistroRequest(registro.nombreCompleto(), registro.correo(), null, registro.monedaId());
        var errores = new BeanPropertyBindingResult(seguro, "registro");
        for (ObjectError error : resultado.getAllErrors()) {
            if (error instanceof FieldError campo) {
                Object rechazado = "password".equals(campo.getField()) ? null : campo.getRejectedValue();
                errores.addError(new FieldError("registro", campo.getField(), rechazado,
                    campo.isBindingFailure(), campo.getCodes(), campo.getArguments(), campo.getDefaultMessage()));
            } else {
                errores.addError(new ObjectError("registro", error.getCodes(), error.getArguments(), error.getDefaultMessage()));
            }
        }
        redirect.addFlashAttribute("registro", seguro);
        redirect.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "registro", errores);
    }

    // El contenedor conserva POST al reenviar una solicitud rechazada por CSRF.
    @RequestMapping(value = "/acceso-denegado", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accesoDenegado() {
        return "auth/acceso-denegado";
    }
}
