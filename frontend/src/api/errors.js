// Traduce las respuestas ProblemDetail (application/problem+json) del backend a mensajes legibles.
const STATUS_MESSAGES = {
  400: 'Los datos enviados no son válidos.',
  401: 'Tu sesión no es válida. Inicia sesión de nuevo.',
  403: 'No tienes permisos para realizar esta operación.',
  404: 'El recurso no existe o no tienes acceso a él.',
  405: 'Operación no permitida.',
  409: 'La operación entra en conflicto con datos existentes.',
  500: 'Error interno del servidor. Intenta más tarde.',
};

export function getErrorMessage(error, fallback = 'Ocurrió un error inesperado.') {
  if (!error) {
    return fallback;
  }
  if (error.response) {
    const { status, data } = error.response;
    return data?.detail || STATUS_MESSAGES[status] || fallback;
  }
  if (error.request) {
    return 'No se pudo conectar con el servidor. Verifica que el backend esté encendido.';
  }
  return error.message || fallback;
}

/**
 * Los errores de validacion llegan como "campo: mensaje; otro: mensaje".
 * Se convierten en { campo: mensaje } para mostrarlos junto a cada input.
 */
export function getFieldErrors(error) {
  const detail = error?.response?.status === 400 ? error.response.data?.detail : null;
  if (typeof detail !== 'string' || !detail.includes(': ')) {
    return {};
  }
  return detail.split('; ').reduce((acc, part) => {
    const [field, ...message] = part.split(': ');
    if (field && message.length > 0 && /^[\w.[\]]+$/.test(field)) {
      acc[field.split(/[.[]/)[0]] = message.join(': ');
    }
    return acc;
  }, {});
}
