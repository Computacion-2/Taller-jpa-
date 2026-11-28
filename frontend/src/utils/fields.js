// Fabricas de campos comunes para los formularios declarativos de CrudPage.
import { monthName } from './format';

/**
 * Campo de propietario (usuarioId, propietarioId...). El administrador elige entre todos los
 * usuarios; un usuario normal no lo ve y se envia su propio id (valor por defecto).
 */
export function ownerField(users, name = 'usuarioId', label = 'Usuario') {
  return {
    name,
    label,
    type: 'select',
    numeric: true,
    required: true,
    options: users.options,
    hidden: !users.isAdmin,
    lockOnEdit: true,
  };
}

/** Opciones de un catalogo filtradas por estado activo. */
export function activeOptions(items, labelOf = (item) => item.nombre, activeValue = 'ACTIVO') {
  return items
    .filter((item) => item.estado === activeValue)
    .map((item) => ({ value: String(item.id), label: labelOf(item) }));
}

/** Categorias validas para un tipo de movimiento y un usuario (compartidas o propias). */
export function categoryOptions(categorias, tipo, usuarioId) {
  return categorias
    .filter((categoria) => categoria.estado === 'ACTIVO'
      && (!tipo || categoria.tipo === tipo)
      && (categoria.usuarioId === null || !usuarioId || categoria.usuarioId === Number(usuarioId)))
    .map((categoria) => ({
      value: String(categoria.id),
      label: categoria.usuarioId === null ? `${categoria.nombre} (compartida)` : categoria.nombre,
    }));
}

/** Cuentas activas de un usuario. */
export function accountOptions(cuentas, usuarioId) {
  return cuentas
    .filter((cuenta) => cuenta.estado === 'ACTIVA' && (!usuarioId || cuenta.usuarioId === Number(usuarioId)))
    .map((cuenta) => ({ value: String(cuenta.id), label: cuenta.nombre }));
}

export const MONTH_OPTIONS = Array.from({ length: 12 }, (_, index) => ({
  value: String(index + 1),
  label: monthName(index + 1).replace(/^./, (letter) => letter.toUpperCase()),
}));

/** Regla de fechas: "hasta" no puede ser anterior a "desde". */
export function dateOrderError(from, to, message) {
  return from && to && to < from ? message : null;
}
