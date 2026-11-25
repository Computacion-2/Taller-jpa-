import { Link } from 'react-router';
import Icon from './Icon';
import { cx } from '../../utils/format';

/** Encabezado de pagina con titulo, descripcion, enlace de regreso y acciones. */
export function PageHeader({ title, subtitle, actions, back }) {
  return (
    <header className="page-header">
      <div>
        {back && (
          <Link to={back.to} className="page-header__back">
            <Icon name="arrowLeft" size={16} />
            {back.label}
          </Link>
        )}
        <h1 className="page-header__title">{title}</h1>
        {subtitle && <p className="page-header__subtitle">{subtitle}</p>}
      </div>
      {actions && <div className="page-header__actions">{actions}</div>}
    </header>
  );
}

/** Contenedor con borde y sombra suave. */
export function Card({ title, subtitle, actions, children, className = '' }) {
  return (
    <section className={cx('card', className)}>
      {(title || actions) && (
        <div className="card__header">
          <div>
            {title && <h2 className="card__title">{title}</h2>}
            {subtitle && <p className="card__subtitle">{subtitle}</p>}
          </div>
          {actions && <div className="card__actions">{actions}</div>}
        </div>
      )}
      <div className="card__body">{children}</div>
    </section>
  );
}

/** Tarjeta de indicador para el panel principal. */
export function StatCard({ label, value, icon, tone = 'primary', hint, to }) {
  const content = (
    <>
      <span className={cx('stat__icon', `stat__icon--${tone}`)}><Icon name={icon} size={22} /></span>
      <div>
        <p className="stat__label">{label}</p>
        <p className="stat__value">{value}</p>
        {hint && <p className="stat__hint">{hint}</p>}
      </div>
    </>
  );
  if (to) {
    return <Link to={to} className="stat stat--link">{content}</Link>;
  }
  return <div className="stat">{content}</div>;
}

/** Pestanas accesibles controladas por el componente padre. */
export function Tabs({ tabs, active, onChange }) {
  return (
    <div className="tabs" role="tablist">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          type="button"
          role="tab"
          id={`tab-${tab.id}`}
          aria-selected={active === tab.id}
          className={cx('tabs__tab', active === tab.id && 'is-active')}
          onClick={() => onChange(tab.id)}
        >
          {tab.icon && <Icon name={tab.icon} size={16} />}
          {tab.label}
          {tab.count !== undefined && <span className="tabs__count">{tab.count}</span>}
        </button>
      ))}
    </div>
  );
}
