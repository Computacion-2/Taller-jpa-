package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.AportePlan;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.AportePlanMapper;
import edu.co.icesi.finanzas.repo.AportePlanRepository;
import org.springframework.stereotype.Service;

@Service
public class AportePlanRestService extends CrudService<AportePlan, Long, AportePlanDto, AportePlanRequest> {
    public AportePlanRestService(AportePlanRepository repository, AportePlanMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, AportePlan::getId, AportePlan::setId,
            access, references, rules);
    }
}
