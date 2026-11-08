package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.PlanAhorro;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PlanAhorroMapper {
    @Mapping(target = "propietarioId", source = "propietario.id")
    PlanAhorroDto entityToDto(PlanAhorro entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "propietario.id", source = "propietarioId")
    @Mapping(target = "fechaCreacion", ignore = true)
    PlanAhorro dtoToEntity(PlanAhorroRequest request);
}
