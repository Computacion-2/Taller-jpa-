// Persistencia del JWT. Se aisla aqui para poder cambiar el almacenamiento sin tocar el resto.
const TOKEN_KEY = 'accessToken';

export const tokenStorage = {
  get() {
    try {
      return window.localStorage.getItem(TOKEN_KEY);
    } catch {
      return null;
    }
  },
  set(token) {
    try {
      window.localStorage.setItem(TOKEN_KEY, token);
    } catch {
      // Sin almacenamiento disponible, la sesion dura solo mientras la pestana este abierta.
    }
  },
  clear() {
    try {
      window.localStorage.removeItem(TOKEN_KEY);
    } catch {
      // Nada que limpiar.
    }
  },
};
