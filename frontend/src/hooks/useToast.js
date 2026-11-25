import { useContext } from 'react';
import { ToastContext } from '../components/ui/ToastContext';

/** Hook personalizado: toast.success('...'), toast.error('...'), toast.info('...'). */
export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast debe usarse dentro de <ToastProvider>.');
  }
  return context;
}
