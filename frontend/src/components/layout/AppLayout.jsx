import { Suspense, useCallback, useState } from 'react';
import { Outlet } from 'react-router';
import Sidebar from './Sidebar';
import Topbar from './Topbar';
import { FullPageLoader } from '../ui/Spinner';
import { cx } from '../../utils/format';

/** Estructura de las paginas autenticadas: menu lateral, barra superior y contenido. */
export default function AppLayout() {
  const [menuOpen, setMenuOpen] = useState(false);
  const closeMenu = useCallback(() => setMenuOpen(false), []);

  return (
    <div className={cx('shell', menuOpen && 'shell--menu-open')}>
      <Sidebar open={menuOpen} onNavigate={closeMenu} />
      {menuOpen && <button type="button" className="shell__overlay" aria-label="Cerrar menú" onClick={closeMenu} />}
      <div className="shell__main">
        <Topbar onToggleMenu={() => setMenuOpen((open) => !open)} />
        <main className="shell__content" id="contenido">
          <Suspense fallback={<FullPageLoader message="Cargando página..." />}>
            <Outlet />
          </Suspense>
        </main>
      </div>
    </div>
  );
}
