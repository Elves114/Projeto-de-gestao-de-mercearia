import { Link } from "react-router-dom";

function VendaTabela({ vendas, onCancelar }) {

    if (vendas.length === 0) {
        return (
            <div className="empty-state">
                <p>Nenhuma venda encontrada.</p>

                <Link
                    to="/vendas/nova"
                    className="button-primary"
                >
                    + Registar primeira venda
                </Link>
            </div>
        );
    }

    return (
        <div className="table-container">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Data</th>
                        <th>Funcionário</th>
                        <th>Total</th>
                        <th>Lucro</th>
                        <th>Estado</th>
                        <th>Ações</th>
                    </tr>
                </thead>

                <tbody>
                    {vendas.map((venda) => {

                        const cancelada =
                            venda.status === "CANCELADA";

                        return (
                            <tr
                                key={venda.id}
                                className={
                                    cancelada
                                        ? "venda-row venda-row-cancelada"
                                        : "venda-row"
                                }
                            >
                                <td>
                                    <strong>#{venda.id}</strong>
                                </td>

                                <td>
                                    {new Date(
                                        venda.dataVenda
                                    ).toLocaleString("pt-MZ")}
                                </td>

                                <td>{venda.usuarioNome}</td>

                                <td>
                                    {Number(
                                        venda.total
                                    ).toLocaleString("pt-MZ", {
                                        minimumFractionDigits: 2,
                                        maximumFractionDigits: 2,
                                    }) + " MT"}
                                </td>

                                <td>
                                    {Number(
                                        venda.lucroTotal
                                    ).toLocaleString("pt-MZ", {
                                        minimumFractionDigits: 2,
                                        maximumFractionDigits: 2,
                                    }) + " MT"}
                                </td>

                                <td>
                                    {cancelada ? (
                                        <span className="venda-status venda-status-cancelada">
                                            Cancelada
                                        </span>
                                    ) : (
                                        <span className="venda-status venda-status-concluida">
                                            Concluída
                                        </span>
                                    )}
                                </td>

                                <td>
                                    <div className="venda-actions">

                                        <Link
                                            to={`/vendas/${venda.id}`}
                                            className="table-action"
                                        >
                                            Ver
                                        </Link>

                                        {!cancelada && onCancelar && (
                                            <button
                                                type="button"
                                                className="venda-cancelar-btn"
                                                onClick={() =>
                                                    onCancelar(venda)
                                                }
                                            >
                                                Cancelar
                                            </button>
                                        )}

                                    </div>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}

export default VendaTabela;