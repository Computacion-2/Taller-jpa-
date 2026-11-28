import Button from '../ui/Button';
import Field from '../ui/Field';
import FormControl from '../ui/FormControl';
import { Alert } from '../ui/Feedback';
import { useForm } from '../../hooks/useForm';
import { validateFields } from '../../utils/validation';

/**
 * Formulario declarativo: recibe la lista de campos y genera inputs, validacion y envio.
 * Campos con hidden=true no se muestran (p. ej. usuarioId del usuario autenticado);
 * lockOnEdit=true bloquea las claves de un registro existente; clears=['x'] limpia otros campos.
 */
export default function EntityForm({
  fields, initialValues, validate, onSubmit, onCancel, submitLabel = 'Guardar', isEdit = false,
}) {
  const visibleFields = fields.filter((field) => !field.hidden);
  const form = useForm(initialValues, (values) => ({
    ...validateFields(visibleFields, values),
    ...(validate ? validate(values) : {}),
  }));

  const onChange = (name, value) => {
    form.setFieldValue(name, value);
    const field = fields.find((item) => item.name === name);
    (field?.clears ?? []).forEach((dependent) => form.setFieldValue(dependent, ''));
  };

  return (
    <form className="form" onSubmit={form.handleSubmit(onSubmit)} noValidate>
      {form.formError && <Alert tone="danger" title="No se pudo guardar">{form.formError}</Alert>}
      <div className="form__grid">
        {visibleFields.map((field) => (
          <Field
            key={field.name}
            id={`field-${field.name}`}
            label={field.label}
            error={form.errors[field.name]}
            hint={field.hint}
            required={field.required}
            full={field.full || field.type === 'textarea' || field.type === 'checkboxes'}
            group={field.type === 'checkboxes'}
          >
            <FormControl
              field={field}
              value={form.values[field.name]}
              values={form.values}
              onChange={onChange}
              error={form.errors[field.name]}
              disabled={isEdit && field.lockOnEdit}
            />
          </Field>
        ))}
      </div>
      <div className="form__actions">
        <Button variant="secondary" onClick={onCancel} disabled={form.submitting}>Cancelar</Button>
        <Button type="submit" icon="check" loading={form.submitting}>{submitLabel}</Button>
      </div>
    </form>
  );
}
