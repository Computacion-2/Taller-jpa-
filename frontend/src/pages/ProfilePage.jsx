import Button from '../components/ui/Button';
import Field from '../components/ui/Field';
import PasswordInput from '../components/ui/PasswordInput';
import { Alert, Badge, EnumBadge } from '../components/ui/Feedback';
import { Card, PageHeader } from '../components/ui/Layout';
import { useAuth } from '../hooks/useAuth';
import { useCatalog } from '../hooks/useCatalog';
import { useForm } from '../hooks/useForm';
import { useToast } from '../hooks/useToast';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { authService, monedasService } from '../api/services';
import { roleLabel } from '../auth/roles';
import { formatDate } from '../utils/format';
import { validateFields } from '../utils/validation';

const EMPTY = { actual: '', nueva: '', confirmar: '' };
const PASSWORD_FIELDS = [
  { name: 'actual', required: true },
  { name: 'nueva', required: true, minLength: 8, maxLength: 72 },
  { name: 'confirmar', required: true },
];
const monedaLabel = (moneda) => `${moneda.codigo} · ${moneda.nombre}`;

function validatePassword(values) {
  const errors = validateFields(PASSWORD_FIELDS, values);
  if (values.confirmar && values.confirmar !== values.nueva) {
    errors.confirmar = 'Las contraseñas no coinciden.';
  }
  if (values.nueva && values.nueva === values.actual) {
    errors.nueva = 'La nueva contraseña debe ser diferente a la actual.';
  }
  return errors;
}

const PASSWORD_INPUTS = [
  { name: 'actual', label: 'Contraseña actual', autoComplete: 'current-password' },
  { name: 'nueva', label: 'Nueva contraseña', autoComplete: 'new-password', hint: 'Entre 8 y 72 caracteres.' },
  { name: 'confirmar', label: 'Confirmar nueva contraseña', autoComplete: 'new-password' },
];

/** Perfil del usuario autenticado (GET /api/me) y cambio de contrasena (PUT /api/me/password). */
export default function ProfilePage() {
  useDocumentTitle('Mi perfil');
  const { user, roles, authorities, expiresAt } = useAuth();
  const toast = useToast();
  const monedas = useCatalog(monedasService, monedaLabel);
  const form = useForm(EMPTY, validatePassword);
  const permissions = authorities.filter((authority) => !authority.startsWith('ROLE_'));

  const onSubmit = async (values) => {
    await authService.changePassword(values.actual, values.nueva);
    form.setValues(EMPTY);
    toast.success('Tu contraseña se actualizó correctamente.');
  };

  return (
    <div className="page">
      <PageHeader title="Mi perfil" subtitle="Consulta tus datos y administra tu contraseña." />
      <div className="profile-grid">
        <Card title="Información personal">
          <dl className="details">
            <div><dt>Nombre</dt><dd>{user.nombreCompleto}</dd></div>
            <div><dt>Correo</dt><dd>{user.correo}</dd></div>
            <div><dt>Moneda principal</dt><dd>{monedas.nameOf(user.monedaId)}</dd></div>
            <div><dt>Programa académico</dt><dd>{user.programaAcademico || 'Sin registrar'}</dd></div>
            <div><dt>Estado</dt><dd><EnumBadge type="estado" value={user.estado} /></dd></div>
            <div><dt>Miembro desde</dt><dd>{formatDate(user.fechaRegistro)}</dd></div>
          </dl>
        </Card>

        <Card title="Roles y permisos" subtitle="Definen las secciones a las que puedes acceder.">
          <div className="badge-list">
            {roles.map((role) => <Badge key={role} tone="primary">{roleLabel(role)}</Badge>)}
            {permissions.map((permission) => <Badge key={permission} tone="info">{permission}</Badge>)}
          </div>
          <p className="muted session-note">
            Tu sesión vence el {new Date(expiresAt).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' })}.
          </p>
        </Card>

        <Card title="Cambiar contraseña" className="profile-grid__wide">
          {form.formError && <Alert tone="danger">{form.formError}</Alert>}
          <form className="form" onSubmit={form.handleSubmit(onSubmit)} noValidate>
            <div className="form__grid form__grid--3">
              {PASSWORD_INPUTS.map((input) => (
                <Field key={input.name} id={input.name} label={input.label} error={form.errors[input.name]} hint={input.hint} required>
                  <PasswordInput
                    id={input.name}
                    name={input.name}
                    value={form.values[input.name]}
                    onChange={form.handleChange}
                    error={form.errors[input.name]}
                    autoComplete={input.autoComplete}
                  />
                </Field>
              ))}
            </div>
            <div className="form__actions">
              <Button type="submit" icon="lock" loading={form.submitting}>Actualizar contraseña</Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
}
