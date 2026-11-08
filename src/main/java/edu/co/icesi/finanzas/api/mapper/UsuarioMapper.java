package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Usuario;
import edu.co.icesi.finanzas.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UsuarioMapper {
    @Mapping(target = "monedaId", source = "moneda.id")
    @Mapping(target = "rolIds", expression = "java(entity.getRoles().stream().map(x -> x.getRol().getId()).collect(java.util.stream.Collectors.toSet()))")
    UsuarioView entityToDto(Usuario entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "moneda", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fotoUrl", ignore = true)
    @Mapping(target = "programaAcademico", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Usuario dtoToEntity(UsuarioCrear request);
}
