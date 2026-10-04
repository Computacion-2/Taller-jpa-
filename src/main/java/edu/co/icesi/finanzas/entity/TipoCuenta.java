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
@Table(name = "tipo_cuenta", uniqueConstraints = @UniqueConstraint(columnNames = {"nombre"}))
@Getter @Setter @NoArgsConstructor
public class TipoCuenta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_cuenta_id")
    private Long id;
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    @Column(name = "descripcion", nullable = true, length = 200)
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;
}
