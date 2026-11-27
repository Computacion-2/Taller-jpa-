import { useCallback, useMemo } from 'react';
import { useFetch } from './useFetch';

/**
 * Lista un recurso REST y expone create/update/remove. Tras cada operacion exitosa
 * actualiza la lista local con la respuesta del backend, sin volver a pedir todo.
 */
export function useCrud(service, enabled = true) {
  const fetcher = useCallback(() => service.list(), [service]);
  const { data, setData, loading, error, reload } = useFetch(fetcher, enabled);
  const items = useMemo(() => data ?? [], [data]);

  const create = useCallback(async (body) => {
    const created = await service.create(body);
    setData((current) => [...(current ?? []), created]);
    return created;
  }, [service, setData]);

  const update = useCallback(async (item, body) => {
    const key = service.keyOf(item);
    const updated = await service.update(key, body);
    setData((current) => (current ?? []).map((row) => (service.keyOf(row) === key ? updated : row)));
    return updated;
  }, [service, setData]);

  const remove = useCallback(async (item) => {
    const key = service.keyOf(item);
    await service.remove(key);
    setData((current) => (current ?? []).filter((row) => service.keyOf(row) !== key));
  }, [service, setData]);

  return { items, loading, error, reload, create, update, remove };
}
