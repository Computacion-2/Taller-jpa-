import { sum, todayISO } from './format';

/** "2026-11" del mes actual. */
export function currentMonthKey() {
  return todayISO().slice(0, 7);
}

/** Movimientos de un mes (yyyy-MM). */
export function movementsOfMonth(movimientos, monthKey = currentMonthKey()) {
  return movimientos.filter((movimiento) => movimiento.fechaMovimiento?.startsWith(monthKey));
}

export function totalByType(movimientos, tipo) {
  return sum(movimientos.filter((movimiento) => movimiento.tipo === tipo), (movimiento) => movimiento.monto);
}

/**
 * Gasto acumulado de un presupuesto: gastos de su categoria, en su mes y año,
 * registrados en cuentas del mismo usuario.
 */
export function spentOnBudget(presupuesto, movimientos, cuentasById) {
  const monthKey = `${presupuesto.anio}-${String(presupuesto.mes).padStart(2, '0')}`;
  return sum(
    movimientos.filter((movimiento) => movimiento.tipo === 'GASTO'
      && movimiento.categoriaId === presupuesto.categoriaId
      && movimiento.fechaMovimiento?.startsWith(monthKey)
      && cuentasById.get(movimiento.cuentaId)?.usuarioId === presupuesto.usuarioId),
    (movimiento) => movimiento.monto,
  );
}

/** Total ahorrado en un plan (aportes activos). */
export function savedOnPlan(planId, aportes) {
  return sum(
    aportes.filter((aporte) => aporte.planId === planId && aporte.estado === 'ACTIVO'),
    (aporte) => aporte.monto,
  );
}

/** Nombre visible de la contraparte de una deuda. */
export function counterpart(deuda) {
  return deuda.contraparteNombre || `Usuario #${deuda.contraparteUsuarioId}`;
}

/** Orden descendente por fecha y luego por id (lo mas reciente primero). */
export function byDateDesc(dateKey) {
  return (a, b) => (b[dateKey] ?? '').localeCompare(a[dateKey] ?? '') || b.id - a.id;
}
