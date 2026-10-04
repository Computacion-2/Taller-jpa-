package edu.co.icesi.finanzas.entity.keys;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
@Embeddable @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class UsuarioRolId implements Serializable {
    @Column(name = "usuario_id") private Long usuarioId;
    @Column(name = "rol_id") private Long rolId;
}
