import { ChevronLeft, ChevronRight } from "lucide-react";
import "./Table.css";

function Table({ columns, data, renderCell, emptyMessage = "Sem dados disponíveis" }) {
    return (
        <div className="ui-table-wrapper">
            {data.length === 0 ? (
                <div className="ui-table-empty" role="status">
                    <span>{emptyMessage}</span>
                </div>
            ) : (
                <div className="ui-table-scroll">
                    <table className="ui-table">
                        <thead>
                            <tr>
                                {columns.map((col) => (
                                    <th
                                        key={col.key}
                                        className={`ui-table-th ${col.className || ""}`}
                                        style={col.width ? { width: col.width } : undefined}
                                    >
                                        {col.label}
                                    </th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            {data.map((row, idx) => (
                                <tr key={row.id || idx} className="ui-table-tr">
                                    {columns.map((col) => (
                                        <td
                                            key={col.key}
                                            className={`ui-table-td ${col.cellClassName || ""}`}
                                        >
                                            {renderCell ? (
                                                renderCell(row, col)
                                            ) : (
                                                row[col.key]
                                            )}
                                        </td>
                                    ))}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

function Pagination({
    page,
    totalPages,
    totalItems,
    pageSize,
    onPageChange,
}) {
    if (totalPages <= 1) return null;

    const startItem = (page - 1) * pageSize + 1;
    const endItem = Math.min(page * pageSize, totalItems);

    return (
        <div className="ui-pagination" role="navigation" aria-label="Paginação">
            <span className="ui-pagination-info">
                {startItem}–{endItem} de {totalItems}
            </span>
            <div className="ui-pagination-controls">
                <button
                    type="button"
                    className="ui-pagination-btn"
                    disabled={page === 1}
                    onClick={() => onPageChange(page - 1)}
                    aria-label="Página anterior"
                >
                    <ChevronLeft size={16} aria-hidden="true" />
                </button>
                <span className="ui-pagination-page">
                    {page} de {totalPages}
                </span>
                <button
                    type="button"
                    className="ui-pagination-btn"
                    disabled={page === totalPages}
                    onClick={() => onPageChange(page + 1)}
                    aria-label="Página seguinte"
                >
                    <ChevronRight size={16} aria-hidden="true" />
                </button>
            </div>
        </div>
    );
}

export { Table, Pagination };
export default Table;
