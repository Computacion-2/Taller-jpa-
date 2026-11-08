package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.PlanAhorro;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.PlanAhorroMapper;
import edu.co.icesi.finanzas.repo.PlanAhorroRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanAhorroRestService extends CrudService<PlanAhorro, Long, PlanAhorroDto, PlanAhorroRequest> {
    public PlanAhorroRestService(PlanAhorroRepository repository, PlanAhorroMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, PlanAhorro::getId, PlanAhorro::setId,
            access, references, rules);
    }
}
