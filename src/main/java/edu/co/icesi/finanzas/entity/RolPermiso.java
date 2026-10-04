package edu.co.icesi.finanzas.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.co.icesi.finanzas.entity.keys.RolPermisoId;
import edu.co.icesi.finanzas.entity.enums.*;
@Entity @Table(name = "rol_permiso") @Getter @Setter @NoArgsConstructor
public class RolPermiso {
    @EmbeddedId private RolPermisoId id;
    @MapsId("rolId") @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false) private Rol rol;
    @MapsId("permisoId") @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permiso_id", nullable = false) private Permiso permiso;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;
}
