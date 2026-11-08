package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.ParticipacionPlan;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ParticipacionPlanMapper {
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    ParticipacionPlanDto entityToDto(ParticipacionPlan entity);
    @Mapping(target = "plan.id", source = "planId")
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "id", ignore = true)
    ParticipacionPlan dtoToEntity(ParticipacionPlanRequest request);
}
