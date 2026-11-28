import { StatCard } from '../ui/Layout';
import { Alert } from '../ui/Feedback';
import { useFetch } from '../../hooks/useFetch';
import { getErrorMessage } from '../../api/errors';
import { monedasService, permisosService, rolesService, tiposCuentaService, usuariosService } from '../../api/services';

function loadAdminStats() {
  return Promise.all([
    usuariosService.page({ size: 1 }),
    usuariosService.page({ size: 1, estado: 'ACTIVO' }),
    rolesService.list(),
    permisosService.list(),
    monedasService.list(),
    tiposCuentaService.list(),
  ]).then(([usuarios, activos, roles, permisos, monedas, tipos]) => ({
    usuarios: usuarios.totalElements,
    activos: activos.totalElements,
    roles: roles.length,
    permisos: permisos.length,
    monedas: monedas.length,
    tipos: tipos.length,
  }));
}

/** Indicadores del modulo administrativo (solo ROLE_ADMINISTRADOR). */
export default function AdminOverview() {
  const { data, loading, error } = useFetch(loadAdminStats);
  const value = (key) => (loading || !data ? '…' : data[key]);

  if (error) {
    return <Alert tone="danger">{getErrorMessage(error)}</Alert>;
  }
  return (
    <div className="stats-grid">
      <StatCard label="Usuarios registrados" value={value('usuarios')} hint={data ? `${data.activos} activos` : undefined} icon="users" to="/admin/usuarios" />
      <StatCard label="Roles" value={value('roles')} icon="shield" tone="info" to="/admin/roles" />
      <StatCard label="Permisos" value={value('permisos')} icon="key" tone="warning" to="/admin/permisos" />
      <StatCard label="Monedas / tipos de cuenta" value={data ? `${data.monedas} / ${data.tipos}` : '…'} icon="coin" tone="success" to="/admin/monedas" />
    </div>
  );
}
