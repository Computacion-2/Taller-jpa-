package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.ParticipacionPlan;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.ParticipacionPlanMapper;
import edu.co.icesi.finanzas.repo.ParticipacionPlanRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipacionPlanRestService extends CrudService<ParticipacionPlan, ParticipacionPlanId, ParticipacionPlanDto, ParticipacionPlanRequest> {
    public ParticipacionPlanRestService(ParticipacionPlanRepository repository, ParticipacionPlanMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, ParticipacionPlan::getId, ParticipacionPlan::setId,
            access, references, rules);
    }
}
