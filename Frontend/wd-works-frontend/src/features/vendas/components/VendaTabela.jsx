import { Link } from "react-router-dom";

function VendaTabela({ vendas }) {
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
                        <th>Ações</th>
                    </tr>
                </thead>

                <tbody>
                    {vendas.map((venda) => (
                        <tr key={venda.id}>
                            <td>
                                <strong>
                                    #{venda.id}
                                </strong>
                            </td>

                            <td>
                                {new Date(
                                    venda.dataVenda
                                ).toLocaleString("pt-MZ")}
                            </td>

                            <td>
                                {venda.usuarioNome}
                            </td>

                            <td>
                                {Number(
                                    venda.total
                                ).toLocaleString(
                                    "pt-MZ",
                                    {
                                        style: "currency",
                                        currency: "MZN",
                                    }
                                )}
                            </td>

                            <td>
                                {Number(
                                    venda.lucroTotal
                                ).toLocaleString(
                                    "pt-MZ",
                                    {
                                        style: "currency",
                                        currency: "MZN",
                                    }
                                )}
                            </td>

                            <td>
                                <Link
                                    to={`/vendas/${venda.id}`}
                                    className="table-action"
                                >
                                    Ver detalhes
                                </Link>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default VendaTabela;