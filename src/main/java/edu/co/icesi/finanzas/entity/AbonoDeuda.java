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
@Table(name = "abono_deuda")
@Getter @Setter @NoArgsConstructor
public class AbonoDeuda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "abono_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deuda_id", nullable = false)
    private Deuda deuda;
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;
    @Column(name = "fecha_abono", nullable = false)
    private LocalDate fechaAbono;
    @Column(name = "descripcion", nullable = true, length = 300)
    private String descripcion;
}
