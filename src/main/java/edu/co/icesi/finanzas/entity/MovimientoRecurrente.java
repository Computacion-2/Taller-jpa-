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
@Table(name = "movimiento_recurrente")
@Getter @Setter @NoArgsConstructor
public class MovimientoRecurrente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurrente_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoMovimiento tipo;
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;
    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidad", nullable = false, length = 15)
    private Periodicidad periodicidad;
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    @Column(name = "fecha_corte", nullable = true)
    private LocalDate fechaCorte;
    @Column(name = "proxima_ocurrencia", nullable = false)
    private LocalDate proximaOcurrencia;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoRecurrente estado;
    @Column(name = "descripcion", nullable = true, length = 500)
    private String descripcion;
}
