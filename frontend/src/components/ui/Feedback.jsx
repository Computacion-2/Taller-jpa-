import Icon from './Icon';
import { cx } from '../../utils/format';
import { enumLabel, enumTone } from '../../utils/enums';

const ALERT_ICONS = { danger: 'alert', warning: 'alert', success: 'check', info: 'info' };

/** Mensaje destacado dentro de una pagina o formulario. tone: info | success | warning | danger. */
export function Alert({ tone = 'info', title, children, action }) {
  return (
    <div className={cx('alert', `alert--${tone}`)} role={tone === 'danger' ? 'alert' : 'status'}>
      <Icon name={ALERT_ICONS[tone]} />
      <div className="alert__body">
        {title && <strong className="alert__title">{title}</strong>}
        <div>{children}</div>
      </div>
      {action && <div className="alert__action">{action}</div>}
    </div>
  );
}

/** Etiqueta de estado. tone: neutral | primary | success | warning | danger | info. */
export function Badge({ tone = 'neutral', children }) {
  return <span className={cx('badge', `badge--${tone}`)}>{children}</span>;
}

/** Insignia para un valor de enumeracion del backend: <EnumBadge type="estadoCuenta" value="ACTIVA" />. */
export function EnumBadge({ type, value }) {
  return <Badge tone={enumTone(type, value)}>{enumLabel(type, value)}</Badge>;
}

/** Estado vacio con icono, mensaje y accion opcional. */
export function EmptyState({ icon = 'info', title, message, action }) {
  return (
    <div className="empty-state">
      <span className="empty-state__icon"><Icon name={icon} size={28} /></span>
      <h3>{title}</h3>
      {message && <p>{message}</p>}
      {action}
    </div>
  );
}

/** Barra de progreso; se torna ambar al 80 % y roja al superar el 100 % cuando invert=true. */
export function ProgressBar({ value, max, invert = false, label }) {
  const ratio = max > 0 ? value / max : 0;
  const percent = Math.round(ratio * 100);
  let tone = 'primary';
  if (invert && ratio > 1) {
    tone = 'danger';
  } else if (invert && ratio >= 0.8) {
    tone = 'warning';
  } else if (!invert && ratio >= 1) {
    tone = 'success';
  }
  return (
    <div className="progress" title={label ?? `${percent} %`}>
      <div
        className="progress__track"
        role="progressbar"
        aria-valuenow={percent}
        aria-valuemin={0}
        aria-valuemax={100}
        aria-label={label ?? 'Progreso'}
      >
        <div className={cx('progress__bar', `progress__bar--${tone}`)} style={{ width: `${Math.min(100, Math.max(0, percent))}%` }} />
      </div>
      <span className="progress__label">{percent} %</span>
    </div>
  );
}
