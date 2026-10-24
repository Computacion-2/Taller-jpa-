package edu.co.icesi.finanzas.controller;

import edu.co.icesi.finanzas.dto.form.UsuarioCrearForm;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class FormSupport {
    private FormSupport() { }

    /** Rebuild validation state after clearing credentials, including rejected values. */
    public static void flash(RedirectAttributes flash, String name, Object form, BindingResult original) {
        if (form instanceof UsuarioCrearForm usuario) usuario.setPassword(null);
        BeanPropertyBindingResult safe = new BeanPropertyBindingResult(form, name);
        original.getAllErrors().forEach(error -> {
            if (error instanceof FieldError field) {
                safe.addError(new FieldError(name, field.getField(),
                    "password".equals(field.getField()) ? null : field.getRejectedValue(),
                    field.isBindingFailure(), field.getCodes(), field.getArguments(),
                    field.isBindingFailure() ? "Seleccione un valor válido" : field.getDefaultMessage()));
            } else safe.addError(error);
        });
        flash.addFlashAttribute(name, form);
        flash.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + name, safe);
    }
}
