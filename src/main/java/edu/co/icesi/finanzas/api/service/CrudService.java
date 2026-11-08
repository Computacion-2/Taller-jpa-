package edu.co.icesi.finanzas.api.service;

import java.util.List;
import java.util.Objects;
import java.util.function.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import static edu.co.icesi.finanzas.exception.BusinessException.*;

@Transactional(readOnly = true)
public abstract class CrudService<E, K, D, R> {
    protected final JpaRepository<E, K> repository;
    private final Function<E, D> toDto;
    private final Function<R, E> toEntity;
    private final Function<E, K> getId;
    private final BiConsumer<E, K> setId;
    protected final RestAccess access;
    private final RestReferences references;
    private final ReglasFinancieras rules;

    protected CrudService(JpaRepository<E, K> repository, Function<E, D> toDto,
            Function<R, E> toEntity, Function<E, K> getId, BiConsumer<E, K> setId,
            RestAccess access, RestReferences references, ReglasFinancieras rules) {
        this.repository = repository;
        this.toDto = toDto;
        this.toEntity = toEntity;
        this.getId = getId;
        this.setId = setId;
        this.access = access;
        this.references = references;
        this.rules = rules;
    }

    public List<D> listar() {
        return repository.findAll().stream().filter(access::canRead).map(toDto).toList();
    }

    public D obtener(K id) {
        E entity = find(id);
        access.requireRead(entity);
        return toDto.apply(entity);
    }

    @Transactional
    public D crear(R request) {
        E entity = toEntity.apply(request);
        references.resolve(entity);
        access.requireWrite(entity);
        if (getId.apply(entity) != null && repository.existsById(getId.apply(entity))) throw conflict("Asociacion duplicada");
        rules.prepare(entity, null);
        return toDto.apply(repository.saveAndFlush(entity));
    }

    @Transactional
    public D actualizar(K id, R request) {
        E old = find(id);
        access.requireRead(old);
        access.requireWrite(old);
        references.lock(old);
        E entity = toEntity.apply(request);
        references.resolve(entity);
        if (getId.apply(entity) != null && !Objects.equals(id, getId.apply(entity))) throw invalid("La clave de la ruta y del cuerpo debe coincidir");
        setId.accept(entity, id);
        access.requireWrite(entity);
        rules.prepare(entity, old);
        return toDto.apply(repository.saveAndFlush(entity));
    }

    @Transactional
    public void eliminar(K id) {
        E entity = find(id);
        access.requireRead(entity);
        access.requireWrite(entity);
        references.lock(entity);
        rules.beforeDelete(entity);
        repository.delete(entity);
        repository.flush();
    }

    protected E find(K id) { return repository.findById(id).orElseThrow(() -> missing("Recurso")); }
}
