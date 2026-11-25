import { IconButton } from './Button';

/** Paginacion simple (page empieza en 0, igual que Spring Data). */
export default function Pagination({ page, totalPages, totalItems, onChange }) {
  return (
    <nav className="pagination" aria-label="Paginación">
      <span className="pagination__info">
        Página {page + 1} de {totalPages} · {totalItems} registros
      </span>
      <div className="pagination__controls">
        <IconButton icon="chevronLeft" label="Página anterior" disabled={page === 0} onClick={() => onChange(page - 1)} />
        <IconButton icon="chevronRight" label="Página siguiente" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)} />
      </div>
    </nav>
  );
}
