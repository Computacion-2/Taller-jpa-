import { useEffect, useState } from 'react';

/** Retrasa la actualizacion de un valor (por ejemplo, un texto de busqueda que consulta la API). */
export function useDebounce(value, delay = 400) {
  const [debounced, setDebounced] = useState(value);

  useEffect(() => {
    const timer = window.setTimeout(() => setDebounced(value), delay);
    return () => window.clearTimeout(timer);
  }, [value, delay]);

  return debounced;
}
