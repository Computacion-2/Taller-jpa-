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
@Table(name = "presupuesto", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "categoria_id", "anio", "mes"}))
@Getter @Setter @NoArgsConstructor
public class Presupuesto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presupuesto_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    @Column(name = "anio", nullable = false)
    private Integer anio;
    @Column(name = "mes", nullable = false)
    private Integer mes;
    @Column(name = "monto_limite", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoLimite;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;
}
