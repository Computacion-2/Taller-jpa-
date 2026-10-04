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
@Table(name = "deuda")
@Getter @Setter @NoArgsConstructor
public class Deuda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deuda_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "contraparte_usuario_id", nullable = true)
    private Usuario contraparteUsuario;
    @Column(name = "contraparte_nombre", nullable = true, length = 150)
    private String contraparteNombre;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15)
    private TipoDeuda tipo;
    @Column(name = "monto_original", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoOriginal;
    @Column(name = "fecha_origen", nullable = false)
    private LocalDate fechaOrigen;
    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;
    @Column(name = "descripcion", nullable = true, length = 500)
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoDeuda estado;
    @Column(name = "saldo_pendiente", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoPendiente;
}
