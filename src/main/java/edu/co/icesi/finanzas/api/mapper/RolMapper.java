package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Rol;
import edu.co.icesi.finanzas.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RolMapper {
    @Mapping(target = "permisoIds", expression = "java(entity.getPermisos().stream().map(x -> x.getPermiso().getId()).collect(java.util.stream.Collectors.toSet()))")
    RolView entityToDto(Rol entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permisos", ignore = true)
    Rol dtoToEntity(RolRequest request);
}
