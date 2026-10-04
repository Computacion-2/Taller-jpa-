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
@Table(name = "plan_ahorro")
@Getter @Setter @NoArgsConstructor
public class PlanAhorro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
    @Column(name = "descripcion", nullable = true, length = 500)
    private String descripcion;
    @Column(name = "monto_objetivo", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoObjetivo;
    @Column(name = "fecha_objetivo", nullable = false)
    private LocalDate fechaObjetivo;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoPlan tipo;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPlan estado;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;
}
