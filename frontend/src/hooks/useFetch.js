import { useCallback, useEffect, useState } from 'react';

/**
 * Ejecuta una funcion asincrona (normalmente un servicio de Axios) y expone
 * { data, loading, error, reload }. La funcion debe estar memorizada con useCallback.
 * Si el componente se desmonta antes de la respuesta, el resultado se descarta.
 */
export function useFetch(fetcher, enabled = true) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(enabled);
  const [error, setError] = useState(null);
  const [version, setVersion] = useState(0);

  useEffect(() => {
    if (!enabled) {
      setLoading(false);
      return undefined;
    }
    let active = true;
    setLoading(true);
    setError(null);
    fetcher()
      .then((result) => {
        if (active) {
          setData(result);
        }
      })
      .catch((err) => {
        if (active) {
          setError(err);
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });
    return () => {
      active = false;
    };
  }, [fetcher, enabled, version]);

  const reload = useCallback(() => setVersion((current) => current + 1), []);

  return { data, setData, loading, error, reload };
}
