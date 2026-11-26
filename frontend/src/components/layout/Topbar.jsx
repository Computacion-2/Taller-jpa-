import { Link, useNavigate } from 'react-router';
import Button, { IconButton } from '../ui/Button';
import { Badge } from '../ui/Feedback';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/useToast';
import { roleLabel } from '../../auth/roles';

function initials(name = '') {
  return name.split(' ').filter(Boolean).slice(0, 2).map((part) => part[0].toUpperCase()).join('');
}

/** Barra superior: boton de menu en movil, datos del usuario, roles y cierre de sesion. */
export default function Topbar({ onToggleMenu }) {
  const { user, roles, logout } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();

  const onLogout = () => {
    logout();
    toast.info('Sesión cerrada. ¡Hasta pronto!');
    navigate('/login', { replace: true });
  };

  return (
    <header className="topbar">
      <IconButton icon="menu" label="Abrir menú" className="topbar__menu" onClick={onToggleMenu} />
      <div className="topbar__spacer" />
      <Link to="/perfil" className="topbar__user" title="Ver mi perfil">
        <span className="avatar" aria-hidden="true">{initials(user?.nombreCompleto)}</span>
        <span className="topbar__identity">
          <strong>{user?.nombreCompleto}</strong>
          <span>{user?.correo}</span>
        </span>
      </Link>
      <div className="topbar__roles">
        {roles.map((role) => <Badge key={role} tone="primary">{roleLabel(role)}</Badge>)}
      </div>
      <Button variant="secondary" size="sm" icon="logout" onClick={onLogout}>Salir</Button>
    </header>
  );
}
