import { useCallback, useState } from 'react';
import { getErrorMessage, getFieldErrors } from '../api/errors';

function onlyErrors(errors) {
  return Object.fromEntries(Object.entries(errors ?? {}).filter(([, message]) => Boolean(message)));
}

/**
 * Estado de un formulario controlado: valores, errores por campo, error general y envio.
 * validate(values) devuelve { campo: mensaje }; los errores 400 del backend se asignan
 * automaticamente al campo correspondiente.
 */
export function useForm(initialValues, validate) {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const setFieldValue = useCallback((name, value) => {
    setValues((current) => ({ ...current, [name]: value }));
    setErrors((current) => (current[name] ? { ...current, [name]: undefined } : current));
  }, []);

  const handleChange = useCallback((event) => {
    const { name, value, type, checked } = event.target;
    setFieldValue(name, type === 'checkbox' ? checked : value);
  }, [setFieldValue]);

  const handleSubmit = (onSubmit) => async (event) => {
    event.preventDefault();
    const validationErrors = onlyErrors(validate ? validate(values) : {});
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) {
      return;
    }
    setSubmitting(true);
    setFormError(null);
    try {
      await onSubmit(values);
    } catch (error) {
      setErrors(getFieldErrors(error));
      setFormError(getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return { values, errors, formError, submitting, setValues, setFieldValue, handleChange, handleSubmit };
}
