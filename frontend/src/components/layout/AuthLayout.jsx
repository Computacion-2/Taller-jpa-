import Icon from '../ui/Icon';

const FEATURES = [
  { icon: 'wallet', text: 'Cuentas y saldos siempre actualizados' },
  { icon: 'pie', text: 'Presupuestos mensuales por categoría' },
  { icon: 'piggy', text: 'Planes de ahorro individuales y compartidos' },
  { icon: 'debt', text: 'Control de deudas y abonos' },
];

/** Plantilla de las paginas publicas: panel de marca a la izquierda y formulario a la derecha. */
export default function AuthLayout({ title, subtitle, children, footer }) {
  return (
    <div className="auth">
      <aside className="auth__brand">
        <div className="auth__logo">
          <span className="sidebar__logo"><Icon name="coin" size={24} /></span>
          <strong>Finanzas personales</strong>
        </div>
        <div>
          <h2 className="auth__headline">Toma el control de tu dinero durante la universidad.</h2>
          <ul className="auth__features">
            {FEATURES.map((feature) => (
              <li key={feature.text}><Icon name={feature.icon} /> {feature.text}</li>
            ))}
          </ul>
        </div>
        <p className="auth__note">Computación en Internet II · Universidad Icesi</p>
      </aside>
      <main className="auth__panel">
        <div className="auth__card">
          <h1 className="auth__title">{title}</h1>
          {subtitle && <p className="auth__subtitle">{subtitle}</p>}
          {children}
          {footer && <div className="auth__footer">{footer}</div>}
        </div>
      </main>
    </div>
  );
}
