package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Permiso;
import edu.co.icesi.finanzas.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PermisoMapper {

    PermisoView entityToDto(Permiso entity);
    @Mapping(target = "id", ignore = true)
    Permiso dtoToEntity(PermisoRequest request);
}
