package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Moneda;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MonedaMapper {

    MonedaDto entityToDto(Moneda entity);
    @Mapping(target = "id", ignore = true)
    Moneda dtoToEntity(MonedaRequest request);
}
