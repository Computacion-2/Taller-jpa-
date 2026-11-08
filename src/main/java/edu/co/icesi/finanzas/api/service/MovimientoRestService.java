package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.Movimiento;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.MovimientoMapper;
import edu.co.icesi.finanzas.repo.MovimientoRepository;
import org.springframework.stereotype.Service;

@Service
public class MovimientoRestService extends CrudService<Movimiento, Long, MovimientoDto, MovimientoRequest> {
    public MovimientoRestService(MovimientoRepository repository, MovimientoMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, Movimiento::getId, Movimiento::setId,
            access, references, rules);
    }
}
