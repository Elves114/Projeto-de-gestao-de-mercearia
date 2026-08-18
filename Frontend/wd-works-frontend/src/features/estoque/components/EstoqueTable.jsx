function EstoqueTable({ estoques }) {
    return (
        <div className="table-container">
            <table className="data-table">

                <thead>
                    <tr>
                        <th>Produto</th>
                        <th>Quantidade</th>
                        <th>Quantidade mínima</th>
                    </tr>
                </thead>

                <tbody>
                    {estoques.map((estoque) => (
                        <tr key={estoque.id}>

                            <td className="produto-nome">
                                {estoque.produtoNome}
                            </td>

                            <td>
                                <span className="stock-quantidade">
                                    {estoque.quantidade}
                                </span>
                            </td>

                            <td>
                                {estoque.quantidadeMinima}
                            </td>

                        </tr>
                    ))}
                </tbody>

            </table>
        </div>
    );
}

export default EstoqueTable;