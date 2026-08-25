import { useEffect, useState } from "react";

import {
    listarMinhasAuditorias,
    listarAuditoriasPorTipo,
    listarAuditoriasPorGravidade,
} from "../services/auditoriaService";

import AuditoriaTabela from "../components/AuditoriaTabela";

import "../style/Auditoria.css";


function Auditoria() {

    const [auditorias, setAuditorias] = useState([]);

    const [tipo, setTipo] = useState("");
    const [gravidade, setGravidade] = useState("");

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [totalElementos, setTotalElementos] = useState(0);


    useEffect(() => {

        async function carregarAuditorias() {

            setCarregando(true);
            setErro(null);

            try {

                let data;

                /*
                 * ==================================================
                 * FILTRO POR GRAVIDADE
                 * ==================================================
                 */

                if (gravidade) {

                    data =
                        await listarAuditoriasPorGravidade(
                            gravidade,
                            pagina,
                            10
                        );

                }

                /*
                 * ==================================================
                 * FILTRO POR TIPO
                 * ==================================================
                 */

                else if (tipo) {

                    data =
                        await listarAuditoriasPorTipo(
                            tipo,
                            pagina,
                            10
                        );

                }

                /*
                 * ==================================================
                 * MINHAS AUDITORIAS
                 * ==================================================
                 */

                else {

                    data =
                        await listarMinhasAuditorias(
                            pagina,
                            10
                        );

                }


                /*
                 * ==================================================
                 * ATUALIZAR RESULTADOS
                 * ==================================================
                 */

                setAuditorias(
                    data.content || []
                );

                setTotalPaginas(
                    data.page?.totalPages || 0
                );

                setTotalElementos(
                    data.page?.totalElements || 0
                );


            } catch (error) {

                console.error(error);

                setErro(
                    "Não foi possível carregar as auditorias."
                );

            } finally {

                setCarregando(false);

            }

        }


        carregarAuditorias();

    }, [tipo, gravidade, pagina]);


    /*
     * ============================================================
     * FILTRO POR TIPO
     * ============================================================
     */

    function handleTipoChange(event) {

        const novoTipo =
            event.target.value;

        setTipo(novoTipo);

        /*
         * Sempre que mudar o filtro,
         * voltamos para a primeira página.
         */
        setPagina(0);
    }


    /*
     * ============================================================
     * FILTRO POR GRAVIDADE
     * ============================================================
     */

    function handleGravidadeChange(event) {

        const novaGravidade =
            event.target.value;

        setGravidade(novaGravidade);

        /*
         * Sempre que mudar o filtro,
         * voltamos para a primeira página.
         */
        setPagina(0);
    }


    /*
     * ============================================================
     * PAGINAÇÃO
     * ============================================================
     */

    function irParaPagina(numero) {

        if (
            numero >= 0 &&
            numero < totalPaginas
        ) {

            setPagina(numero);

        }

    }


    /*
     * ============================================================
     * LOADING
     * ============================================================
     */

    if (carregando) {

        return (
            <div className="auditoria-loading">

                <span />

                <p>
                    Carregando auditorias...
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
            <div className="auditoria-error">

                {erro}

            </div>
        );

    }


    /*
     * ============================================================
     * CONTADORES
     * ============================================================
     *
     * IMPORTANTE:
     *
     * Estes valores representam apenas os resultados
     * atualmente carregados na página.
     *
     * O total geral vem do backend através de
     * totalElementos.
     */

    const total =
        totalElementos;

    const totalInfo =
        auditorias.filter(
            auditoria =>
                auditoria.gravidade === "INFO"
        ).length;

    const totalWarning =
        auditorias.filter(
            auditoria =>
                auditoria.gravidade === "WARNING"
        ).length;

    const totalCritical =
        auditorias.filter(
            auditoria =>
                auditoria.gravidade === "CRITICAL"
        ).length;


    return (

        <div className="page-container auditoria-page">


            {/* ==================================================
                HEADER
            ================================================== */}

            <div className="page-header auditoria-page-header">

                <div>

                    <span className="auditoria-overline">
                        SEGURANÇA · RASTREABILIDADE
                    </span>

                    <h1>
                        Auditoria
                    </h1>

                    <p>
                        Histórico das atividades realizadas no sistema.
                    </p>

                </div>

            </div>


            {/* ==================================================
                RESUMO
            ================================================== */}

            <div className="auditoria-summary">


                <div className="auditoria-summary-card">

                    <span className="auditoria-summary-label">
                        Eventos
                    </span>

                    <strong>
                        {total}
                    </strong>

                    <span className="auditoria-summary-description">
                        Registos encontrados
                    </span>

                </div>


                <div className="auditoria-summary-card info">

                    <span className="auditoria-summary-label">
                        Informação
                    </span>

                    <strong>
                        {totalInfo}
                    </strong>

                    <span className="auditoria-summary-description">
                        Eventos informativos
                    </span>

                </div>


                <div className="auditoria-summary-card warning">

                    <span className="auditoria-summary-label">
                        Warning
                    </span>

                    <strong>
                        {totalWarning}
                    </strong>

                    <span className="auditoria-summary-description">
                        Eventos de atenção
                    </span>

                </div>


                <div className="auditoria-summary-card critical">

                    <span className="auditoria-summary-label">
                        Critical
                    </span>

                    <strong>
                        {totalCritical}
                    </strong>

                    <span className="auditoria-summary-description">
                        Eventos críticos
                    </span>

                </div>

            </div>


            {/* ==================================================
                CONTEÚDO
            ================================================== */}

            <div className="content-card auditoria-content-card">


                <div className="content-card-header auditoria-content-header">

                    <div>

                        <h2>
                            Histórico de auditoria
                        </h2>

                        <p>
                            Consulte e filtre as atividades registadas.
                        </p>

                    </div>


                    <span className="auditoria-event-count">

                        {total} eventos

                    </span>

                </div>


                {/* ==================================================
                    FILTROS
                ================================================== */}

                <div className="auditoria-filtros">


                    <div className="form-group">

                        <label htmlFor="tipo-auditoria">
                            Tipo de atividade
                        </label>

                        <select
                            id="tipo-auditoria"
                            value={tipo}
                            onChange={handleTipoChange}
                        >

                            <option value="">
                                Minhas auditorias
                            </option>

                            <option value="CRIACAO">
                                Criação
                            </option>

                            <option value="ALTERACAO">
                                Alteração
                            </option>

                            <option value="EXCLUSAO">
                                Exclusão
                            </option>

                            <option value="LOGIN">
                                Login
                            </option>

                            <option value="LOGOUT">
                                Logout
                            </option>

                            <option value="VENDA">
                                Venda
                            </option>

                        </select>

                    </div>


                    <div className="form-group">

                        <label htmlFor="gravidade-auditoria">
                            Gravidade
                        </label>

                        <select
                            id="gravidade-auditoria"
                            value={gravidade}
                            onChange={handleGravidadeChange}
                        >

                            <option value="">
                                Todas as gravidades
                            </option>

                            <option value="INFO">
                                Info
                            </option>

                            <option value="WARNING">
                                Warning
                            </option>

                            <option value="CRITICAL">
                                Critical
                            </option>

                        </select>

                    </div>

                </div>


                {/* ==================================================
                    TABELA
                ================================================== */}

                <AuditoriaTabela
                    auditorias={auditorias}
                />


                {/* ==================================================
                    PAGINAÇÃO
                ================================================== */}

                {totalPaginas > 1 && (

                    <div className="auditoria-paginacao">

                        <div className="auditoria-paginacao-info">
                            <span>
                                Página
                            </span>

                            <strong>
                                {pagina + 1}
                            </strong>

                            <span>
                                de {totalPaginas}
                            </span>
                        </div>


                        <div className="auditoria-paginacao-acoes">

                            <button
                                type="button"
                                className="auditoria-paginacao-btn"
                                disabled={pagina === 0}
                                onClick={() =>
                                    irParaPagina(pagina - 1)
                                }
                            >
                                <span className="auditoria-paginacao-icon">
                                    ←
                                </span>

                                Anterior
                            </button>


                            <button
                                type="button"
                                className="auditoria-paginacao-btn"
                                disabled={
                                    pagina >= totalPaginas - 1
                                }
                                onClick={() =>
                                    irParaPagina(pagina + 1)
                                }
                            >
                                Próxima

                                <span className="auditoria-paginacao-icon">
                                    →
                                </span>
                            </button>

                        </div>

                    </div>

                )}

            </div>

        </div>
    );
}

export default Auditoria;