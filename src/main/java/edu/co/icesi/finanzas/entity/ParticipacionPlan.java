package edu.co.icesi.finanzas.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import edu.co.icesi.finanzas.entity.keys.ParticipacionPlanId;
import edu.co.icesi.finanzas.entity.enums.*;
@Entity @Table(name = "participacion_plan") @Getter @Setter @NoArgsConstructor
public class ParticipacionPlan {
    @EmbeddedId private ParticipacionPlanId id;
    @MapsId("planId") @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false) private PlanAhorro plan;
    @MapsId("usuarioId") @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario;
    @Column(name = "meta_individual", precision = 15, scale = 2)
    private BigDecimal metaIndividual;
    @Column(name = "fecha_aceptacion", nullable = true)
    private LocalDate fechaAceptacion;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoParticipacion estado;
}
