const INPUT_TYPES = { money: 'number', number: 'number', date: 'date', email: 'email', password: 'password', url: 'url' };

function CheckboxGroup({ field, value, options, onChange, disabled }) {
  const selected = new Set(value ?? []);
  const toggle = (optionValue) => {
    const next = new Set(selected);
    if (next.has(optionValue)) {
      next.delete(optionValue);
    } else {
      next.add(optionValue);
    }
    onChange(field.name, [...next]);
  };
  if (options.length === 0) {
    return <p className="field__hint">No hay opciones disponibles.</p>;
  }
  return (
    <div className="checkbox-grid">
      {options.map((option) => (
        <label key={option.value} className="checkbox">
          <input
            type="checkbox"
            checked={selected.has(option.value)}
            onChange={() => toggle(option.value)}
            disabled={disabled}
          />
          <span>{option.label}</span>
        </label>
      ))}
    </div>
  );
}

function TextControl({ field, value, onChange, common }) {
  const isMoney = field.type === 'money';
  return (
    <input
      {...common}
      type={INPUT_TYPES[field.type] ?? 'text'}
      value={value ?? ''}
      onChange={onChange}
      placeholder={field.placeholder}
      maxLength={field.maxLength}
      min={isMoney ? 0 : field.min}
      max={field.max}
      step={isMoney ? '0.01' : field.step}
      autoComplete={field.autoComplete ?? 'off'}
    />
  );
}

/**
 * Control de formulario generado a partir de la configuracion de un campo
 * ({ name, type, options, min, max, ... }). Los cambios se informan con onChange(name, value).
 */
export default function FormControl({ field, value, values, onChange, error, disabled = false }) {
  const id = `field-${field.name}`;
  const options = typeof field.options === 'function' ? field.options(values) : field.options ?? [];
  const isDisabled = disabled || field.disabled;
  const common = {
    id,
    name: field.name,
    disabled: isDisabled,
    'aria-invalid': Boolean(error),
    'aria-describedby': error ? `${id}-error` : undefined,
    className: 'input',
  };
  const handle = (event) => onChange(field.name, event.target.value);

  if (field.type === 'checkboxes') {
    return <CheckboxGroup field={field} value={value} options={options} onChange={onChange} disabled={isDisabled} />;
  }
  if (field.type === 'select') {
    return (
      <select {...common} value={value ?? ''} onChange={handle}>
        <option value="">{field.placeholder ?? 'Selecciona una opción'}</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>{option.label}</option>
        ))}
      </select>
    );
  }
  if (field.type === 'textarea') {
    return <textarea {...common} rows={3} value={value ?? ''} onChange={handle} maxLength={field.maxLength} placeholder={field.placeholder} />;
  }
  return <TextControl field={field} value={value} onChange={handle} common={common} />;
}
