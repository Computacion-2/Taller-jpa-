package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.Moneda;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.MonedaMapper;
import edu.co.icesi.finanzas.repo.MonedaRepository;
import org.springframework.stereotype.Service;

@Service
public class MonedaRestService extends CrudService<Moneda, Long, MonedaDto, MonedaRequest> {
    public MonedaRestService(MonedaRepository repository, MonedaMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, Moneda::getId, Moneda::setId,
            access, references, rules);
    }
}
