import axios from 'axios';
import { tokenStorage } from './tokenStorage';

/** URL del backend Spring Boot (ver .env.development y .env.production). */
export const API_URL = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '');

/** Cliente Axios unico para toda la aplicacion. */
export const http = axios.create({
  baseURL: `${API_URL}/api`,
  timeout: 15000,
  headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
});

let unauthorizedHandler = () => {};

/** El AuthProvider registra aqui que hacer cuando el backend rechaza el token (401). */
export function setUnauthorizedHandler(handler) {
  unauthorizedHandler = handler;
}

// Adjunta el JWT en cada solicitud: Authorization: Bearer <token>.
http.interceptors.request.use((config) => {
  const token = tokenStorage.get();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Un 401 fuera del login significa token vencido, alterado o usuario inactivo: se cierra la sesion.
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const url = error.config?.url ?? '';
    if (status === 401 && !url.startsWith('/auth/')) {
      unauthorizedHandler();
    }
    return Promise.reject(error);
  },
);
