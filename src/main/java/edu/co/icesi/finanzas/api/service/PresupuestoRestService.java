package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.Presupuesto;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.PresupuestoMapper;
import edu.co.icesi.finanzas.repo.PresupuestoRepository;
import org.springframework.stereotype.Service;

@Service
public class PresupuestoRestService extends CrudService<Presupuesto, Long, PresupuestoDto, PresupuestoRequest> {
    public PresupuestoRestService(PresupuestoRepository repository, PresupuestoMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, Presupuesto::getId, Presupuesto::setId,
            access, references, rules);
    }
}
