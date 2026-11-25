import { useContext } from 'react';
import { AuthContext } from '../auth/AuthContext';

/** Hook personalizado para leer la sesion: usuario, autoridades, login y logout. */
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de <AuthProvider>.');
  }
  return context;
}
