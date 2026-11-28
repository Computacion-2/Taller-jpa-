// Conversion entre los DTO del backend y los valores (texto) de los formularios declarativos.
const NUMERIC_TYPES = new Set(['number', 'money']);

function toFormValue(field, value) {
  if (field.type === 'checkboxes') {
    return (value ?? []).map(String);
  }
  if (value === null || value === undefined) {
    return '';
  }
  return String(value);
}

function toBodyValue(field, value) {
  if (field.type === 'checkboxes') {
    return value.map((item) => (field.numeric ? Number(item) : item));
  }
  if (value === '' || value === null || value === undefined) {
    return null;
  }
  if (NUMERIC_TYPES.has(field.type) || field.numeric) {
    return Number(value);
  }
  return typeof value === 'string' ? value.trim() : value;
}

/** Valores iniciales: los del registro a editar o, si es nuevo, los valores por defecto. */
export function toFormValues(fields, item, defaults = {}) {
  return fields.reduce((values, field) => {
    const source = item ? item[field.name] : defaults[field.name];
    return { ...values, [field.name]: toFormValue(field, source) };
  }, {});
}

/** Cuerpo JSON para POST/PUT con los tipos que espera el DTO Request de Spring. */
export function toRequestBody(fields, values) {
  return fields.reduce((body, field) => {
    if (field.exclude) {
      return body;
    }
    return { ...body, [field.name]: toBodyValue(field, values[field.name]) };
  }, {});
}
