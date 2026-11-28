import { useMemo } from 'react';
import Button, { ButtonLink } from '../ui/Button';
import { Card, StatCard } from '../ui/Layout';
import { Alert, EmptyState, EnumBadge, ProgressBar } from '../ui/Feedback';
import Spinner from '../ui/Spinner';
import { useFetch } from '../../hooks/useFetch';
import { getErrorMessage } from '../../api/errors';
import {
  categoriasService, cuentasService, deudasService, movimientosService, presupuestosService, recurrentesService,
} from '../../api/services';
import { formatDate, formatMoney, formatPeriod, sum } from '../../utils/format';
import { byDateDesc, currentMonthKey, movementsOfMonth, spentOnBudget, totalByType } from '../../utils/finance';

function loadFinances() {
  return Promise.all([
    cuentasService.list(), movimientosService.list(), presupuestosService.list(),
    deudasService.list(), categoriasService.list(), recurrentesService.list(),
  ]).then(([cuentas, movimientos, presupuestos, deudas, categorias, recurrentes]) => ({
    cuentas, movimientos, presupuestos, deudas, categorias, recurrentes,
  }));
}

function summarize(data) {
  const cuentasById = new Map(data.cuentas.map((cuenta) => [cuenta.id, cuenta]));
  const categoriasById = new Map(data.categorias.map((categoria) => [categoria.id, categoria]));
  const month = movementsOfMonth(data.movimientos);
  const [year, monthNumber] = currentMonthKey().split('-').map(Number);
  const pendientes = data.deudas.filter((deuda) => deuda.estado === 'PENDIENTE');
  return {
    cuentasById,
    categoriaName: (id) => categoriasById.get(id)?.nombre ?? `#${id}`,
    saldo: sum(data.cuentas.filter((cuenta) => cuenta.estado === 'ACTIVA'), (cuenta) => cuenta.saldoActual),
    ingresos: totalByType(month, 'INGRESO'),
    gastos: totalByType(month, 'GASTO'),
    porPagar: sum(pendientes.filter((deuda) => deuda.tipo === 'POR_PAGAR'), (deuda) => deuda.saldoPendiente),
    recientes: [...data.movimientos].sort(byDateDesc('fechaMovimiento')).slice(0, 5),
    presupuestos: data.presupuestos.filter((item) => item.anio === year && item.mes === monthNumber),
    proximos: data.recurrentes.filter((item) => item.estado === 'ACTIVO')
      .sort((a, b) => a.proximaOcurrencia.localeCompare(b.proximaOcurrencia)).slice(0, 4),
    periodo: formatPeriod(year, monthNumber),
  };
}

/** Resumen financiero del panel principal (saldos, mes actual, presupuestos y recurrentes). */
export default function FinanceOverview({ isAdmin }) {
  // loadFinances esta definida fuera del componente, asi que es estable entre renders.
  const { data, loading, error, reload } = useFetch(loadFinances);
  const summary = useMemo(() => (data ? summarize(data) : null), [data]);

  if (loading) {
    return <div className="table__loading"><Spinner /> <span>Calculando tu resumen...</span></div>;
  }
  if (error) {
    return (
      <Alert tone="danger" action={<Button variant="secondary" size="sm" icon="refresh" onClick={reload}>Reintentar</Button>}>
        {getErrorMessage(error)}
      </Alert>
    );
  }

  return (
    <>
      <div className="stats-grid">
        <StatCard label={isAdmin ? 'Saldo total (todos)' : 'Saldo disponible'} value={formatMoney(summary.saldo)} icon="wallet" to="/cuentas" />
        <StatCard label={`Ingresos · ${summary.periodo}`} value={formatMoney(summary.ingresos)} icon="trendUp" tone="success" to="/movimientos" />
        <StatCard label={`Gastos · ${summary.periodo}`} value={formatMoney(summary.gastos)} icon="trendDown" tone="danger" to="/movimientos" />
        <StatCard label="Deudas por pagar" value={formatMoney(summary.porPagar)} icon="debt" tone="warning" to="/deudas" />
      </div>

      <div className="dashboard-grid">
        <Card title="Movimientos recientes" actions={<ButtonLink to="/movimientos" variant="ghost" size="sm">Ver todos</ButtonLink>}>
          {summary.recientes.length === 0 ? (
            <EmptyState icon="swap" title="Aún no hay movimientos" action={<ButtonLink to="/movimientos" icon="plus" size="sm">Registrar movimiento</ButtonLink>} />
          ) : (
            <ul className="list">
              {summary.recientes.map((movimiento) => (
                <li key={movimiento.id} className="list__item">
                  <div>
                    <strong>{movimiento.descripcion || summary.categoriaName(movimiento.categoriaId)}</strong>
                    <span className="muted">{formatDate(movimiento.fechaMovimiento)} · {summary.cuentasById.get(movimiento.cuentaId)?.nombre}</span>
                  </div>
                  <span className={movimiento.tipo === 'GASTO' ? 'amount amount--out' : 'amount amount--in'}>
                    {movimiento.tipo === 'GASTO' ? '−' : '+'}{formatMoney(movimiento.monto)}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </Card>

        <Card title={`Presupuestos de ${summary.periodo}`} actions={<ButtonLink to="/presupuestos" variant="ghost" size="sm">Gestionar</ButtonLink>}>
          {summary.presupuestos.length === 0 ? (
            <EmptyState icon="pie" title="Sin presupuestos este mes" action={<ButtonLink to="/presupuestos" icon="plus" size="sm">Crear presupuesto</ButtonLink>} />
          ) : (
            <ul className="list">
              {summary.presupuestos.map((presupuesto) => {
                const spent = spentOnBudget(presupuesto, data.movimientos, summary.cuentasById);
                return (
                  <li key={presupuesto.id} className="list__item list__item--stack">
                    <div className="list__row">
                      <strong>{summary.categoriaName(presupuesto.categoriaId)}</strong>
                      <span className="muted">{formatMoney(spent)} de {formatMoney(presupuesto.montoLimite)}</span>
                    </div>
                    <ProgressBar value={spent} max={Number(presupuesto.montoLimite)} invert />
                  </li>
                );
              })}
            </ul>
          )}
        </Card>

        <Card title="Próximos movimientos recurrentes" actions={<ButtonLink to="/recurrentes" variant="ghost" size="sm">Ver todos</ButtonLink>}>
          {summary.proximos.length === 0 ? (
            <EmptyState icon="repeat" title="No hay recurrentes activos" />
          ) : (
            <ul className="list">
              {summary.proximos.map((item) => (
                <li key={item.id} className="list__item">
                  <div>
                    <strong>{item.descripcion || summary.categoriaName(item.categoriaId)}</strong>
                    <span className="muted">{formatDate(item.proximaOcurrencia)} · <EnumBadge type="periodicidad" value={item.periodicidad} /></span>
                  </div>
                  <span className={item.tipo === 'GASTO' ? 'amount amount--out' : 'amount amount--in'}>{formatMoney(item.monto)}</span>
                </li>
              ))}
            </ul>
          )}
        </Card>

        <Card title="Acciones rápidas">
          <div className="quick-actions">
            <ButtonLink to="/movimientos" icon="swap" variant="secondary">Registrar movimiento</ButtonLink>
            <ButtonLink to="/cuentas" icon="wallet" variant="secondary">Administrar cuentas</ButtonLink>
            <ButtonLink to="/planes" icon="piggy" variant="secondary">Planes de ahorro</ButtonLink>
            <ButtonLink to="/deudas" icon="debt" variant="secondary">Deudas y abonos</ButtonLink>
          </div>
        </Card>
      </div>
    </>
  );
}
