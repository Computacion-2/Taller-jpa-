package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.UsuarioRol;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UsuarioRolMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "rolId", source = "rol.id")
    UsuarioRolDto entityToDto(UsuarioRol entity);
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "rol.id", source = "rolId")
    @Mapping(target = "id", ignore = true)
    UsuarioRol dtoToEntity(UsuarioRolRequest request);
}
