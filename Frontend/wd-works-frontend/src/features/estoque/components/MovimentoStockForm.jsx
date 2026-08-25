import { useEffect, useState } from "react";

import { listarProdutos } from "../../produtos/services/produtoService";
import { criarMovimentoStock } from "../services/MovimentoStockService";
import "../style/Estoque.css"

function MovimentoStockForm() {

    const [produtos, setProdutos] = useState([]);
    const [produtoId, setProdutoId] = useState("");
    const [produtoPesquisa, setProdutoPesquisa] = useState("");
    const [produtoSelecionado, setProdutoSelecionado] = useState(null);
    const [mostrarProdutos, setMostrarProdutos] = useState(false);
    const [acao, setAcao] = useState("ENTRADA");
    const [quantidade, setQuantidade] = useState("");
    const [quantidadeAjuste, setQuantidadeAjuste] = useState("");
    const [descricao, setDescricao] = useState("");

    const [carregando, setCarregando] = useState(true);
    const [enviando, setEnviando] = useState(false);

    const [erro, setErro] = useState("");
    const [sucesso, setSucesso] = useState("");

    /*
     * Carregar produtos
     */
    useEffect(() => {

        async function carregarProdutos() {

            try {

                const data =
                    await listarProdutos();

                setProdutos(
                    data.content
                );

            } catch (error) {

                console.error(
                    "Erro ao carregar produtos:",
                    error
                );

                setErro(
                    "Não foi possível carregar os produtos."
                );

            } finally {

                setCarregando(false);

            }
        }

        carregarProdutos();

    }, []);

    const produtosFiltrados = produtos.filter((produto) =>
        produto.nome
            .toLowerCase()
            .includes(produtoPesquisa.toLowerCase())
    );


    /*
     * Alterar ação
     */
    function handleAcao(event) {

        const novaAcao =
            event.target.value;

        setAcao(novaAcao);

        /*
         * Limpamos os campos que não
         * pertencem à nova operação.
         */
        setQuantidade("");
        setQuantidadeAjuste("");

        setErro("");
        setSucesso("");
    }


    /*
     * Enviar formulário
     */
    async function handleSubmit(event) {

        event.preventDefault();

        setErro("");
        setSucesso("");


        /*
         * Validação do produto
         */
        if (!produtoId) {

            setErro(
                "Selecione um produto."
            );

            return;
        }


        /*
         * ENTRADA, SAIDA e DEVOLUCAO
         */
        if (
            acao === "ENTRADA" ||
            acao === "SAIDA" ||
            acao === "DEVOLUCAO"
        ) {

            if (
                !quantidade ||
                Number(quantidade) <= 0
            ) {

                setErro(
                    "Informe uma quantidade maior que zero."
                );

                return;
            }
        }


        /*
         * AJUSTE
         */
        if (acao === "AJUSTE") {

            if (
                quantidadeAjuste === "" ||
                Number(quantidadeAjuste) < 0
            ) {

                setErro(
                    "Informe uma quantidade de ajuste válida."
                );

                return;
            }
        }


        try {

            setEnviando(true);

            const movimento = {
                acao,
                produtoId: Number(produtoId),
                descricao: descricao.trim() || null,
            };


            /*
             * Quantidade normal
             */
            if (
                acao === "ENTRADA" ||
                acao === "SAIDA" ||
                acao === "DEVOLUCAO"
            ) {

                movimento.quantidade =
                    Number(quantidade);

            }


            /*
             * Quantidade do ajuste
             */
            if (acao === "AJUSTE") {

                movimento.quantidadeAjuste =
                    Number(quantidadeAjuste);

            }


            await criarMovimentoStock(
                movimento
            );

            window.dispatchEvent(
                new CustomEvent("stock:atualizado", {
                    detail: {
                        produtoId: Number(produtoId),
                        acao,
                        quantidade:
                            acao === "AJUSTE"
                                ? Number(quantidadeAjuste)
                                : Number(quantidade)
                    }
                }));
            /*
             * Limpar formulário
             */
            setProdutoId("");
            setProdutoPesquisa("");
            setProdutoSelecionado(null);
            setMostrarProdutos(false);
            setAcao("ENTRADA");
            setQuantidade("");
            setQuantidadeAjuste("");
            setDescricao("");


            setSucesso(
                "Movimentação de stock registrada com sucesso."
            );

        } catch (error) {

            console.error(
                "Erro ao criar movimento de stock:",
                error
            );

            /*
             * Se o backend devolver uma mensagem
             * específica, tentamos mostrá-la.
             */
            const mensagem =
                error.response?.data?.message;

            setErro(
                mensagem ||
                "Não foi possível registrar a movimentação de stock."
            );

        } finally {

            setEnviando(false);

        }
    }


    if (carregando) {

        return (
            <p>
                Carregando produtos...
            </p>
        );

    }


    return (

        <form
            className="movimento-stock-form"
            onSubmit={handleSubmit}
        >

            <div className="section-header">

                <div>

                    <h2>
                        Registrar movimentação
                    </h2>

                    <p>
                        Registre entradas, saídas,
                        devoluções ou ajustes de stock.
                    </p>

                </div>

            </div>


            {/* PRODUTO */}

            <div className="form-group">

                <label htmlFor="produto">
                    Produto
                </label>
                <div className="form-group produto-search-group">

                    <label htmlFor="produto">
                        Produto
                    </label>

                    <div className="produto-search">

                        <input
                            id="produto"
                            type="text"
                            value={produtoPesquisa}
                            onChange={(event) => {

                                setProdutoPesquisa(
                                    event.target.value
                                );

                                setProdutoSelecionado(null);
                                setProdutoId("");

                                setMostrarProdutos(true);
                            }}
                            onFocus={() => {
                                setMostrarProdutos(true);
                            }}
                            placeholder="Pesquisar produto..."
                            autoComplete="off"
                        />

                        {mostrarProdutos &&
                            produtoPesquisa.trim() !== "" &&
                            produtosFiltrados.length > 0 && (

                                <div className="produto-resultados">

                                    {produtosFiltrados.map(
                                        (produto) => (

                                            <button
                                                key={produto.id}
                                                type="button"
                                                className="produto-resultado"
                                                onClick={() => {

                                                    setProdutoId(
                                                        produto.id
                                                    );

                                                    setProdutoPesquisa(
                                                        produto.nome
                                                    );

                                                    setProdutoSelecionado(
                                                        produto
                                                    );

                                                    setMostrarProdutos(
                                                        false
                                                    );

                                                    setErro("");
                                                }}
                                            >

                                                <span className="produto-resultado-nome">
                                                    {produto.nome}
                                                </span>

                                            </button>

                                        )
                                    )}

                                </div>
                            )}

                    </div>

                    {produtoSelecionado && (

                        <small className="produto-selecionado">
                            Produto selecionado:{" "}
                            <strong>
                                {produtoSelecionado.nome}
                            </strong>
                        </small>

                    )}

                </div>
            </div>


            {/* AÇÃO */}

            <div className="form-group">

                <label htmlFor="acao">
                    Tipo de movimentação
                </label>

                <select
                    id="acao"
                    value={acao}
                    onChange={handleAcao}
                >

                    <option value="ENTRADA">
                        Entrada
                    </option>

                    <option value="SAIDA">
                        Saída
                    </option>

                    <option value="DEVOLUCAO">
                        Devolução
                    </option>

                    <option value="AJUSTE">
                        Ajuste
                    </option>

                </select>

            </div>


            {/* QUANTIDADE NORMAL */}

            {acao !== "AJUSTE" && (

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
                            setQuantidade(
                                event.target.value
                            )
                        }
                        placeholder="Ex: 20"
                    />

                </div>

            )}


            {/* QUANTIDADE DO AJUSTE */}

            {acao === "AJUSTE" && (

                <div className="form-group">

                    <label htmlFor="quantidade-ajuste">
                        Nova quantidade em stock
                    </label>

                    <input
                        id="quantidade-ajuste"
                        type="number"
                        min="0"
                        value={quantidadeAjuste}
                        onChange={(event) =>
                            setQuantidadeAjuste(
                                event.target.value
                            )
                        }
                        placeholder="Ex: 50"
                    />

                    <small>
                        O ajuste define diretamente
                        a quantidade existente no stock.
                    </small>

                </div>

            )}


            {/* DESCRIÇÃO */}

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
                        setDescricao(
                            event.target.value
                        )
                    }
                    placeholder="Ex: Entrada de mercadoria"
                />

            </div>


            {/* ERRO */}

            {erro && (

                <p className="form-error">
                    {erro}
                </p>

            )}


            {/* SUCESSO */}

            {sucesso && (

                <p className="form-success">
                    {sucesso}
                </p>

            )}


            {/* BOTÃO */}

            <button
                className="button-primary"
                type="submit"
                disabled={enviando}
            >

                {enviando
                    ? "Registrando..."
                    : "Registrar movimentação"}

            </button>

        </form>

    );
}

export default MovimentoStockForm;