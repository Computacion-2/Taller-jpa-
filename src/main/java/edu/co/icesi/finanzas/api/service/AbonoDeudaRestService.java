package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.AbonoDeuda;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.AbonoDeudaMapper;
import edu.co.icesi.finanzas.repo.AbonoDeudaRepository;
import org.springframework.stereotype.Service;

@Service
public class AbonoDeudaRestService extends CrudService<AbonoDeuda, Long, AbonoDeudaDto, AbonoDeudaRequest> {
    public AbonoDeudaRestService(AbonoDeudaRepository repository, AbonoDeudaMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, AbonoDeuda::getId, AbonoDeuda::setId,
            access, references, rules);
    }
}
