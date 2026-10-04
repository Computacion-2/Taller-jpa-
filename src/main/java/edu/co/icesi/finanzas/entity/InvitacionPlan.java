package edu.co.icesi.finanzas.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import edu.co.icesi.finanzas.entity.enums.*;
@Entity
@Table(name = "invitacion_plan")
@Getter @Setter @NoArgsConstructor
public class InvitacionPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitacion_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanAhorro plan;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_invitado_id", nullable = false)
    private Usuario usuarioInvitado;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_invitador_id", nullable = false)
    private Usuario usuarioInvitador;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoInvitacion estado;
    @Column(name = "fecha_invitacion", nullable = false)
    private LocalDate fechaInvitacion;
    @Column(name = "fecha_respuesta", nullable = true)
    private LocalDate fechaRespuesta;
}
