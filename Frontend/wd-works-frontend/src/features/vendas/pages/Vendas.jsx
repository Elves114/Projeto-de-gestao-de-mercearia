import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { listarVendas } from "../services/vendaService";
import VendaTabela from "../components/VendaTabela";

import "../style/Venda.css";


function Vendas() {

    /*
     * ============================================================
     * VENDAS
     * ============================================================
     */

    const [vendas, setVendas] = useState([]);


    /*
     * ============================================================
     * PAGINAÇÃO
     * ============================================================
     */

    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [totalVendas, setTotalVendas] = useState(0);

    const tamanhoPagina = 10;


    /*
     * ============================================================
     * ESTADO
     * ============================================================
     */

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);


    /*
     * ============================================================
     * FILTROS
     * ============================================================
     */

    const [vendaId, setVendaId] = useState("");

    const [dataInicio, setDataInicio] = useState("");
    const [horaInicio, setHoraInicio] = useState("");

    const [dataFim, setDataFim] = useState("");
    const [horaFim, setHoraFim] = useState("");


    /*
     * Filtros que realmente estão sendo utilizados
     * na pesquisa.
     *
     * Isso permite ao usuário preencher os campos
     * e somente pesquisar quando clicar no botão.
     */

    const [filtrosAplicados, setFiltrosAplicados] =
        useState({});


    /*
     * ============================================================
     * CARREGAR VENDAS
     * ============================================================
     */

    useEffect(() => {

        async function carregar() {

            try {

                setCarregando(true);
                setErro(null);


                const data =
                    await listarVendas(
                        pagina,
                        tamanhoPagina,
                        filtrosAplicados
                    );


                /*
                 * Conteúdo da página atual.
                 */

                setVendas(
                    data.content || []
                );


                /*
                 * IMPORTANTE:
                 *
                 * No teu backend o Spring retorna:
                 *
                 * {
                 *     content: [],
                 *     page: {
                 *         totalPages: 2,
                 *         totalElements: 15
                 *     }
                 * }
                 */

                setTotalPaginas(
                    data.page?.totalPages || 0
                );


                setTotalVendas(
                    data.page?.totalElements || 0
                );


                /*
                 * Caso o usuário esteja numa página
                 * que deixou de existir depois de aplicar
                 * um filtro.
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
                    "Erro ao carregar vendas:",
                    error
                );

                setErro(
                    "Não foi possível carregar as vendas."
                );

            } finally {

                setCarregando(false);
            }
        }


        carregar();

    }, [
        pagina,
        filtrosAplicados
    ]);


    /*
     * ============================================================
     * PESQUISAR
     * ============================================================
     */

    function pesquisar() {

        /*
         * Validação do ID.
         */

        if (
            vendaId.trim() !== "" &&
            !/^\d+$/.test(vendaId.trim())
        ) {

            setErro(
                "O número da venda deve ser um número válido."
            );

            return;
        }


        /*
         * Construímos os valores de data/hora.
         *
         * Exemplo:
         *
         * data = 2026-08-15
         * hora = 14:30
         *
         * resultado:
         *
         * 2026-08-15T14:30:00
         */

        let inicio = null;
        let fim = null;


        if (dataInicio) {

            inicio =
                `${dataInicio}T${horaInicio || "00:00"}:00`;
        }


        if (dataFim) {

            fim =
                `${dataFim}T${horaFim || "23:59"}:59`;
        }


        /*
         * Se houver data inicial e final,
         * verificamos se o intervalo é válido.
         */

        if (
            inicio &&
            fim &&
            new Date(inicio) > new Date(fim)
        ) {

            setErro(
                "A data/hora inicial não pode ser maior que a final."
            );

            return;
        }


        /*
         * Aplicamos os filtros.
         */

        setPagina(0);

        setFiltrosAplicados({

            ...(vendaId.trim() && {
                vendaId: Number(
                    vendaId.trim()
                )
            }),

            ...(inicio && {
                inicio
            }),

            ...(fim && {
                fim
            })
        });
    }


    /*
     * ============================================================
     * LIMPAR FILTROS
     * ============================================================
     */

    function limparFiltros() {

        setVendaId("");

        setDataInicio("");
        setHoraInicio("");

        setDataFim("");
        setHoraFim("");

        setFiltrosAplicados({});

        setPagina(0);

        setErro(null);
    }


    /*
     * ============================================================
     * PAGINAÇÃO
     * ============================================================
     */

    function paginaAnterior() {

        if (pagina > 0) {

            setPagina(
                atual => atual - 1
            );
        }
    }


    function proximaPagina() {

        if (
            pagina <
            totalPaginas - 1
        ) {

            setPagina(
                atual => atual + 1
            );
        }
    }


    /*
     * ============================================================
     * CARREGANDO
     * ============================================================
     */

    if (carregando) {

        return (

            <div className="page-container">

                <p>
                    Carregando vendas...
                </p>

            </div>
        );
    }


    /*
     * ============================================================
     * ERRO
     * ============================================================
     */

    if (erro) {

        return (

            <div className="page-container">

                <p>
                    {erro}
                </p>

                <button
                    type="button"
                    onClick={() => setErro(null)}
                >
                    Voltar
                </button>

            </div>
        );
    }


    /*
     * ============================================================
     * INTERFACE
     * ============================================================
     */

    return (

        <div className="page-container">


            {/* ================================================= */}
            {/* CABEÇALHO */}
            {/* ================================================= */}

            <div className="page-header">

                <div>

                    <h1>
                        Vendas
                    </h1>

                    <p>
                        Histórico das vendas realizadas pela empresa.
                    </p>

                </div>


                <Link
                    to="/vendas/nova"
                    className="button-primary"
                >
                    + Nova venda
                </Link>

            </div>


            {/* ================================================= */}
            {/* CONTEÚDO */}
            {/* ================================================= */}

            <section className="content-card">


                {/* ================================================= */}
                {/* CABEÇALHO DA SECÇÃO */}
                {/* ================================================= */}

                <div className="content-card-header">

                    <div>

                        <h2>
                            Histórico de vendas
                        </h2>

                        <p>
                            Pesquise uma venda por número ou período.
                        </p>

                    </div>

                </div>


                {/* ================================================= */}
                {/* FILTROS */}
                {/* ================================================= */}

                <div className="vendas-filtros">


                    {/* ================================================= */}
                    {/* NÚMERO DA VENDA */}
                    {/* ================================================= */}

                    <div className="filtro-group">

                        <label htmlFor="vendaId">
                            Nº da venda
                        </label>

                        <input
                            id="vendaId"
                            type="text"
                            inputMode="numeric"
                            placeholder="Ex.: 487"
                            value={vendaId}
                            onChange={(event) =>
                                setVendaId(
                                    event.target.value
                                )
                            }
                        />

                    </div>


                    {/* ================================================= */}
                    {/* DATA INICIAL */}
                    {/* ================================================= */}

                    <div className="filtro-group">

                        <label htmlFor="dataInicio">
                            Data inicial
                        </label>

                        <input
                            id="dataInicio"
                            type="date"
                            value={dataInicio}
                            onChange={(event) =>
                                setDataInicio(
                                    event.target.value
                                )
                            }
                        />

                    </div>


                    {/* ================================================= */}
                    {/* HORA INICIAL */}
                    {/* ================================================= */}

                    <div className="filtro-group">

                        <label htmlFor="horaInicio">
                            Hora inicial
                        </label>

                        <input
                            id="horaInicio"
                            type="time"
                            value={horaInicio}
                            onChange={(event) =>
                                setHoraInicio(
                                    event.target.value
                                )
                            }
                        />

                    </div>


                    {/* ================================================= */}
                    {/* DATA FINAL */}
                    {/* ================================================= */}

                    <div className="filtro-group">

                        <label htmlFor="dataFim">
                            Data final
                        </label>

                        <input
                            id="dataFim"
                            type="date"
                            value={dataFim}
                            onChange={(event) =>
                                setDataFim(
                                    event.target.value
                                )
                            }
                        />

                    </div>


                    {/* ================================================= */}
                    {/* HORA FINAL */}
                    {/* ================================================= */}

                    <div className="filtro-group">

                        <label htmlFor="horaFim">
                            Hora final
                        </label>

                        <input
                            id="horaFim"
                            type="time"
                            value={horaFim}
                            onChange={(event) =>
                                setHoraFim(
                                    event.target.value
                                )
                            }
                        />

                    </div>


                    {/* ================================================= */}
                    {/* BOTÕES */}
                    {/* ================================================= */}

                    <div className="filtros-acoes">

                        <button
                            type="button"
                            className="button-primary"
                            onClick={pesquisar}
                        >
                            Pesquisar
                        </button>


                        <button
                            type="button"
                            className="button-secondary"
                            onClick={limparFiltros}
                        >
                            Limpar
                        </button>

                    </div>

                </div>


                {/* ================================================= */}
                {/* QUANTIDADE */}
                {/* ================================================= */}

                <div className="vendas-resumo">

                    <span>
                        {totalVendas} venda
                        {totalVendas !== 1 ? "s" : ""}
                        encontrada
                        {totalVendas !== 1 ? "s" : ""}
                    </span>

                </div>


                {/* ================================================= */}
                {/* TABELA */}
                {/* ================================================= */}

                <VendaTabela
                    vendas={vendas}
                />


                {/* ================================================= */}
                {/* PAGINAÇÃO */}
                {/* ================================================= */}

                {totalPaginas > 1 && (

                    <div className="pagination">

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


export default Vendas;

