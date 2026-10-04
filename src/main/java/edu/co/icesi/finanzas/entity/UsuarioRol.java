package edu.co.icesi.finanzas.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.co.icesi.finanzas.entity.keys.UsuarioRolId;
import edu.co.icesi.finanzas.entity.enums.*;
@Entity @Table(name = "usuario_rol") @Getter @Setter @NoArgsConstructor
public class UsuarioRol {
    @EmbeddedId private UsuarioRolId id;
    @MapsId("usuarioId") @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario;
    @MapsId("rolId") @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false) private Rol rol;
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;
}
