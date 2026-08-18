import { useEffect, useState } from "react";
import { listarProdutos } from "../../produtos/services/produtoService";
import { criarMovimentoStock } from "../services/movimentoStockService";

function MovimentoStockForm() {
    const [produtos, setProdutos] = useState([]);
    const [produtoId, setProdutoId] = useState("");
    const [quantidade, setQuantidade] = useState("");
    const [descricao, setDescricao] = useState("");

    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);
    const [erro, setErro] = useState(null);
    const [sucesso, setSucesso] = useState(false);

    useEffect(() => {
        async function carregarProdutos() {
            try {
                const data = await listarProdutos();

                setProdutos(data.content);
            } catch {
                setErro("Não foi possível carregar os produtos.");
            } finally {
                setCarregando(false);
            }
        }

        carregarProdutos();
    }, []);

    async function handleSubmit(event) {
        event.preventDefault();

        if (!produtoId || !quantidade) {
            setErro("Selecione um produto e informe a quantidade.");
            return;
        }

        try {
            setEnviando(true);
            setErro(null);
            setSucesso(false);

            await criarMovimentoStock({
                acao: "ENTRADA",
                quantidade: Number(quantidade),
                produtoId: Number(produtoId),
                descricao,
            });

            setProdutoId("");
            setQuantidade("");
            setDescricao("");

            setSucesso(true);
        } catch {
            setErro("Não foi possível registrar a entrada de stock.");
        } finally {
            setEnviando(false);
        }
    }

    if (carregando) {
        return <p>Carregando produtos...</p>;
    }

    return (
    <form
        className="movimento-stock-form"
        onSubmit={handleSubmit}
    >
        <div className="section-header">
            <div>
                <h2>Registrar entrada</h2>

                <p>
                    Adicione produtos ao stock da empresa.
                </p>
            </div>
        </div>

        <div className="form-group">
            <label htmlFor="produto">
                Produto
            </label>

            <select
                id="produto"
                value={produtoId}
                onChange={(event) =>
                    setProdutoId(event.target.value)
                }
            >
                <option value="">
                    Selecione um produto
                </option>

                {produtos.map((produto) => (
                    <option
                        key={produto.id}
                        value={produto.id}
                    >
                        {produto.nome}
                    </option>
                ))}
            </select>
        </div>

        <div className="form-group">
            <label htmlFor="quantidade">
                Quantidade
            </label>

            <input
                id="quantidade"
                type="number"
                min="1"
                value={quantidade}
                onChange={(event) =>
                    setQuantidade(event.target.value)
                }
                placeholder="Ex: 20"
            />
        </div>

        <div className="form-group">
            <label htmlFor="descricao">
                Descrição
            </label>

            <input
                id="descricao"
                type="text"
                maxLength="500"
                value={descricao}
                onChange={(event) =>
                    setDescricao(event.target.value)
                }
                placeholder="Ex: Entrada inicial de stock"
            />
        </div>

        {erro && (
            <p className="form-error">
                {erro}
            </p>
        )}

        {sucesso && (
            <p className="form-success">
                Entrada de stock registrada com sucesso!
            </p>
        )}

        <button
            className="button-primary"
            type="submit"
            disabled={enviando}
        >
            {enviando
                ? "Registrando..."
                : "Registrar entrada"}
        </button>
    </form>
);
}

export default MovimentoStockForm;