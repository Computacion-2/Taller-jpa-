import { http } from './http';
import { createCrudService } from './crudService';

// ---- Autenticacion y perfil -------------------------------------------------------------
export const authService = {
  login: (correo, password) => http.post('/auth/login', { correo, password }).then((r) => r.data),
  register: (body) => http.post('/auth/registro', body).then((r) => r.data),
  me: () => http.get('/me').then((r) => r.data),
  changePassword: (actual, nueva) => http.put('/me/password', { actual, nueva }).then(() => undefined),
};

// ---- Catalogos --------------------------------------------------------------------------
export const monedasService = createCrudService('/monedas');
export const tiposCuentaService = createCrudService('/tipos-cuenta');

// ---- Finanzas personales ----------------------------------------------------------------
export const categoriasService = createCrudService('/categorias');
export const cuentasService = createCrudService('/cuentas');
export const movimientosService = createCrudService('/movimientos');
export const recurrentesService = createCrudService('/movimientos-recurrentes');
export const presupuestosService = createCrudService('/presupuestos');
export const deudasService = createCrudService('/deudas');
export const abonosService = createCrudService('/abonos-deuda');
export const planesService = createCrudService('/planes-ahorro');
export const aportesService = createCrudService('/aportes-plan');
export const invitacionesService = createCrudService('/invitaciones-plan');
export const participacionesService = createCrudService(
  '/participaciones-plan',
  (item) => `${item.planId}/${item.usuarioId}`,
);

// ---- Administracion (solo ROLE_ADMINISTRADOR) -------------------------------------------
export const permisosService = createCrudService('/admin/permisos');
export const rolesService = createCrudService('/admin/roles');
export const usuariosRolesService = createCrudService(
  '/admin/usuarios-roles',
  (item) => `${item.usuarioId}/${item.rolId}`,
);
export const rolesPermisosService = createCrudService(
  '/admin/roles-permisos',
  (item) => `${item.rolId}/${item.permisoId}`,
);

const usuariosBase = createCrudService('/admin/usuarios');
export const usuariosService = {
  ...usuariosBase,
  /** Devuelve un PageView { content, number, size, totalElements, totalPages }. */
  page: (params) => http.get('/admin/usuarios', { params }).then((r) => r.data),
  changeState: (id, estado) => http
    .patch(`/admin/usuarios/${id}/estado`, null, { params: { estado } })
    .then((r) => r.data),
  assignRole: (id, rolId) => http.post(`/admin/usuarios/${id}/roles/${rolId}`).then((r) => r.data),
  removeRole: (id, rolId) => http.delete(`/admin/usuarios/${id}/roles/${rolId}`).then(() => undefined),
  resetPassword: (id, nueva) => http.put(`/admin/usuarios/${id}/password`, { nueva }).then(() => undefined),
};
