import { useCallback, useMemo } from 'react';
import { useAuth } from './useAuth';
import { useFetch } from './useFetch';
import { usuariosService } from '../api/services';

const ALL_USERS = { page: 0, size: 1000, sort: 'id' };

/**
 * Usuarios que se pueden elegir en los formularios. El administrador ve a todos
 * (GET /api/admin/usuarios); un usuario normal solo puede operar sobre si mismo.
 */
export function useUserOptions() {
  const { user, isAdmin } = useAuth();
  const fetcher = useCallback(() => usuariosService.page(ALL_USERS).then((page) => page.content), []);
  const { data, loading } = useFetch(fetcher, isAdmin);

  const users = useMemo(() => {
    if (isAdmin) {
      return data ?? [];
    }
    return user ? [user] : [];
  }, [isAdmin, data, user]);

  const options = useMemo(
    () => users.map((item) => ({ value: String(item.id), label: `${item.nombreCompleto} (${item.correo})` })),
    [users],
  );

  const nameOf = useCallback((id) => {
    if (id === null || id === undefined) {
      return '—';
    }
    if (user && id === user.id) {
      return isAdmin ? `${user.nombreCompleto} (tú)` : 'Tú';
    }
    const found = users.find((item) => item.id === id);
    return found ? found.nombreCompleto : `Usuario #${id}`;
  }, [users, user, isAdmin]);

  return { users, options, nameOf, loading, isAdmin, currentUserId: user?.id };
}
