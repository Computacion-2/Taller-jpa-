package edu.co.icesi.finanzas.repo;
import edu.co.icesi.finanzas.entity.ParticipacionPlan;
import edu.co.icesi.finanzas.entity.keys.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface ParticipacionPlanRepository extends JpaRepository<ParticipacionPlan, ParticipacionPlanId> {
}
