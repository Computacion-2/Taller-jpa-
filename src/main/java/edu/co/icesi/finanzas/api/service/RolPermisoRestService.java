package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.RolPermiso;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.RolPermisoMapper;
import edu.co.icesi.finanzas.repo.RolPermisoRepository;
import org.springframework.stereotype.Service;

@Service
public class RolPermisoRestService extends CrudService<RolPermiso, RolPermisoId, RolPermisoDto, RolPermisoRequest> {
    public RolPermisoRestService(RolPermisoRepository repository, RolPermisoMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, RolPermiso::getId, RolPermiso::setId,
            access, references, rules);
    }
}
