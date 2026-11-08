package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.AportePlan;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AportePlanMapper {
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "cuentaId", source = "cuenta.id")
    AportePlanDto entityToDto(AportePlan entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan.id", source = "planId")
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "cuenta.id", source = "cuentaId")
    @Mapping(target = "fechaCreacion", ignore = true)
    AportePlan dtoToEntity(AportePlanRequest request);
}
