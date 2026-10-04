package edu.co.icesi.finanzas.repo;
import edu.co.icesi.finanzas.entity.Rol;
import edu.co.icesi.finanzas.entity.keys.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rol r where r.id = :id")
    Optional<Rol> findLockedById(@Param("id") Long id);
}
