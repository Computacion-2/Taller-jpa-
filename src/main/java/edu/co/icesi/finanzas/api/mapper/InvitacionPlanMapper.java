package edu.co.icesi.finanzas.api.mapper;
import org.mapstruct.*;
import edu.co.icesi.finanzas.entity.InvitacionPlan;
import edu.co.icesi.finanzas.api.dto.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InvitacionPlanMapper {
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "usuarioInvitadoId", source = "usuarioInvitado.id")
    @Mapping(target = "usuarioInvitadorId", source = "usuarioInvitador.id")
    InvitacionPlanDto entityToDto(InvitacionPlan entity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan.id", source = "planId")
    @Mapping(target = "usuarioInvitado.id", source = "usuarioInvitadoId")
    @Mapping(target = "usuarioInvitador", ignore = true)
    @Mapping(target = "fechaInvitacion", ignore = true)
    @Mapping(target = "fechaRespuesta", ignore = true)
    InvitacionPlan dtoToEntity(InvitacionPlanRequest request);
}
