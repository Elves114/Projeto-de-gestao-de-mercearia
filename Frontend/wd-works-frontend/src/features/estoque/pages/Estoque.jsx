
import { useEffect, useState } from "react";
import { listarEstoque } from "../services/estoqueService";
import EstoqueTable from "../components/EstoqueTable";
import { Link } from "react-router-dom";
import "../style/Estoque.css";


function Estoque() {

    const [estoques, setEstoques] = useState([]);

    /*
     * Página atual.
     *
     * O Spring começa em 0:
     *
     * pagina 0 = página 1
     * pagina 1 = página 2
     * pagina 2 = página 3
     */
    const [pagina, setPagina] = useState(0);

    /*
     * Total de produtos encontrados
     * pelo backend.
     */
    const [totalEstoques, setTotalEstoques] = useState(0);

    /*
     * Total de páginas retornadas pelo backend.
     */
    const [totalPaginas, setTotalPaginas] = useState(0);

    const [carregando, setCarregando] = useState(true);

    const [erro, setErro] = useState("");


    /*
     * =========================================================
     * ATUALIZAR ESTOQUE
     * =========================================================
     */

    function atualizarEstoque(estoqueAtualizado) {

        setEstoques((estoquesAtuais) =>
            estoquesAtuais.map((estoque) =>
                estoque.id === estoqueAtualizado.id
                    ? estoqueAtualizado
                    : estoque
            )
        );

    }


    /*
     * =========================================================
     * CARREGAR ESTOQUE
     * =========================================================
     */

    useEffect(() => {

        let ativo = true;


        async function carregar() {

            try {

                setCarregando(true);

                setErro("");


                /*
                 * Envia a página atual para o backend.
                 */
                const data = await listarEstoque(
                    pagina,
                    10
                );


                console.log(
                    "RESPOSTA DA API DO ESTOQUE:",
                    data
                );


                if (!ativo) {
                    return;
                }


                /*
                 * Produtos da página atual.
                 */
                setEstoques(
                    data.content || []
                );


                /*
                 * IMPORTANTE:
                 *
                 * A paginação do Spring está dentro
                 * de data.page.
                 */
                setTotalEstoques(
                    data.page?.totalElements || 0
                );


                setTotalPaginas(
                    data.page?.totalPages || 0
                );


                /*
                 * Caso a página atual deixe de existir.
                 *
                 * Exemplo:
                 *
                 * Estamos na página 3.
                 * Depois de uma alteração passam a existir
                 * apenas 2 páginas.
                 *
                 * Voltamos automaticamente para a página 2.
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
                    "Erro ao carregar estoque:",
                    error
                );


                if (!ativo) {
                    return;
                }


                setEstoques([]);

                setTotalEstoques(0);

                setTotalPaginas(0);

                setErro(
                    "Não foi possível carregar o estoque."
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

    }, [pagina]);


    /*
     * =========================================================
     * PÁGINA ANTERIOR
     * =========================================================
     */

    function paginaAnterior() {

        if (pagina > 0) {

            setPagina(
                (paginaAtual) =>
                    paginaAtual - 1
            );

        }

    }


    /*
     * =========================================================
     * PRÓXIMA PÁGINA
     * =========================================================
     */

    function proximaPagina() {

        if (
            pagina < totalPaginas - 1
        ) {

            setPagina(
                (paginaAtual) =>
                    paginaAtual + 1
            );

        }

    }


    /*
     * =========================================================
     * CARREGAMENTO INICIAL
     * =========================================================
     */

    if (
        carregando &&
        estoques.length === 0
    ) {

        return (
            <p>
                A carregar estoque...
            </p>
        );

    }


    /*
     * =========================================================
     * ERRO
     * =========================================================
     */

    if (erro) {

        return (
            <p>
                {erro}
            </p>
        );

    }


    return (

        <div className="estoque-page">


            {/* =================================================
                CABEÇALHO
            ================================================= */}

            <div className="page-header">

                <div>

                    <h1>
                        Stock
                    </h1>

                    <p>
                        Consulte a quantidade de produtos
                        disponível na sua empresa.
                    </p>

                </div>


                <Link
                    to="/estoque/entrada"
                    className="button button-primary"
                >
                    Adicionar Stock
                </Link>

            </div>


            {/* =================================================
                LISTAGEM
            ================================================= */}

            <section className="estoque-list-card">


                <div className="section-header">

                    <div>

                        <h2>
                            Stock atual
                        </h2>

                        <p>
                            Produtos e respectivas
                            quantidades disponíveis.
                        </p>

                    </div>


                    {/* TOTAL REAL */}

                    <span className="categoria-count">

                        {totalEstoques}

                    </span>

                </div>


                {/* =================================================
                    TABELA
                ================================================= */}

                {carregando ? (

                    <p>
                        A carregar estoque...
                    </p>

                ) : estoques.length === 0 ? (

                    <div className="empty-state">

                        <p>
                            Nenhum produto encontrado
                            no stock.
                        </p>

                    </div>

                ) : (

                    <EstoqueTable
                        estoques={estoques}
                        onAtualizar={atualizarEstoque}
                    />

                )}


                {/* =================================================
                    PAGINAÇÃO
                ================================================= */}

                {totalPaginas > 0 && (

                    <div className="produto-paginacao">

                        <button
                            type="button"
                            onClick={paginaAnterior}
                            disabled={
                                pagina === 0 ||
                                carregando
                            }
                        >
                            ← Anterior
                        </button>


                        <span>

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


            </section>

        </div>

    );

}


export default Estoque;

