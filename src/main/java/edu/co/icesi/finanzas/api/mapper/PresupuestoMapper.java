package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Presupuesto;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PresupuestoMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    PresupuestoDto entityToDto(Presupuesto entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "fechaCreacion", ignore = true)
    Presupuesto dtoToEntity(PresupuestoRequest request);
}
