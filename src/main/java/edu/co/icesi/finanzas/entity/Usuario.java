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
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = {"correo"}))
@Getter @Setter @NoArgsConstructor
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;
    @Column(name = "correo", nullable = false, length = 150)
    private String correo;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(name = "foto_url", nullable = true, length = 500)
    private String fotoUrl;
    @Column(name = "programa_academico", nullable = true, length = 150)
    private String programaAcademico;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado;
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;
    @Column(name = "fecha_actualizacion", nullable = true)
    private LocalDate fechaActualizacion;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioRol> roles = new ArrayList<>();
}
