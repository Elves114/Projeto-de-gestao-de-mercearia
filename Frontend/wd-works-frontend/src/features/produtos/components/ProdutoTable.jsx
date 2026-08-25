function ProdutoTable({
    produtos,
    onEditar,
    onDesativar,
    produtoAnimando,
    produtoRemovendo
}) {

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

                        <th>Status</th>

                        <th>Ações</th>

                    </tr>

                </thead>

                <tbody>

                    {produtos.map((produto) => {

                        const estaAnimando =
                            produtoAnimando === produto.id;

                        const estaRemovendo =
                            produtoRemovendo === produto.id;

                        return (

                            <tr
                                key={produto.id}
                                className={`
                                    produto-row
                                    ${estaAnimando ? "produto-row-novo" : ""}
                                    ${estaRemovendo ? "produto-row-removendo" : ""}
                                `}
                            >

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

                                <td>

                                    <span
                                        className={
                                            produto.status === "ATIVO"
                                                ? "status-ativo"
                                                : "status-inativo"
                                        }
                                    >

                                        {produto.status === "ATIVO"
                                            ? "Ativo"
                                            : "Inativo"}

                                    </span>

                                </td>

                                <td>

                                    <button
                                        onClick={() =>
                                            onEditar(produto)
                                        }
                                        className="btn-sm btn-editar"
                                        type="button"
                                    >
                                        Editar
                                    </button>


                                    {produto.status === "ATIVO" && (

                                        <button
                                            onClick={() =>
                                                onDesativar(produto)
                                            }
                                            className="btn-sm btn-desativar"
                                            type="button"
                                        >
                                            Desativar
                                        </button>

                                    )}

                                </td>

                            </tr>

                        );

                    })}

                </tbody>

            </table>

        </div>
    );
}

export default ProdutoTable;