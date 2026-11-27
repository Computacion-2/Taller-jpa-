import { useEffect } from 'react';

/** Actualiza el titulo de la pestana del navegador segun la pagina visible. */
export function useDocumentTitle(title) {
  useEffect(() => {
    document.title = title ? `${title} · Finanzas personales` : 'Finanzas personales';
  }, [title]);
}
