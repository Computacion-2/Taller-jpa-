package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.exception.BusinessException;
import edu.co.icesi.finanzas.service.PermisoService;
import edu.co.icesi.finanzas.service.RolService;
import edu.co.icesi.finanzas.dto.form.RolForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/roles") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('ROLES_GESTIONAR')")
public class RolController {
    private final RolService roles;
    private final PermisoService permisos;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roles", roles.listar());
        model.addAttribute("permisos", permisos.listar());
        return "roles/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("rol")) model.addAttribute("rol", new RolForm());
        opciones(model); model.addAttribute("creacion", true);
        return "roles/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var actual = roles.obtener(id);
        if (!model.containsAttribute("rol")) model.addAttribute("rol", RolForm.from(actual));
        opciones(model); model.addAttribute("creacion", false); model.addAttribute("rolId", id);
        model.addAttribute("rolBase", "ADMINISTRADOR".equals(actual.nombre()) || "USUARIO".equals(actual.nombre()));
        return "roles/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("rol") RolForm form, BindingResult errors, RedirectAttributes flash) {
        return guardar(null, form, errors, flash);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("rol") RolForm form,
                             BindingResult errors, RedirectAttributes flash) {
        return guardar(id, form, errors, flash);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            roles.eliminar(id); flash.addFlashAttribute("exito", "Rol eliminado correctamente.");
        } catch (BusinessException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "Retire el rol de sus usuarios antes de eliminarlo.");
        }
        return "redirect:/roles";
    }

    private String guardar(Long id, RolForm form, BindingResult errors, RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                if (id == null) roles.crear(form.toRequest()); else roles.actualizar(id, form.toRequest());
                flash.addFlashAttribute("exito", id == null ? "Rol creado correctamente." : "Rol actualizado; sus permisos quedaron guardados.");
                return "redirect:/roles";
            } catch (BusinessException ex) {
                flash.addFlashAttribute("error", ex.getMessage());
            } catch (DataIntegrityViolationException ex) {
                flash.addFlashAttribute("error", "No se pudo guardar el rol. Revise que el nombre no esté registrado.");
            }
        }
        FormSupport.flash(flash, "rol", form, errors);
        return id == null ? "redirect:/roles/nuevo" : "redirect:/roles/" + id + "/editar";
    }

    private void opciones(Model model) {
        model.addAttribute("permisos", permisos.listar()); model.addAttribute("estados", Estado.values());
    }
}
