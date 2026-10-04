package edu.co.icesi.finanzas.entity.keys;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
@Embeddable @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class RolPermisoId implements Serializable {
    @Column(name = "rol_id") private Long rolId;
    @Column(name = "permiso_id") private Long permisoId;
}
