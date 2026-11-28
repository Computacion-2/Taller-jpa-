import { useMemo, useState } from 'react';
import EntityForm from './EntityForm';
import Button, { IconButton } from '../ui/Button';
import DataTable from '../ui/DataTable';
import Modal, { ConfirmDialog } from '../ui/Modal';
import { Alert } from '../ui/Feedback';
import { PageHeader } from '../ui/Layout';
import { useCrud } from '../../hooks/useCrud';
import { useToast } from '../../hooks/useToast';
import { getErrorMessage } from '../../api/errors';
import { toFormValues, toRequestBody } from '../../utils/formMapping';

const always = () => true;

function messages({ singular, female = false }) {
  const name = singular.charAt(0).toUpperCase() + singular.slice(1);
  const suffix = female ? 'a' : 'o';
  return {
    create: `Nuev${suffix} ${singular}`,
    edit: `Editar ${singular}`,
    created: `${name} cread${suffix} correctamente.`,
    updated: `${name} actualizad${suffix} correctamente.`,
    deleted: `${name} eliminad${suffix} correctamente.`,
    confirm: `¿Eliminar est${female ? 'a' : 'e'} ${singular}? Esta acción no se puede deshacer.`,
  };
}

/**
 * Pagina CRUD completa y reutilizable: tabla + crear/editar en modal + eliminar con confirmacion.
 * Cada pagina de entidad solo declara columnas, campos y reglas propias.
 *
 * - fields: lista o funcion (item) => lista de campos del formulario.
 * - defaults: valores iniciales al crear. transform(body, item) ajusta el cuerpo antes de enviarlo.
 * - filter(row): filtra filas (p. ej. abonos de una deuda). onChange(): se llama tras cada cambio.
 * - variant="section" la muestra como bloque dentro de otra pagina.
 */
export default function CrudPage({
  title, subtitle, entity, service, columns, fields, defaults, transform, validate, filter,
  canCreate = true, canEdit = always, canDelete = always, rowActions, headerActions, onChange,
  emptyMessage, toolbar, variant = 'page',
}) {
  const { items, loading, error, reload, create, update, remove } = useCrud(service);
  const toast = useToast();
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);
  const [removing, setRemoving] = useState(false);
  const text = messages(entity);

  const rows = useMemo(() => (filter ? items.filter(filter) : items), [items, filter]);
  const resolveFields = (item) => (typeof fields === 'function' ? fields(item) : fields);
  const formFields = editing ? resolveFields(editing.item) : [];

  const save = async (values) => {
    const base = toRequestBody(formFields, values);
    const body = transform ? transform(base, editing.item) : base;
    if (editing.item) {
      await update(editing.item, body);
      toast.success(text.updated);
    } else {
      await create(body);
      toast.success(text.created);
    }
    setEditing(null);
    onChange?.();
  };

  const confirmDelete = async () => {
    setRemoving(true);
    try {
      await remove(deleting);
      toast.success(text.deleted);
      setDeleting(null);
      onChange?.();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setRemoving(false);
    }
  };

  const renderActions = (row) => (
    <div className="row-actions">
      {rowActions?.(row)}
      {canEdit(row) && <IconButton icon="edit" label="Editar" onClick={() => setEditing({ item: row })} />}
      {canDelete(row) && <IconButton icon="trash" label="Eliminar" variant="danger" onClick={() => setDeleting(row)} />}
    </div>
  );

  const createButton = canCreate && (
    <Button icon="plus" onClick={() => setEditing({ item: null })}>{text.create}</Button>
  );

  return (
    <section className={variant === 'section' ? 'crud-section' : 'page'}>
      {variant === 'page' ? (
        <PageHeader title={title} subtitle={subtitle} actions={<>{headerActions}{createButton}</>} />
      ) : (
        <div className="crud-section__header">
          <div>
            <h2 className="crud-section__title">{title}</h2>
            {subtitle && <p className="crud-section__subtitle">{subtitle}</p>}
          </div>
          <div className="page-header__actions">{headerActions}{createButton}</div>
        </div>
      )}

      {error && (
        <Alert tone="danger" title="No se pudieron cargar los datos" action={<Button variant="secondary" size="sm" icon="refresh" onClick={reload}>Reintentar</Button>}>
          {getErrorMessage(error)}
        </Alert>
      )}

      <DataTable
        columns={columns}
        rows={rows}
        rowKey={service.keyOf}
        loading={loading}
        actions={renderActions}
        emptyMessage={emptyMessage}
        toolbar={toolbar}
      />

      {editing && (
        <Modal title={editing.item ? text.edit : text.create} onClose={() => setEditing(null)} size="lg">
          <EntityForm
            fields={formFields}
            initialValues={toFormValues(formFields, editing.item, defaults)}
            validate={validate}
            onSubmit={save}
            onCancel={() => setEditing(null)}
            isEdit={Boolean(editing.item)}
            submitLabel={editing.item ? 'Guardar cambios' : 'Crear'}
          />
        </Modal>
      )}

      {deleting && (
        <ConfirmDialog
          title={`Eliminar ${entity.singular}`}
          message={text.confirm}
          loading={removing}
          onConfirm={confirmDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </section>
  );
}
