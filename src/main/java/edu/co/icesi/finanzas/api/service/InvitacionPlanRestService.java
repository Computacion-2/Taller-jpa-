package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.InvitacionPlan;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.InvitacionPlanMapper;
import edu.co.icesi.finanzas.repo.InvitacionPlanRepository;
import org.springframework.stereotype.Service;

@Service
public class InvitacionPlanRestService extends CrudService<InvitacionPlan, Long, InvitacionPlanDto, InvitacionPlanRequest> {
    public InvitacionPlanRestService(InvitacionPlanRepository repository, InvitacionPlanMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, InvitacionPlan::getId, InvitacionPlan::setId,
            access, references, rules);
    }
}
