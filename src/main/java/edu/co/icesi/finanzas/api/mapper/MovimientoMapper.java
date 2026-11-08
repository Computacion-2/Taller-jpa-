package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Movimiento;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MovimientoMapper {
    @Mapping(target = "cuentaId", source = "cuenta.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    MovimientoDto entityToDto(Movimiento entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuenta.id", source = "cuentaId")
    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "fechaCreacion", ignore = true)
    Movimiento dtoToEntity(MovimientoRequest request);
}
