import { http } from './http';

/**
 * Crea un servicio REST con las cinco operaciones CRUD que exponen los controladores de Spring.
 * keyOf construye la clave de la ruta; las claves compuestas usan "a/b" (p. ej. /{planId}/{usuarioId}).
 */
export function createCrudService(resource, keyOf = (item) => item.id) {
  const path = (key) => `${resource}/${key}`;
  return {
    resource,
    keyOf,
    list: (params) => http.get(resource, { params }).then((response) => response.data),
    get: (key) => http.get(path(key)).then((response) => response.data),
    create: (body) => http.post(resource, body).then((response) => response.data),
    update: (key, body) => http.put(path(key), body).then((response) => response.data),
    remove: (key) => http.delete(path(key)).then(() => undefined),
  };
}
