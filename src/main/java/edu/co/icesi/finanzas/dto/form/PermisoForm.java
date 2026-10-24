package edu.co.icesi.finanzas.dto.form;

import edu.co.icesi.finanzas.dto.PermisoRequest;
import edu.co.icesi.finanzas.dto.PermisoView;
import edu.co.icesi.finanzas.entity.enums.Estado;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PermisoForm {
    @NotBlank(message = "Escriba el nombre del permiso")
    @Size(max = 100, message = "El nombre admite hasta 100 caracteres")
    private String nombre;
    @Size(max = 250, message = "La descripción admite hasta 250 caracteres")
    private String descripcion;
    @NotNull(message = "Seleccione un estado")
    private Estado estado = Estado.ACTIVO;

    public static PermisoForm from(PermisoView permiso) {
        PermisoForm form = new PermisoForm();
        form.setNombre(permiso.nombre()); form.setDescripcion(permiso.descripcion());
        form.setEstado(permiso.estado());
        return form;
    }

    public PermisoRequest toRequest() { return new PermisoRequest(nombre, descripcion, estado); }
}
