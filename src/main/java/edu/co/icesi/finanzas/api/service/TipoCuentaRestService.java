package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.TipoCuenta;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.TipoCuentaMapper;
import edu.co.icesi.finanzas.repo.TipoCuentaRepository;
import org.springframework.stereotype.Service;

@Service
public class TipoCuentaRestService extends CrudService<TipoCuenta, Long, TipoCuentaDto, TipoCuentaRequest> {
    public TipoCuentaRestService(TipoCuentaRepository repository, TipoCuentaMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, TipoCuenta::getId, TipoCuenta::setId,
            access, references, rules);
    }
}
