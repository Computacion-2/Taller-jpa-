package edu.co.icesi.finanzas.repo;
import edu.co.icesi.finanzas.entity.Usuario;
import edu.co.icesi.finanzas.entity.keys.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    @Query("select distinct u from Usuario u left join u.roles ur where " +
        "(:texto is null or lower(u.nombreCompleto) like lower(concat('%', :texto, '%')) or lower(u.correo) like lower(concat('%', :texto, '%'))) " +
        "and (:rolId is null or ur.rol.id = :rolId) and (:estado is null or u.estado = :estado)")
    org.springframework.data.domain.Page<Usuario> buscar(@Param("texto") String texto, @Param("rolId") Long rolId,
        @Param("estado") edu.co.icesi.finanzas.entity.enums.Estado estado, org.springframework.data.domain.Pageable pageable);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);
    @EntityGraph(attributePaths = {"roles", "roles.rol", "moneda"})
    @Query("select u from Usuario u where lower(u.correo) = lower(:correo)")
    Optional<Usuario> findForLogin(@Param("correo") String correo);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> findLockedById(@Param("id") Long id);
}
