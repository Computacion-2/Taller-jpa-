package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.RolPermiso;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RolPermisoMapper {
    @Mapping(target = "rolId", source = "rol.id")
    @Mapping(target = "permisoId", source = "permiso.id")
    RolPermisoDto entityToDto(RolPermiso entity);
    @Mapping(target = "rol.id", source = "rolId")
    @Mapping(target = "permiso.id", source = "permisoId")
    @Mapping(target = "id", ignore = true)
    RolPermiso dtoToEntity(RolPermisoRequest request);
}
