import { useCallback, useEffect, useMemo, useState } from 'react';
import { AuthContext } from './AuthContext';
import { decodeToken, remainingTime } from './jwt';
import { FINANCE_ACCESS, ROLE_ADMIN } from './roles';
import { authService } from '../api/services';
import { setUnauthorizedHandler } from '../api/http';
import { tokenStorage } from '../api/tokenStorage';
import { getErrorMessage } from '../api/errors';

/** Lee el token guardado y lo descarta si esta vencido o malformado. */
function readStoredSession() {
  const token = tokenStorage.get();
  const claims = token ? decodeToken(token) : null;
  if (!token || remainingTime(claims) === 0) {
    tokenStorage.clear();
    return null;
  }
  return { token, claims };
}

function toSession(token) {
  return { token, claims: decodeToken(token) };
}

/**
 * Proveedor de autenticacion: guarda el JWT, carga el perfil (/api/me), expone las
 * autoridades del token y cierra la sesion cuando el token vence o el backend lo rechaza.
 */
export function AuthProvider({ children }) {
  const [session, setSession] = useState(readStoredSession);
  const [user, setUser] = useState(null);
  const [profileError, setProfileError] = useState(null);
  const [logoutReason, setLogoutReason] = useState(null);

  const logout = useCallback((reason = null) => {
    tokenStorage.clear();
    setSession(null);
    setUser(null);
    setProfileError(null);
    setLogoutReason(reason);
  }, []);

  const loadProfile = useCallback(() => {
    setProfileError(null);
    return authService.me().then(setUser).catch((error) => {
      if (error.response?.status !== 401) {
        setProfileError(getErrorMessage(error));
      }
    });
  }, []);

  // Al recargar la pagina con un token guardado, se recupera el perfil del usuario.
  useEffect(() => {
    if (session && !user) {
      loadProfile();
    }
  }, [session, user, loadProfile]);

  // Cierre automatico de la sesion en el instante en que vence el JWT.
  useEffect(() => {
    if (!session) {
      return undefined;
    }
    const timer = window.setTimeout(() => logout('expired'), remainingTime(session.claims));
    return () => window.clearTimeout(timer);
  }, [session, logout]);

  // El interceptor de Axios avisa cuando el backend responde 401.
  useEffect(() => {
    setUnauthorizedHandler(() => logout('expired'));
    return () => setUnauthorizedHandler(() => {});
  }, [logout]);

  const login = useCallback(async (correo, password) => {
    const auth = await authService.login(correo, password);
    tokenStorage.set(auth.accessToken);
    let profile;
    try {
      profile = await authService.me();
    } catch (error) {
      tokenStorage.clear();
      throw error;
    }
    setUser(profile);
    setLogoutReason(null);
    setSession(toSession(auth.accessToken));
    return profile;
  }, []);

  const register = useCallback(async (data) => {
    await authService.register(data);
    return login(data.correo, data.password);
  }, [login]);

  const authorities = useMemo(() => session?.claims?.authorities ?? [], [session]);

  const hasAnyAuthority = useCallback(
    (required) => !required?.length || required.some((authority) => authorities.includes(authority)),
    [authorities],
  );

  const value = useMemo(() => {
    let status = 'anonymous';
    if (session) {
      status = user ? 'authenticated' : 'checking';
    }
    return {
      status,
      user,
      authorities,
      roles: session?.claims?.roles ?? [],
      expiresAt: session ? session.claims.exp * 1000 : null,
      isAdmin: authorities.includes(ROLE_ADMIN),
      canManageFinances: hasAnyAuthority(FINANCE_ACCESS),
      hasAnyAuthority,
      login,
      register,
      logout,
      logoutReason,
      profileError,
      retryProfile: loadProfile,
      refreshProfile: loadProfile,
    };
  }, [session, user, authorities, hasAnyAuthority, login, register, logout, logoutReason, profileError, loadProfile]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
