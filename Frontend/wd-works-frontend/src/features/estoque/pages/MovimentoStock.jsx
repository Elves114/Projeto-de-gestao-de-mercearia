
import { useEffect, useState } from "react";

import {
    listarMovimentosStock
} from "../services/MovimentoStockService";

import "../style/MovimentoStock.css";


function MovimentosStock() {

    const [movimentos, setMovimentos] = useState([]);

    const [produtoPesquisa, setProdutoPesquisa] =
        useState("");

    const [pesquisaAplicada, setPesquisaAplicada] =
        useState("");

    const [pagina, setPagina] =
        useState(0);

    const [totalPaginas, setTotalPaginas] =
        useState(0);

    const [totalElementos, setTotalElementos] =
        useState(0);

    const [carregando, setCarregando] =
        useState(false);

    const [erro, setErro] =
        useState("");

    const tamanhoPagina = 10;


    /*
     * ============================================================
     * CARREGAR MOVIMENTOS
     * ============================================================
     */

    useEffect(() => {

        let ativo = true;

        async function carregar() {

            try {

                setCarregando(true);
                setErro("");

                const data =
                    await listarMovimentosStock(
                        pagina,
                        tamanhoPagina,
                        pesquisaAplicada
                    );

                if (!ativo) {
                    return;
                }


                /*
                 * Dados dos movimentos
                 */
                setMovimentos(
                    data.content || []
                );


                /*
                 * IMPORTANTE:
                 *
                 * A paginação vem dentro de "page".
                 */
                setTotalPaginas(
                    data.page?.totalPages || 0
                );

                setTotalElementos(
                    data.page?.totalElements || 0
                );


                /*
                 * Se a página atual deixar de existir,
                 * voltamos para a última página válida.
                 */
                if (
                    data.page?.totalPages > 0 &&
                    pagina >= data.page.totalPages
                ) {

                    setPagina(
                        data.page.totalPages - 1
                    );
                }

            } catch (error) {

                console.error(
                    "Erro ao carregar movimentos:",
                    error
                );

                if (!ativo) {
                    return;
                }

                setErro(
                    "Não foi possível carregar o histórico de stock."
                );

            } finally {

                if (ativo) {
                    setCarregando(false);
                }

            }
        }

        carregar();

        return () => {
            ativo = false;
        };

    }, [pagina, pesquisaAplicada]);


    /*
     * ============================================================
     * PESQUISAR
     * ============================================================
     */

    function pesquisar(event) {

        event.preventDefault();

        setPagina(0);

        setPesquisaAplicada(
            produtoPesquisa.trim()
        );
    }


    /*
     * ============================================================
     * LIMPAR PESQUISA
     * ============================================================
     */

    function limparPesquisa() {

        setProdutoPesquisa("");

        setPesquisaAplicada("");

        setPagina(0);
    }


    /*
     * ============================================================
     * PAGINAÇÃO
     * ============================================================
     */

    function paginaAnterior() {

        if (pagina > 0) {

            setPagina(
                (paginaAtual) =>
                    paginaAtual - 1
            );
        }
    }


    function proximaPagina() {

        if (
            pagina <
            totalPaginas - 1
        ) {

            setPagina(
                (paginaAtual) =>
                    paginaAtual + 1
            );
        }
    }


    /*
     * ============================================================
     * PÁGINA
     * ============================================================
     */

    return (

        <div className="movimentos-stock-page">

            <div className="page-header">

                <div>

                    <h1>
                        Histórico de Stock
                    </h1>

                    <p>
                        Consulte as movimentações de stock
                        realizadas na sua empresa.
                    </p>

                </div>

            </div>


            <section className="movimentos-stock-list-card">

                <div className="section-header">

                    <div>

                        <h2>
                            Movimentações
                        </h2>

                        <p>
                            Pesquise pelo nome do produto
                            para encontrar rapidamente
                            os movimentos.
                        </p>

                    </div>

                    <span className="categoria-count">
                        {totalElementos}
                    </span>

                </div>


                {/* =================================================
                    PESQUISA
                   ================================================= */}

                <form
                    className="movimentos-stock-pesquisa"
                    onSubmit={pesquisar}
                >

                    <div className="form-group">

                        <label htmlFor="produtoPesquisa">
                            Produto
                        </label>

                        <input
                            id="produtoPesquisa"
                            type="text"
                            value={produtoPesquisa}
                            onChange={(event) =>
                                setProdutoPesquisa(
                                    event.target.value
                                )
                            }
                            placeholder="Pesquisar produto..."
                        />

                    </div>


                    <div className="pesquisa-actions">

                        <button
                            type="submit"
                            className="button button-primary"
                            disabled={carregando}
                        >
                            Pesquisar
                        </button>


                        {pesquisaAplicada && (

                            <button
                                type="button"
                                className="button button-secondary"
                                onClick={limparPesquisa}
                            >
                                Limpar
                            </button>

                        )}

                    </div>

                </form>


                {/* =================================================
                    CARREGAMENTO
                   ================================================= */}

                {carregando ? (

                    <div className="empty-state">

                        <p>
                            A carregar histórico de stock...
                        </p>

                    </div>

                ) : erro ? (

                    <div>

                        <p className="form-error">
                            {erro}
                        </p>

                        <button
                            type="button"
                            className="button button-primary"
                            onClick={() => {
                                setPagina(
                                    (paginaAtual) =>
                                        paginaAtual
                                );
                            }}
                        >
                            Tentar novamente
                        </button>

                    </div>

                ) : movimentos.length === 0 ? (

                    <div className="empty-state">

                        <p>
                            {pesquisaAplicada
                                ? "Nenhuma movimentação encontrada para este produto."
                                : "Nenhuma movimentação de stock encontrada."
                            }
                        </p>

                    </div>

                ) : (

                    <>

                        <div className="table-container">

                            <table className="data-table">

                                <thead>

                                    <tr>

                                        <th>
                                            Data
                                        </th>

                                        <th>
                                            Produto
                                        </th>

                                        <th>
                                            Ação
                                        </th>

                                        <th>
                                            Quantidade
                                        </th>

                                        <th>
                                            Antes
                                        </th>

                                        <th>
                                            Depois
                                        </th>

                                        <th>
                                            Utilizador
                                        </th>

                                        <th>
                                            Descrição
                                        </th>

                                    </tr>

                                </thead>


                                <tbody>

                                    {movimentos.map(
                                        (movimento) => (

                                            <tr
                                                key={
                                                    movimento.id
                                                }
                                            >

                                                <td>
                                                    {formatarData(
                                                        movimento.data
                                                    )}
                                                </td>


                                                <td className="produto-nome">
                                                    {
                                                        movimento.produtoNome
                                                    }
                                                </td>


                                                <td>

                                                    <span
                                                        className={
                                                            `movimento-acao movimento-${movimento.acao.toLowerCase()}`
                                                        }
                                                    >
                                                        {
                                                            movimento.acao
                                                        }
                                                    </span>

                                                </td>


                                                <td>
                                                    {
                                                        movimento.quantidade
                                                    }
                                                </td>


                                                <td>
                                                    {
                                                        movimento.quantidadeAnterior
                                                    }
                                                </td>


                                                <td>
                                                    {
                                                        movimento.quantidadePosterior
                                                    }
                                                </td>


                                                <td>
                                                    {
                                                        movimento.usuarioNome
                                                    }
                                                </td>


                                                <td>
                                                    {
                                                        movimento.descricao ||
                                                        "-"
                                                    }
                                                </td>

                                            </tr>

                                        )
                                    )}

                                </tbody>

                            </table>

                        </div>


                        {/* =================================================
                            PAGINAÇÃO
                           ================================================= */}

                        {totalPaginas > 0 && (

                            <div className="pagination">

                                <button
                                    type="button"
                                    className="button button-secondary"
                                    onClick={paginaAnterior}
                                    disabled={
                                        pagina === 0 ||
                                        carregando
                                    }
                                >
                                    ← Anterior
                                </button>


                                <span className="pagination-info">

                                    Página{" "}

                                    <strong>
                                        {pagina + 1}
                                    </strong>

                                    {" "}de{" "}

                                    <strong>
                                        {totalPaginas}
                                    </strong>

                                </span>


                                <button
                                    type="button"
                                    className="button button-secondary"
                                    onClick={proximaPagina}
                                    disabled={
                                        pagina >=
                                            totalPaginas - 1 ||
                                        carregando
                                    }
                                >
                                    Próxima →
                                </button>

                            </div>

                        )}

                    </>

                )}

            </section>

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


export default MovimentosStock;

