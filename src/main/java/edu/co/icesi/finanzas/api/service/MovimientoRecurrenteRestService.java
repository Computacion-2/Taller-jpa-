package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.MovimientoRecurrente;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.MovimientoRecurrenteMapper;
import edu.co.icesi.finanzas.repo.MovimientoRecurrenteRepository;
import org.springframework.stereotype.Service;

@Service
public class MovimientoRecurrenteRestService extends CrudService<MovimientoRecurrente, Long, MovimientoRecurrenteDto, MovimientoRecurrenteRequest> {
    public MovimientoRecurrenteRestService(MovimientoRecurrenteRepository repository, MovimientoRecurrenteMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, MovimientoRecurrente::getId, MovimientoRecurrente::setId,
            access, references, rules);
    }
}
