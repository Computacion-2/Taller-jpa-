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
@Table(name = "moneda", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo"}))
@Getter @Setter @NoArgsConstructor
public class Moneda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moneda_id")
    private Long id;
    @Column(name = "codigo", nullable = false, length = 3)
    private String codigo;
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    @Column(name = "simbolo", nullable = true, length = 5)
    private String simbolo;
}
