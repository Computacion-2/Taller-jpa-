package edu.co.icesi.finanzas.dto.form;

import edu.co.icesi.finanzas.dto.UsuarioEditar;
import edu.co.icesi.finanzas.dto.UsuarioView;
import edu.co.icesi.finanzas.entity.enums.Estado;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.LinkedHashSet;

@Getter @Setter
public class UsuarioEditarForm extends UsuarioForm {
    @NotNull(message = "Seleccione un estado")
    private Estado estado;

    public static UsuarioEditarForm from(UsuarioView usuario) {
        UsuarioEditarForm form = new UsuarioEditarForm();
        form.setNombreCompleto(usuario.nombreCompleto());
        form.setCorreo(usuario.correo());
        form.setMonedaId(usuario.monedaId());
        form.setRolIds(new LinkedHashSet<>(usuario.rolIds()));
        form.setEstado(usuario.estado());
        return form;
    }

    public UsuarioEditar toRequest() {
        return new UsuarioEditar(getNombreCompleto(), getCorreo(), getMonedaId(), estado, getRolIds());
    }
}
