import { useMemo, useState } from 'react';
import Icon from './Icon';
import Spinner from './Spinner';
import Pagination from './Pagination';
import { EmptyState } from './Feedback';
import { cx, normalize } from '../../utils/format';

function searchText(columns, row) {
  return normalize(columns
    .map((column) => (column.searchValue ? column.searchValue(row) : row[column.key]))
    .join(' '));
}

function Toolbar({ searchable, query, onSearch, children }) {
  if (!searchable && !children) {
    return null;
  }
  return (
    <div className="table-card__toolbar">
      {searchable && (
        <label className="search">
          <Icon name="search" size={18} />
          <span className="sr-only">Buscar</span>
          <input type="search" value={query} onChange={onSearch} placeholder="Buscar..." className="search__input" />
        </label>
      )}
      {children}
    </div>
  );
}

/**
 * Tabla reutilizable con busqueda, paginacion local, estados de carga/vacio y acciones por fila.
 * columns: [{ key, header, render?(row), align?, searchValue?(row) }]
 */
export default function DataTable({
  columns, rows, rowKey, loading = false, actions, pageSize = 8, searchable = true,
  emptyTitle = 'Sin registros', emptyMessage, toolbar,
}) {
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(0);

  const filtered = useMemo(() => {
    const term = normalize(query.trim());
    return term ? rows.filter((row) => searchText(columns, row).includes(term)) : rows;
  }, [rows, columns, query]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const currentPage = Math.min(page, totalPages - 1);
  const visible = filtered.slice(currentPage * pageSize, (currentPage + 1) * pageSize);

  const onSearch = (event) => {
    setQuery(event.target.value);
    setPage(0);
  };

  return (
    <div className="table-card">
      <Toolbar searchable={searchable} query={query} onSearch={onSearch}>{toolbar}</Toolbar>
      <div className="table-wrap">
        <table className="table">
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key} className={cx(column.align && `is-${column.align}`)} scope="col">{column.header}</th>
              ))}
              {actions && <th className="is-right" scope="col">Acciones</th>}
            </tr>
          </thead>
          <tbody>
            {visible.map((row) => (
              <tr key={rowKey(row)}>
                {columns.map((column) => (
                  <td key={column.key} className={cx(column.align && `is-${column.align}`)} data-label={column.header}>
                    {column.render ? column.render(row) : row[column.key] ?? '—'}
                  </td>
                ))}
                {actions && <td className="is-right table__actions">{actions(row)}</td>}
              </tr>
            ))}
          </tbody>
        </table>
        {loading && (
          <div className="table__loading"><Spinner /> <span>Cargando datos...</span></div>
        )}
        {!loading && visible.length === 0 && (
          <EmptyState
            icon={query ? 'search' : 'info'}
            title={query ? 'Sin coincidencias' : emptyTitle}
            message={query ? `No hay resultados para "${query}".` : emptyMessage}
          />
        )}
      </div>
      {filtered.length > pageSize && (
        <Pagination page={currentPage} totalPages={totalPages} totalItems={filtered.length} onChange={setPage} />
      )}
    </div>
  );
}
