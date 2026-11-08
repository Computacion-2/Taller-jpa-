package edu.co.icesi.finanzas.api.service;
import edu.co.icesi.finanzas.entity.Categoria;
import edu.co.icesi.finanzas.entity.keys.*;
import edu.co.icesi.finanzas.api.dto.*;
import edu.co.icesi.finanzas.api.mapper.CategoriaMapper;
import edu.co.icesi.finanzas.repo.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaRestService extends CrudService<Categoria, Long, CategoriaDto, CategoriaRequest> {
    public CategoriaRestService(CategoriaRepository repository, CategoriaMapper mapper,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        super(repository, mapper::entityToDto, mapper::dtoToEntity, Categoria::getId, Categoria::setId,
            access, references, rules);
    }
}
