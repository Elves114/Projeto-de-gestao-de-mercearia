import { useEffect, useState } from "react";
import { pesquisarProdutos } from "../../produtos/services/produtoService";
import { obterMensagemErro } from "../../../services/api";
import "../style/NovaVenda.css";

function ItemVendaForm({ onAdicionar }) {
    const [pesquisa, setPesquisa] = useState("");
    const [produtos, setProdutos] = useState([]);
    const [produtoSelecionado, setProdutoSelecionado] = useState(null);
    const [quantidade, setQuantidade] = useState(1);

    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState("");

    useEffect(() => {
        if (!pesquisa.trim()) {
            return;
        }

        let ativo = true;

        const timeout = setTimeout(async () => {
            try {
                setCarregando(true);
                setErro("");

                const data = await pesquisarProdutos(
                    pesquisa.trim()
                );

                if (ativo) {
                    setProdutos(data.content);
                }
            } catch (error) {
                console.error(
                    "Erro ao pesquisar produtos:",
                    error
                );

                if (ativo) {
                    setErro(obterMensagemErro(error));
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }, 300);

        return () => {
            ativo = false;
            clearTimeout(timeout);
        };
    }, [pesquisa]);

    function selecionarProduto(produto) {
        setProdutoSelecionado(produto);
        setPesquisa("");
        setProdutos([]);
        setErro("");
    }

    function adicionarItem() {
        if (!produtoSelecionado) {
            setErro("Selecione um produto.");
            return;
        }

        if (quantidade <= 0) {
            setErro("A quantidade deve ser maior que zero.");
            return;
        }

        onAdicionar({
            produtoId: produtoSelecionado.id,
            produtoNome: produtoSelecionado.nome,
            quantidade: Number(quantidade),
            precoVenda: produtoSelecionado.precoVenda,
        });

        setProdutoSelecionado(null);
        setQuantidade(1);
        setPesquisa("");
        setErro("");
    }

    return (
        <div className="item-venda-form">

            <div className="product-search">

                <label htmlFor="pesquisa-produto">
                    Produto
                </label>

                <input
                    id="pesquisa-produto"
                    type="text"
                    value={pesquisa}
                    onChange={(event) => {
                        setPesquisa(event.target.value);
                        setProdutoSelecionado(null);
                    }}
                    placeholder="Pesquisar produto..."
                    disabled={!!produtoSelecionado}
                />

                {carregando && (
                    <span className="search-status">
                        Pesquisando...
                    </span>
                )}

                {produtos.length > 0 && (
                    <div className="product-search-results">

                        {produtos.map((produto) => (
                            <button
                                type="button"
                                className="product-search-result"
                                key={produto.id}
                                onClick={() =>
                                    selecionarProduto(produto)
                                }
                            >
                                <span>
                                    {produto.nome}
                                </span>

                                <span>
                                    {produto.precoVenda}
                                </span>
                            </button>
                        ))}

                    </div>
                )}

            </div>

            {produtoSelecionado && (
                <div className="selected-product">

                    <div>
                        <span className="selected-product-label">
                            Produto selecionado
                        </span>

                        <strong>
                            {produtoSelecionado.nome}
                        </strong>

                        <span>
                            Preço:{" "}
                            {produtoSelecionado.precoVenda}
                        </span>
                    </div>

                    <button
                        type="button"
                        onClick={() =>
                            setProdutoSelecionado(null)
                        }
                    >
                        Alterar
                    </button>

                </div>
            )}

            <div className="quantity-field">

                <label htmlFor="quantidade-produto">
                    Quantidade
                </label>

                <input
                    id="quantidade-produto"
                    type="number"
                    min="1"
                    value={quantidade}
                    onChange={(event) =>
                        setQuantidade(event.target.value)
                    }
                />

            </div>

            {erro && (
                <p className="form-error">
                    {erro}
                </p>
            )}

            <button
                type="button"
                className="add-product-button"
                onClick={adicionarItem}
                disabled={!produtoSelecionado}
            >
                + Adicionar produto
            </button>

        </div>
    );
}

export default ItemVendaForm;