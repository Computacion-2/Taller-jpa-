package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.Deuda;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeudaMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "contraparteUsuarioId", source = "contraparteUsuario.id")
    DeudaDto entityToDto(Deuda entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "contraparteUsuario.id", source = "contraparteUsuarioId")
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "saldoPendiente", ignore = true)
    Deuda dtoToEntity(DeudaRequest request);

    @AfterMapping
    default void optionalCounterparty(DeudaRequest request, @MappingTarget Deuda entity) {
        if (request.contraparteUsuarioId() == null) entity.setContraparteUsuario(null);
    }
}
