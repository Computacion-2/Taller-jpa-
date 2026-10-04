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
@Table(name = "movimiento")
@Getter @Setter @NoArgsConstructor
public class Movimiento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private Long id;
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
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDate fechaMovimiento;
    @Column(name = "descripcion", nullable = true, length = 500)
    private String descripcion;
    @Column(name = "soporte_url", nullable = true, length = 500)
    private String soporteUrl;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;
}
