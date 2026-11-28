// Validaciones del lado del cliente equivalentes a las anotaciones Jakarta Validation del backend.
const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function isEmpty(value) {
  return value === null || value === undefined || value === '' || (Array.isArray(value) && value.length === 0);
}

function numberError(field, value) {
  const number = Number(value);
  if (Number.isNaN(number)) {
    return 'Debe ser un número.';
  }
  if (field.min !== undefined && number < field.min) {
    return `Debe ser mayor o igual a ${field.min}.`;
  }
  if (field.max !== undefined && number > field.max) {
    return `Debe ser menor o igual a ${field.max}.`;
  }
  if (field.positive && number <= 0) {
    return 'Debe ser mayor que cero.';
  }
  return null;
}

/** Devuelve el mensaje de error de un campo o null si es valido. */
export function validateField(field, value) {
  if (isEmpty(value)) {
    return field.required ? 'Este campo es obligatorio.' : null;
  }
  if (field.type === 'email' && !EMAIL.test(value)) {
    return 'Ingresa un correo válido.';
  }
  if (field.minLength && String(value).length < field.minLength) {
    return `Debe tener al menos ${field.minLength} caracteres.`;
  }
  if (field.maxLength && String(value).length > field.maxLength) {
    return `Máximo ${field.maxLength} caracteres.`;
  }
  if (field.pattern && !field.pattern.regex.test(value)) {
    return field.pattern.message;
  }
  if (field.type === 'number' || field.type === 'money') {
    return numberError(field, value);
  }
  return null;
}

/** Valida todos los campos visibles de un formulario declarativo. */
export function validateFields(fields, values) {
  return fields.reduce((errors, field) => {
    const message = validateField(field, values[field.name]);
    return message ? { ...errors, [field.name]: message } : errors;
  }, {});
}
