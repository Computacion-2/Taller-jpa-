package edu.co.icesi.finanzas.repo;
import edu.co.icesi.finanzas.entity.RolPermiso;
import edu.co.icesi.finanzas.entity.keys.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {
    boolean existsByPermiso_Id(Long id);
}
