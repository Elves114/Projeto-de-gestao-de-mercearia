function ProdutoTable({ produtos }) {
    return (
        <div className="table-container">
            <table className="data-table">

                <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Categoria</th>
                        <th>Preço de compra</th>
                        <th>Preço de venda</th>
                        <th>Quantidade</th>
                    </tr>
                </thead>

                <tbody>
                    {produtos.map((produto) => (
                        <tr key={produto.id}>

                            <td className="produto-nome">
                                {produto.nome}
                            </td>

                            <td>
                                {produto.categoriaNome}
                            </td>

                            <td>
                                {produto.precoCompra}
                            </td>

                            <td className="produto-preco">
                                {produto.precoVenda}
                            </td>

                            <td>
                                <span className="produto-quantidade">
                                    {produto.quantidade}
                                </span>
                            </td>

                        </tr>
                    ))}
                </tbody>

            </table>
        </div>
    );
}

export default ProdutoTable;