package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.TipoCuenta;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TipoCuentaMapper {

    TipoCuentaDto entityToDto(TipoCuenta entity);
    @Mapping(target = "id", ignore = true)
    TipoCuenta dtoToEntity(TipoCuentaRequest request);
}
