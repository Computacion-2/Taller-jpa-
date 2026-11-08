package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.AbonoDeuda;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AbonoDeudaMapper {
    @Mapping(target = "deudaId", source = "deuda.id")
    AbonoDeudaDto entityToDto(AbonoDeuda entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deuda.id", source = "deudaId")
    AbonoDeuda dtoToEntity(AbonoDeudaRequest request);
}
