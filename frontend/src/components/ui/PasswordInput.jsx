import { useState } from 'react';
import { IconButton } from './Button';

/** Campo de contrasena con boton para mostrar u ocultar el texto. */
export default function PasswordInput({ id, name, value, onChange, error, autoComplete = 'current-password', disabled }) {
  const [visible, setVisible] = useState(false);
  return (
    <div className="input-group">
      <input
        id={id}
        name={name}
        type={visible ? 'text' : 'password'}
        className="input"
        value={value}
        onChange={onChange}
        autoComplete={autoComplete}
        disabled={disabled}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${id}-error` : undefined}
      />
      <IconButton
        icon={visible ? 'eyeOff' : 'eye'}
        label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
        className="input-group__button"
        onClick={() => setVisible((current) => !current)}
      />
    </div>
  );
}
