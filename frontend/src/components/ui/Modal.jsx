import { useEffect, useId, useRef } from 'react';
import { createPortal } from 'react-dom';
import Button, { IconButton } from './Button';
import { cx } from '../../utils/format';

/** Ventana modal accesible: se cierra con Escape, con el fondo o con el boton X. */
export default function Modal({ title, description, onClose, children, size = 'md' }) {
  const titleId = useId();
  const dialogRef = useRef(null);
  const onCloseRef = useRef(onClose);

  // Se guarda la ultima funcion onClose sin volver a ejecutar el efecto de apertura.
  useEffect(() => {
    onCloseRef.current = onClose;
  }, [onClose]);

  // Al abrir: enfoca el primer campo, escucha Escape y bloquea el scroll del fondo.
  useEffect(() => {
    const previous = document.activeElement;
    const focusable = dialogRef.current?.querySelector('input:not([disabled]), select:not([disabled]), textarea, .modal__body button');
    focusable?.focus();
    const onKey = (event) => {
      if (event.key === 'Escape') {
        onCloseRef.current();
      }
    };
    document.addEventListener('keydown', onKey);
    document.body.classList.add('has-modal');
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.classList.remove('has-modal');
      previous?.focus?.();
    };
  }, []);

  return createPortal(
    <div className="modal">
      <button type="button" className="modal__backdrop" aria-label="Cerrar ventana" tabIndex={-1} onClick={onClose} />
      <div ref={dialogRef} className={cx('modal__dialog', `modal__dialog--${size}`)} role="dialog" aria-modal="true" aria-labelledby={titleId}>
        <div className="modal__header">
          <div>
            <h2 id={titleId} className="modal__title">{title}</h2>
            {description && <p className="modal__description">{description}</p>}
          </div>
          <IconButton icon="x" label="Cerrar" className="modal__close" onClick={onClose} />
        </div>
        <div className="modal__body">{children}</div>
      </div>
    </div>,
    document.body,
  );
}

/** Confirmacion para acciones destructivas (eliminar, retirar, desactivar). */
export function ConfirmDialog({
  title, message, confirmLabel = 'Eliminar', tone = 'danger', loading = false, onConfirm, onCancel,
}) {
  return (
    <Modal title={title} onClose={onCancel} size="sm">
      <p className="confirm__message">{message}</p>
      <div className="form__actions">
        <Button variant="secondary" onClick={onCancel} disabled={loading}>Cancelar</Button>
        <Button variant={tone} onClick={onConfirm} loading={loading}>{confirmLabel}</Button>
      </div>
    </Modal>
  );
}
