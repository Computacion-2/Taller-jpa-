package edu.co.icesi.finanzas.dto.form;

import edu.co.icesi.finanzas.dto.RolRequest;
import edu.co.icesi.finanzas.dto.RolView;
import edu.co.icesi.finanzas.entity.enums.Estado;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter @Setter
public class RolForm {
    @NotBlank(message = "Escriba el nombre del rol")
    @Size(max = 50, message = "El nombre admite hasta 50 caracteres")
    private String nombre;
    @Size(max = 200, message = "La descripción admite hasta 200 caracteres")
    private String descripcion;
    @NotNull(message = "Seleccione un estado")
    private Estado estado = Estado.ACTIVO;
    @NotEmpty(message = "Seleccione al menos un permiso")
    private Set<@NotNull @Positive Long> permisoIds = new LinkedHashSet<>();

    public static RolForm from(RolView rol) {
        RolForm form = new RolForm();
        form.setNombre(rol.nombre()); form.setDescripcion(rol.descripcion());
        form.setEstado(rol.estado()); form.setPermisoIds(new LinkedHashSet<>(rol.permisoIds()));
        return form;
    }

    public RolRequest toRequest() { return new RolRequest(nombre, descripcion, estado, permisoIds); }
}
