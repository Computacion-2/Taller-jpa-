import { useCallback, useMemo, useRef, useState } from 'react';
import { ToastContext } from './ToastContext';
import Icon from './Icon';

const DURATION_MS = 4500;
const ICONS = { success: 'check', danger: 'alert', info: 'info' };

/** Muestra notificaciones apiladas en la esquina inferior derecha. */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const nextId = useRef(1);

  const dismiss = useCallback((id) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const push = useCallback((tone, message) => {
    const id = nextId.current;
    nextId.current += 1;
    setToasts((current) => [...current, { id, tone, message }]);
    window.setTimeout(() => dismiss(id), DURATION_MS);
  }, [dismiss]);

  const api = useMemo(() => ({
    success: (message) => push('success', message),
    error: (message) => push('danger', message),
    info: (message) => push('info', message),
  }), [push]);

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="toaster" aria-live="polite" aria-atomic="false">
        {toasts.map((toast) => (
          <div key={toast.id} className={`toast toast--${toast.tone}`} role="status">
            <Icon name={ICONS[toast.tone]} />
            <span className="toast__message">{toast.message}</span>
            <button type="button" className="toast__close" onClick={() => dismiss(toast.id)} aria-label="Cerrar notificación">
              <Icon name="x" size={16} />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}
