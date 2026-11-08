package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.MovimientoRecurrente;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MovimientoRecurrenteMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "cuentaId", source = "cuenta.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    MovimientoRecurrenteDto entityToDto(MovimientoRecurrente entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "cuenta.id", source = "cuentaId")
    @Mapping(target = "categoria.id", source = "categoriaId")
    MovimientoRecurrente dtoToEntity(MovimientoRecurrenteRequest request);
}
