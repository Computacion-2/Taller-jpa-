package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Cuenta;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CuentaMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "tipoCuentaId", source = "tipoCuenta.id")
    CuentaDto entityToDto(Cuenta entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "tipoCuenta.id", source = "tipoCuentaId")
    @Mapping(target = "saldoActual", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaArchivado", ignore = true)
    Cuenta dtoToEntity(CuentaRequest request);
}
