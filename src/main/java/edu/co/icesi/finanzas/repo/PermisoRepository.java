package edu.co.icesi.finanzas.repo;
import edu.co.icesi.finanzas.entity.Permiso;
import edu.co.icesi.finanzas.entity.keys.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Permiso p where p.id = :id")
    Optional<Permiso> findLockedById(@Param("id") Long id);
}
