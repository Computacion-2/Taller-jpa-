import { Link } from 'react-router';
import Icon from './Icon';
import Spinner from './Spinner';
import { cx } from '../../utils/format';

/**
 * Boton base de la aplicacion.
 * variant: primary | secondary | danger | ghost — size: sm | md — loading muestra un spinner y lo deshabilita.
 */
export default function Button({
  children, variant = 'primary', size = 'md', type = 'button', loading = false,
  icon, disabled = false, className = '', ...props
}) {
  return (
    <button
      type={type === 'submit' ? 'submit' : 'button'}
      className={cx('btn', `btn--${variant}`, `btn--${size}`, loading && 'is-loading', className)}
      disabled={disabled || loading}
      aria-busy={loading || undefined}
      {...props}
    >
      {loading ? <Spinner size="sm" /> : icon && <Icon name={icon} size={size === 'sm' ? 16 : 18} />}
      {children && <span>{children}</span>}
    </button>
  );
}

/** Enlace de React Router con la apariencia de un boton. */
export function ButtonLink({ to, children, variant = 'primary', size = 'md', icon, className = '', ...props }) {
  return (
    <Link to={to} className={cx('btn', `btn--${variant}`, `btn--${size}`, className)} {...props}>
      {icon && <Icon name={icon} size={size === 'sm' ? 16 : 18} />}
      <span>{children}</span>
    </Link>
  );
}

/** Enlace cuadrado solo con icono (p. ej. "Ver detalle" en una fila de tabla). */
export function IconLink({ to, icon, label, className = '' }) {
  return (
    <Link to={to} className={cx('icon-btn', 'icon-btn--ghost', className)} aria-label={label} title={label}>
      <Icon name={icon} size={18} />
    </Link>
  );
}

/** Boton cuadrado solo con icono; label se usa como texto accesible y tooltip. */
export function IconButton({ icon, label, variant = 'ghost', className = '', ...props }) {
  return (
    <button
      type="button"
      className={cx('icon-btn', `icon-btn--${variant}`, className)}
      aria-label={label}
      title={label}
      {...props}
    >
      <Icon name={icon} size={18} />
    </button>
  );
}
