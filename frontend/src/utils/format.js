const MONTHS = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio', 'agosto',
  'septiembre', 'octubre', 'noviembre', 'diciembre'];

const moneyFormatter = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

const dateFormatter = new Intl.DateTimeFormat('es-CO', { day: '2-digit', month: 'short', year: 'numeric' });

/** Une clases CSS ignorando valores falsos: cx('btn', active && 'is-active'). */
export function cx(...classes) {
  return classes.filter(Boolean).join(' ');
}

export function formatMoney(value) {
  if (value === null || value === undefined || value === '') {
    return '—';
  }
  return moneyFormatter.format(Number(value));
}

/** Convierte "2026-11-24" (LocalDate de Java) en fecha local sin desfase de zona horaria. */
export function parseLocalDate(value) {
  if (!value) {
    return null;
  }
  const [year, month, day] = value.split('-').map(Number);
  return new Date(year, month - 1, day);
}

export function formatDate(value) {
  const date = parseLocalDate(value);
  return date ? dateFormatter.format(date) : '—';
}

export function formatPeriod(year, month) {
  return `${MONTHS[month - 1] ?? month} ${year}`;
}

export function monthName(month) {
  return MONTHS[month - 1] ?? String(month);
}

/** Fecha de hoy en formato ISO (yyyy-MM-dd) segun la zona horaria del navegador. */
export function todayISO() {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60000;
  return new Date(now.getTime() - offset).toISOString().slice(0, 10);
}

/** Dias que faltan hasta una fecha (negativo si ya paso). */
export function daysUntil(value) {
  const date = parseLocalDate(value);
  if (!date) {
    return null;
  }
  const today = parseLocalDate(todayISO());
  return Math.round((date - today) / 86400000);
}

/** Texto sin tildes y en minusculas, para busquedas tolerantes. */
export function normalize(text) {
  return String(text ?? '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();
}

export function sum(items, selector) {
  return items.reduce((total, item) => total + Number(selector(item) ?? 0), 0);
}
