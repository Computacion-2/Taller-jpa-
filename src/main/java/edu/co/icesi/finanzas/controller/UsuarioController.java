package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.exception.BusinessException;
import edu.co.icesi.finanzas.repo.MonedaRepository;
import edu.co.icesi.finanzas.service.RolService;
import edu.co.icesi.finanzas.service.UsuarioService;
import edu.co.icesi.finanzas.dto.form.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('USUARIOS_GESTIONAR')")
public class UsuarioController {
    private final UsuarioService usuarios;
    private final RolService roles;
    private final MonedaRepository monedas;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String texto,
                         @RequestParam(required = false) Long rolId,
                         @RequestParam(required = false) Estado estado,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        var opciones = roles.listar();
        model.addAttribute("pagina", usuarios.listar(texto, rolId, estado,
            PageRequest.of(Math.max(0, page), 10, Sort.by("id").ascending())));
        model.addAttribute("roles", opciones);
        model.addAttribute("estados", Estado.values());
        model.addAttribute("texto", texto); model.addAttribute("rolId", rolId); model.addAttribute("estado", estado);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("usuario")) model.addAttribute("usuario", new UsuarioCrearForm());
        opciones(model); model.addAttribute("creacion", true);
        return "usuarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var actual = usuarios.obtener(id);
        if (!model.containsAttribute("usuario")) model.addAttribute("usuario", UsuarioEditarForm.from(actual));
        opciones(model); model.addAttribute("creacion", false); model.addAttribute("usuarioId", id);
        return "usuarios/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("usuario") UsuarioCrearForm form, BindingResult errors,
                        RedirectAttributes flash) {
        if (errors.hasErrors()) return invalidar(form, errors, flash, "/usuarios/nuevo");
        try {
            usuarios.crear(form.toRequest());
            flash.addFlashAttribute("exito", "Usuario creado correctamente.");
            return "redirect:/usuarios";
        } catch (BusinessException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "No se pudo crear el usuario. Revise que el correo no esté registrado.");
        }
        return invalidar(form, errors, flash, "/usuarios/nuevo");
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("usuario") UsuarioEditarForm form,
                             BindingResult errors, RedirectAttributes flash) {
        if (errors.hasErrors()) return invalidar(form, errors, flash, "/usuarios/" + id + "/editar");
        try {
            usuarios.editar(id, form.toRequest());
            flash.addFlashAttribute("exito", "Usuario actualizado; sus roles quedaron guardados.");
            return "redirect:/usuarios";
        } catch (BusinessException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "No se pudo actualizar el usuario. Revise el correo y sus datos asociados.");
        }
        return invalidar(form, errors, flash, "/usuarios/" + id + "/editar");
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarios.eliminar(id); flash.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        } catch (BusinessException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "No se puede eliminar: el usuario conserva información financiera asociada.");
        }
        return "redirect:/usuarios";
    }

    private void opciones(Model model) {
        model.addAttribute("roles", roles.listar());
        model.addAttribute("monedas", monedas.findAll(Sort.by("codigo")));
        model.addAttribute("estados", Estado.values());
    }

    private String invalidar(Object form, BindingResult errors, RedirectAttributes flash, String destino) {
        FormSupport.flash(flash, "usuario", form, errors);
        return "redirect:" + destino;
    }
}
