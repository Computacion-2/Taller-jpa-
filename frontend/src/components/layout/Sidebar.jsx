import { NavLink } from 'react-router';
import Icon from '../ui/Icon';
import { NAV_SECTIONS } from '../../config/navigation';
import { useAuth } from '../../hooks/useAuth';
import { cx } from '../../utils/format';

/** Menu lateral: solo muestra las secciones que el rol del usuario puede abrir. */
export default function Sidebar({ open, onNavigate }) {
  const { hasAnyAuthority } = useAuth();
  const sections = NAV_SECTIONS.filter((section) => hasAnyAuthority(section.allow));

  return (
    <aside className={cx('sidebar', open && 'is-open')} aria-label="Menú principal">
      <div className="sidebar__brand">
        <span className="sidebar__logo"><Icon name="coin" size={22} /></span>
        <div>
          <strong>Finanzas</strong>
          <span>personales</span>
        </div>
      </div>
      <nav className="sidebar__nav">
        {sections.map((section) => (
          <div key={section.title} className="sidebar__section">
            <p className="sidebar__heading">{section.title}</p>
            {section.items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                onClick={onNavigate}
                className={({ isActive }) => cx('sidebar__link', isActive && 'is-active')}
              >
                <Icon name={item.icon} size={19} />
                <span>{item.label}</span>
              </NavLink>
            ))}
          </div>
        ))}
      </nav>
    </aside>
  );
}
