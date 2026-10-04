package edu.co.icesi.finanzas.entity.keys;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
@Embeddable @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class ParticipacionPlanId implements Serializable {
    @Column(name = "plan_id") private Long planId;
    @Column(name = "usuario_id") private Long usuarioId;
}
