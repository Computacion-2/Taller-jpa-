package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.Cuenta;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.CuentaMapper;
import edu.co.icesi.finanzas.repo.CuentaRepository;
import org.springframework.stereotype.Service;

@Service
public class CuentaRestService extends CrudService<Cuenta, Long, CuentaDto, CuentaRequest> {
    public CuentaRestService(CuentaRepository repository, CuentaMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, Cuenta::getId, Cuenta::setId,
            access, references, rules);
    }
}
