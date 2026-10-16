import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { listarVendas, cancelarVenda } from "../services/vendaService";
import VendaTabela from "../components/VendaTabela";
import CancelarVendaModal from "../components/CancelarVendaModal";

import { obterMensagemErro } from "../../../services/api";

import "../style/Venda.css";

function Vendas() {

    /* ============================================================
       VENDAS
       ============================================================ */

    const [vendas, setVendas] = useState([]);

    /* ============================================================
       PAGINAÇÃO
       ============================================================ */

    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [totalVendas, setTotalVendas] = useState(0);

    const tamanhoPagina = 10;

    /* ============================================================
       ESTADO
       ============================================================ */

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    /* ============================================================
       FILTROS
       ============================================================ */

    const [vendaId, setVendaId] = useState("");

    const [dataInicio, setDataInicio] = useState("");
    const [horaInicio, setHoraInicio] = useState("");

    const [dataFim, setDataFim] = useState("");
    const [horaFim, setHoraFim] = useState("");

    /* ============================================================
       CANCELAMENTO
       ============================================================ */

    const [vendaParaCancelar, setVendaParaCancelar] = useState(null);
    const [cancelando, setCancelando] = useState(false);
    const [sucesso, setSucesso] = useState("");

    /* ============================================================
       FILTROS APLICADOS
       ============================================================ */

    const [filtrosAplicados, setFiltrosAplicados] = useState({});

    /* ============================================================
       CARREGAR VENDAS
       ============================================================ */

    useEffect(() => {

        async function carregar() {

            try {

                setCarregando(true);
                setErro(null);

                const data = await listarVendas(
                    pagina,
                    tamanhoPagina,
                    filtrosAplicados,
                );

                setVendas(data.content || []);

                setTotalPaginas(data.page?.totalPages || 0);
                setTotalVendas(data.page?.totalElements || 0);

                if (
                    data.page?.totalPages > 0 &&
                    pagina >= data.page.totalPages
                ) {
                    setPagina(data.page.totalPages - 1);
                }

            } catch (error) {

                console.error("Erro ao carregar vendas:", error);
                setErro(obterMensagemErro(error));

            } finally {
                setCarregando(false);
            }
        }

        carregar();

    }, [pagina, filtrosAplicados]);

    /* ============================================================
       PESQUISAR
       ============================================================ */

    function pesquisar() {

        if (vendaId.trim() !== "" && !/^\d+$/.test(vendaId.trim())) {
            setErro("O número da venda deve ser um número válido.");
            return;
        }

        let inicio = null;
        let fim = null;

        if (dataInicio) {
            inicio = `${dataInicio}T${horaInicio || "00:00"}:00`;
        }

        if (dataFim) {
            fim = `${dataFim}T${horaFim || "23:59"}:59`;
        }

        if (inicio && fim && new Date(inicio) > new Date(fim)) {
            setErro("A data/hora inicial não pode ser maior que a final.");
            return;
        }

        setPagina(0);

        setFiltrosAplicados({
            ...(vendaId.trim() && { vendaId: Number(vendaId.trim()) }),
            ...(inicio && { inicio }),
            ...(fim && { fim }),
        });
    }

    /* ============================================================
       LIMPAR FILTROS
       ============================================================ */

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

    /* ============================================================
       ABRIR MODAL DE CANCELAMENTO
       ============================================================ */

    function abrirCancelamento(venda) {
        setVendaParaCancelar(venda);
        setSucesso("");
    }

    function fecharCancelamento() {
        if (cancelando) return;
        setVendaParaCancelar(null);
    }

    /* ============================================================
       CONFIRMAR CANCELAMENTO
       ============================================================ */

    async function confirmarCancelamento(motivo) {

        if (!vendaParaCancelar) return;

        try {

            setCancelando(true);

            const vendaAtualizada = await cancelarVenda(
                vendaParaCancelar.id,
                motivo,
            );

            setVendas((atuais) =>
                atuais.map((v) =>
                    v.id === vendaAtualizada.id ? vendaAtualizada : v
                )
            );

            setSucesso(
                `Venda #${vendaAtualizada.id} cancelada. O stock foi devolvido.`
            );

            setVendaParaCancelar(null);

        } catch (error) {

            console.error("Erro ao cancelar venda:", error);
            alert(obterMensagemErro(error));

        } finally {
            setCancelando(false);
        }
    }

    /* ============================================================
       PAGINAÇÃO
       ============================================================ */

    function paginaAnterior() {
        if (pagina > 0) {
            setPagina((atual) => atual - 1);
        }
    }

    function proximaPagina() {
        if (pagina < totalPaginas - 1) {
            setPagina((atual) => atual + 1);
        }
    }

    /* ============================================================
       CARREGANDO
       ============================================================ */

    if (carregando) {
        return (
            <div className="page-container">
                <p>Carregando vendas...</p>
            </div>
        );
    }

    /* ============================================================
       ERRO
       ============================================================ */

    if (erro) {
        return (
            <div className="page-container">
                <p>{erro}</p>

                <button type="button" onClick={() => setErro(null)}>
                    Voltar
                </button>
            </div>
        );
    }

    /* ============================================================
       INTERFACE
       ============================================================ */

    return (

        <div className="page-container">

            {/* ================================================= */}
            {/* CABEÇALHO */}
            {/* ================================================= */}

            <div className="page-header">

                <div>
                    <h1>Vendas</h1>
                    <p>Histórico das vendas realizadas pela empresa.</p>
                </div>

                <Link to="/vendas/nova" className="button-primary">
                    + Nova venda
                </Link>

            </div>


            {/* ================================================= */}
            {/* MENSAGEM DE SUCESSO (fora do card) */}
            {/* ================================================= */}

            {sucesso && (
                <div className="venda-sucesso">
                    {sucesso}
                </div>
            )}


            {/* ================================================= */}
            {/* CONTEÚDO */}
            {/* ================================================= */}

            <section className="content-card">

                <div className="content-card-header">

                    <div>
                        <h2>Histórico de vendas</h2>
                        <p>Pesquise uma venda por número ou período.</p>
                    </div>

                </div>


                {/* ================================================= */}
                {/* FILTROS */}
                {/* ================================================= */}

                <div className="vendas-filtros">

                    <div className="filtro-group">
                        <label htmlFor="vendaId">Nº da venda</label>
                        <input
                            id="vendaId"
                            type="text"
                            inputMode="numeric"
                            placeholder="Ex.: 487"
                            value={vendaId}
                            onChange={(event) => setVendaId(event.target.value)}
                        />
                    </div>

                    <div className="filtro-group">
                        <label htmlFor="dataInicio">Data inicial</label>
                        <input
                            id="dataInicio"
                            type="date"
                            value={dataInicio}
                            onChange={(event) => setDataInicio(event.target.value)}
                        />
                    </div>

                    <div className="filtro-group">
                        <label htmlFor="horaInicio">Hora inicial</label>
                        <input
                            id="horaInicio"
                            type="time"
                            value={horaInicio}
                            onChange={(event) => setHoraInicio(event.target.value)}
                        />
                    </div>

                    <div className="filtro-group">
                        <label htmlFor="dataFim">Data final</label>
                        <input
                            id="dataFim"
                            type="date"
                            value={dataFim}
                            onChange={(event) => setDataFim(event.target.value)}
                        />
                    </div>

                    <div className="filtro-group">
                        <label htmlFor="horaFim">Hora final</label>
                        <input
                            id="horaFim"
                            type="time"
                            value={horaFim}
                            onChange={(event) => setHoraFim(event.target.value)}
                        />
                    </div>

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
                        {" "}encontrada
                        {totalVendas !== 1 ? "s" : ""}
                    </span>
                </div>


                {/* ================================================= */}
                {/* TABELA */}
                {/* ================================================= */}

                <VendaTabela
                    vendas={vendas}
                    onCancelar={abrirCancelamento}
                />


                {/* ================================================= */}
                {/* PAGINAÇÃO */}
                {/* ================================================= */}

                {totalPaginas > 1 && (
                    <div className="pagination">

                        <button
                            type="button"
                            onClick={paginaAnterior}
                            disabled={pagina === 0 || carregando}
                        >
                            ← Anterior
                        </button>

                        <span>
                            Página <strong>{pagina + 1}</strong> de{" "}
                            <strong>{totalPaginas}</strong>
                        </span>

                        <button
                            type="button"
                            onClick={proximaPagina}
                            disabled={pagina >= totalPaginas - 1 || carregando}
                        >
                            Próxima →
                        </button>

                    </div>
                )}

            </section>


            {/* ================================================= */}
            {/* MODAL DE CANCELAMENTO */}
            {/* ================================================= */}

            <CancelarVendaModal
                venda={vendaParaCancelar}
                onConfirmar={confirmarCancelamento}
                onFechar={fecharCancelamento}
                carregando={cancelando}
            />

        </div>
    );
}

export default Vendas;