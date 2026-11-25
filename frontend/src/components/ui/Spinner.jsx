import { cx } from '../../utils/format';

/** Indicador de carga. size: sm | md | lg. */
export default function Spinner({ size = 'md', label = 'Cargando' }) {
  return <span className={cx('spinner', `spinner--${size}`)} role="status" aria-label={label} />;
}

/** Pantalla de carga centrada (verificacion de sesion, paginas diferidas). */
export function FullPageLoader({ message = 'Cargando...' }) {
  return (
    <div className="full-loader">
      <Spinner size="lg" />
      <p>{message}</p>
    </div>
  );
}
