package edu.co.icesi.finanzas.dto.form;

import edu.co.icesi.finanzas.dto.UsuarioCrear;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioCrearForm extends UsuarioForm {
    @NotBlank(message = "Escriba una contraseña")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String password;

    public UsuarioCrear toRequest() {
        return new UsuarioCrear(getNombreCompleto(), getCorreo(), password, getMonedaId(), getRolIds());
    }
}
