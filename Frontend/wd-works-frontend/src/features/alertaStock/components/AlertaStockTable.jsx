function AlertaStockTable({ alertas }) {

    return (
        <div className="table-container">

            <table className="data-table">

                <thead>
                    <tr>
                        <th>Produto</th>
                        <th>Stock atual</th>
                        <th>Stock mínimo</th>
                        <th>Data do alerta</th>
                        <th>Estado</th>
                    </tr>
                </thead>

                <tbody>

                    {alertas.map((alerta) => (

                        <tr key={alerta.id}>

                            <td className="produto-nome">
                                {alerta.produtoNome}
                            </td>

                            <td>
                                {alerta.quantidadeAtual}
                            </td>

                            <td>
                                {alerta.quantidadeMinima}
                            </td>

                            <td>
                                {formatarData(alerta.criadoEm)}
                            </td>

                            <td>

                                <span
                                    className={
                                        alerta.ativo
                                            ? "alerta-status alerta-ativo"
                                            : "alerta-status alerta-resolvido"
                                    }
                                >
                                    {alerta.ativo
                                        ? "Ativo"
                                        : "Resolvido"
                                    }
                                </span>

                            </td>

                        </tr>

                    ))}

                </tbody>

            </table>

        </div>
    );
}


function formatarData(data) {

    if (!data) {
        return "-";
    }

    return new Date(data).toLocaleString(
        "pt-MZ"
    );
}


export default AlertaStockTable;