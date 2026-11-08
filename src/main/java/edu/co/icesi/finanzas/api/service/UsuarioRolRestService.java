package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.UsuarioRol;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.UsuarioRolMapper;
import edu.co.icesi.finanzas.repo.UsuarioRolRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioRolRestService extends CrudService<UsuarioRol, UsuarioRolId, UsuarioRolDto, UsuarioRolRequest> {
    public UsuarioRolRestService(UsuarioRolRepository repository, UsuarioRolMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, UsuarioRol::getId, UsuarioRol::setId,
            access, references, rules);
    }
}
