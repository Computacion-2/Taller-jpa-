import { cx } from '../../utils/format';

/**
 * Envoltura de un campo de formulario: etiqueta, control, ayuda y mensaje de error.
 * Con group=true usa <fieldset>/<legend> (para grupos de casillas).
 */
export default function Field({ id, label, error, hint, required = false, full = false, group = false, children }) {
  const className = cx('field', full && 'field--full', error && 'field--error');
  const marker = required && <span className="field__required" aria-hidden="true">*</span>;
  const message = error
    ? <p className="field__error" id={`${id}-error`} role="alert">{error}</p>
    : hint && <p className="field__hint">{hint}</p>;

  if (group) {
    return (
      <fieldset className={className}>
        <legend className="field__label">{label}{marker}</legend>
        {children}
        {message}
      </fieldset>
    );
  }
  return (
    <div className={className}>
      <label htmlFor={id} className="field__label">{label}{marker}</label>
      {children}
      {message}
    </div>
  );
}
