function ProdutoForm({ categorias, onSubmit }) {
    return (
        <form className="produto-form" onSubmit={onSubmit}>

            <div className="form-group">
                <label htmlFor="produto-nome">
                    Nome
                </label>

                <input
                    id="produto-nome"
                    type="text"
                    name="nome"
                    placeholder="Ex: Coca-Cola 2L"
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="preco-compra">
                    Preço de compra
                </label>

                <input
                    id="preco-compra"
                    type="number"
                    name="precoCompra"
                    step="0.01"
                    min="0"
                    placeholder="0,00"
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="preco-venda">
                    Preço de venda
                </label>

                <input
                    id="preco-venda"
                    type="number"
                    name="precoVenda"
                    step="0.01"
                    min="0"
                    placeholder="0,00"
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="categoria">
                    Categoria
                </label>

                <select
                    id="categoria"
                    name="categoriaId"
                    required
                    defaultValue=""
                >
                    <option value="" disabled>
                        Selecione uma categoria
                    </option>

                    {categorias.map((categoria) => (
                        <option
                            key={categoria.id}
                            value={categoria.id}
                        >
                            {categoria.nome}
                        </option>
                    ))}
                </select>
            </div>

            <button
                className="button-primary"
                type="submit"
            >
                Criar produto
            </button>

        </form>
    );
}

export default ProdutoForm;