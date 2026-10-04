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
@Table(name = "aporte_plan")
@Getter @Setter @NoArgsConstructor
public class AportePlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aporte_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanAhorro plan;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;
    @Column(name = "fecha_aporte", nullable = false)
    private LocalDate fechaAporte;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAporte estado;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;
}
