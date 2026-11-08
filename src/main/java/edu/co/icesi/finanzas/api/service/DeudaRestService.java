package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.Deuda;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.DeudaMapper;
import edu.co.icesi.finanzas.repo.DeudaRepository;
import org.springframework.stereotype.Service;

@Service
public class DeudaRestService extends CrudService<Deuda, Long, DeudaDto, DeudaRequest> {
    public DeudaRestService(DeudaRepository repository, DeudaMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, Deuda::getId, Deuda::setId,
            access, references, rules);
    }
}
