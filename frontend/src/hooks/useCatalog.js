import { useCallback, useMemo } from 'react';
import { useFetch } from './useFetch';

const byNombre = (item) => item.nombre;

/**
 * Carga un catalogo (monedas, cuentas, categorias...) para usarlo en selects y tablas.
 * Devuelve la lista, un Map por id y una funcion para obtener el nombre de un id.
 * labelOf debe ser una funcion estable (definida fuera del componente).
 */
export function useCatalog(service, labelOf = byNombre, enabled = true) {
  const fetcher = useCallback(() => service.list(), [service]);
  const { data, loading, error, reload } = useFetch(fetcher, enabled);
  const items = useMemo(() => data ?? [], [data]);
  const byId = useMemo(() => new Map(items.map((item) => [item.id, item])), [items]);

  const nameOf = useCallback((id) => {
    if (id === null || id === undefined) {
      return '—';
    }
    const item = byId.get(id);
    return item ? labelOf(item) : `#${id}`;
  }, [byId, labelOf]);

  const options = useMemo(
    () => items.map((item) => ({ value: String(item.id), label: labelOf(item) })),
    [items, labelOf],
  );

  return { items, byId, nameOf, options, loading, error, reload };
}
