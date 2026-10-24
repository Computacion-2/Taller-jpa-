package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.entity.enums.Estado;
import edu.co.icesi.finanzas.exception.BusinessException;
import edu.co.icesi.finanzas.service.PermisoService;
import edu.co.icesi.finanzas.dto.form.PermisoForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/permisos") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISOS_GESTIONAR')")
public class PermisoController {
    private final PermisoService permisos;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("permisos", permisos.listar());
        return "permisos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("permiso")) model.addAttribute("permiso", new PermisoForm());
        model.addAttribute("estados", Estado.values()); model.addAttribute("creacion", true);
        return "permisos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var actual = permisos.obtener(id);
        if (!model.containsAttribute("permiso")) model.addAttribute("permiso", PermisoForm.from(actual));
        model.addAttribute("estados", Estado.values()); model.addAttribute("creacion", false); model.addAttribute("permisoId", id);
        return "permisos/formulario";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("permiso") PermisoForm form, BindingResult errors, RedirectAttributes flash) {
        return guardar(null, form, errors, flash);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("permiso") PermisoForm form,
                             BindingResult errors, RedirectAttributes flash) {
        return guardar(id, form, errors, flash);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            permisos.eliminar(id); flash.addFlashAttribute("exito", "Permiso eliminado correctamente.");
        } catch (BusinessException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error", "Retire el permiso de sus roles antes de eliminarlo.");
        }
        return "redirect:/permisos";
    }

    private String guardar(Long id, PermisoForm form, BindingResult errors, RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                if (id == null) permisos.crear(form.toRequest()); else permisos.actualizar(id, form.toRequest());
                flash.addFlashAttribute("exito", id == null ? "Permiso creado correctamente." : "Permiso actualizado correctamente.");
                return "redirect:/permisos";
            } catch (BusinessException ex) {
                flash.addFlashAttribute("error", ex.getMessage());
            } catch (DataIntegrityViolationException ex) {
                flash.addFlashAttribute("error", "No se pudo guardar el permiso. Revise que el nombre no esté registrado.");
            }
        }
        FormSupport.flash(flash, "permiso", form, errors);
        return id == null ? "redirect:/permisos/nuevo" : "redirect:/permisos/" + id + "/editar";
    }
}
