package edu.co.icesi.finanzas.dto;
import edu.co.icesi.finanzas.entity.*;
import edu.co.icesi.finanzas.api.mapper.*;
import org.mapstruct.factory.Mappers;

public final class Views {
    public static final PermisoMapper PERMISOS = Mappers.getMapper(PermisoMapper.class);
    public static final RolMapper ROLES = Mappers.getMapper(RolMapper.class);
    public static final UsuarioMapper USUARIOS = Mappers.getMapper(UsuarioMapper.class);
    private Views() {}
    public static PermisoView permiso(Permiso entity) { return PERMISOS.entityToDto(entity); }
    public static RolView rol(Rol entity) { return ROLES.entityToDto(entity); }
    public static UsuarioView usuario(Usuario entity) { return USUARIOS.entityToDto(entity); }
}
