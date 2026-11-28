import AdminOverview from '../components/dashboard/AdminOverview';
import FinanceOverview from '../components/dashboard/FinanceOverview';
import { ButtonLink } from '../components/ui/Button';
import { EmptyState } from '../components/ui/Feedback';
import { PageHeader } from '../components/ui/Layout';
import { useAuth } from '../hooks/useAuth';
import { useDocumentTitle } from '../hooks/useDocumentTitle';

function greeting() {
  const hour = new Date().getHours();
  if (hour < 12) {
    return 'Buenos días';
  }
  return hour < 19 ? 'Buenas tardes' : 'Buenas noches';
}

/** Pagina de inicio: el contenido cambia segun el rol del usuario autenticado. */
export default function DashboardPage() {
  useDocumentTitle('Inicio');
  const { user, isAdmin, canManageFinances } = useAuth();
  const firstName = user?.nombreCompleto?.split(' ')[0] ?? '';

  return (
    <div className="page">
      <PageHeader
        title={`${greeting()}, ${firstName}`}
        subtitle={isAdmin ? 'Panel de administración y resumen financiero global.' : 'Este es el resumen de tus finanzas.'}
        actions={canManageFinances && <ButtonLink to="/movimientos" icon="plus">Nuevo movimiento</ButtonLink>}
      />

      {isAdmin && (
        <section className="dashboard-section">
          <h2 className="section-title">Administración</h2>
          <AdminOverview />
        </section>
      )}

      {canManageFinances ? (
        <section className="dashboard-section">
          {isAdmin && <h2 className="section-title">Finanzas de todos los usuarios</h2>}
          <FinanceOverview isAdmin={isAdmin} />
        </section>
      ) : (
        <EmptyState
          icon="lock"
          title="Tu cuenta aún no tiene acceso a los módulos financieros"
          message="Un administrador debe asignarte el permiso GESTIONAR_FINANZAS. Mientras tanto puedes revisar tu perfil."
          action={<ButtonLink to="/perfil" icon="user">Ir a mi perfil</ButtonLink>}
        />
      )}
    </div>
  );
}
