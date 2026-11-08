package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Categoria;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoriaMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    CategoriaDto entityToDto(Categoria entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "fechaCreacion", ignore = true)
    Categoria dtoToEntity(CategoriaRequest request);

    @AfterMapping
    default void optionalOwner(CategoriaRequest request, @MappingTarget Categoria entity) {
        if (request.usuarioId() == null) entity.setUsuario(null);
    }
}
