// Enumeraciones del backend (paquete entity.enums) con su etiqueta y color de insignia.
export const ENUMS = {
  estado: {
    ACTIVO: { label: 'Activo', tone: 'success' },
    INACTIVO: { label: 'Inactivo', tone: 'neutral' },
  },
  estadoCuenta: {
    ACTIVA: { label: 'Activa', tone: 'success' },
    ARCHIVADA: { label: 'Archivada', tone: 'neutral' },
  },
  tipoMovimiento: {
    INGRESO: { label: 'Ingreso', tone: 'success' },
    GASTO: { label: 'Gasto', tone: 'danger' },
  },
  periodicidad: {
    SEMANAL: { label: 'Semanal', tone: 'info' },
    QUINCENAL: { label: 'Quincenal', tone: 'info' },
    MENSUAL: { label: 'Mensual', tone: 'info' },
  },
  estadoRecurrente: {
    ACTIVO: { label: 'Activo', tone: 'success' },
    SUSPENDIDO: { label: 'Suspendido', tone: 'warning' },
    INACTIVO: { label: 'Inactivo', tone: 'neutral' },
  },
  tipoDeuda: {
    POR_PAGAR: { label: 'Por pagar', tone: 'danger' },
    POR_COBRAR: { label: 'Por cobrar', tone: 'info' },
  },
  estadoDeuda: {
    PENDIENTE: { label: 'Pendiente', tone: 'warning' },
    SALDADA: { label: 'Saldada', tone: 'success' },
  },
  tipoPlan: {
    INDIVIDUAL: { label: 'Individual', tone: 'primary' },
    COMPARTIDO: { label: 'Compartido', tone: 'info' },
  },
  estadoPlan: {
    ACTIVO: { label: 'Activo', tone: 'success' },
    CERRADO: { label: 'Cerrado', tone: 'neutral' },
    ARCHIVADO: { label: 'Archivado', tone: 'neutral' },
  },
  estadoAporte: {
    ACTIVO: { label: 'Activo', tone: 'success' },
    RETIRADO: { label: 'Retirado', tone: 'neutral' },
  },
  estadoParticipacion: {
    ACTIVO: { label: 'Activo', tone: 'success' },
    RETIRADO: { label: 'Retirado', tone: 'neutral' },
  },
  estadoInvitacion: {
    PENDIENTE: { label: 'Pendiente', tone: 'warning' },
    ACEPTADA: { label: 'Aceptada', tone: 'success' },
    RECHAZADA: { label: 'Rechazada', tone: 'danger' },
  },
};

/** Opciones { value, label } para un <select>. */
export function enumOptions(type) {
  return Object.entries(ENUMS[type]).map(([value, meta]) => ({ value, label: meta.label }));
}

export function enumLabel(type, value) {
  return ENUMS[type]?.[value]?.label ?? value ?? '—';
}

export function enumTone(type, value) {
  return ENUMS[type]?.[value]?.tone ?? 'neutral';
}
