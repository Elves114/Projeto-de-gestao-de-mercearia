import { useEffect, useState } from "react";

import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
} from "recharts";

import "../style/Dashboard.css";
import { buscarDashboard } from "../service/DashboardService";
import { useAuth } from "../../../contexts/useAuth";


/* ============================================================
   ICONES DAS ATIVIDADES
   ============================================================ */

const ICONES_ATIVIDADE = {
    CRIACAO: "+",
    ALTERACAO: "✎",
    EXCLUSAO: "×",
    LOGIN: "→",
    LOGOUT: "←",
    VENDA: "▣",
};

const ROTULOS_ATIVIDADE = {
    CRIACAO: "Criação",
    ALTERACAO: "Alteração",
    EXCLUSAO: "Exclusão",
    LOGIN: "Login",
    LOGOUT: "Logout",
    VENDA: "Venda",
};


/* ============================================================
   DASHBOARD
   ============================================================ */

function Dashboard() {

    const { usuario } = useAuth();

    const [dashboard, setDashboard] = useState(null);
    const [periodo, setPeriodo] = useState(7);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    /*
     * "Agora" é uma leitura do relógio — impura.
     * Guardamos num estado e actualizamos a cada 30s
     * para os tempos relativos não ficarem presos.
     */
    const [agora, setAgora] = useState(() => Date.now());

    useEffect(() => {

        const intervalo = setInterval(() => {
            setAgora(Date.now());
        }, 30_000);

        return () => clearInterval(intervalo);

    }, []);

    /* ========================================================
       CARREGAMENTO
       ======================================================== */

    useEffect(() => {

        let ativo = true;

        async function carregar() {

            try {

                setCarregando(true);
                setErro("");

                const dados = await buscarDashboard(periodo);

                if (ativo) {
                    setDashboard(dados);
                }

            } catch (error) {

                console.error(
                    "Erro ao carregar dashboard:",
                    error
                );

                if (ativo) {
                    setErro(
                        "Não foi possível carregar os dados do dashboard."
                    );
                }

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

    }, [periodo]);


    /* ========================================================
       FORMATAÇÃO
       ======================================================== */

    const formatarMoeda = (valor) => {

        return new Intl.NumberFormat("pt-MZ", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        }).format(valor || 0) + " MT";
    };


    const formatarMoedaCompacta = (valor) => {

        const numero = Number(valor) || 0;

        if (numero >= 1_000_000) {
            return (
                (numero / 1_000_000).toFixed(1) + "M MT"
            );
        }

        if (numero >= 1_000) {
            return (
                (numero / 1_000).toFixed(0) + "k MT"
            );
        }

        return formatarMoeda(numero);
    };


    const formatarDataGrafico = (data, formato = "curto") => {

        const d = new Date(`${data}T00:00:00`);

        if (formato === "completo") {

            return d.toLocaleDateString("pt-MZ", {
                day: "2-digit",
                month: "long",
                year: "numeric",
            });
        }

        return d.toLocaleDateString("pt-MZ", {
            day: "2-digit",
            month: "short",
        });
    };


    const formatarData = (agoraMs) => {
        return new Intl.DateTimeFormat("pt-MZ", {
            day: "2-digit",
            month: "long",
            year: "numeric",
        }).format(new Date(agoraMs));
    };

    const tempoRelativo = (dataISO, agoraMs) => {

        if (!dataISO) {
            return "—";
        }

        const data = new Date(dataISO).getTime();
        const diff = Math.floor((agoraMs - data) / 1000);

        if (diff < 60) {
            return "agora mesmo";
        }

        if (diff < 3600) {
            return `há ${Math.floor(diff / 60)} min`;
        }

        if (diff < 86400) {
            return `há ${Math.floor(diff / 3600)} h`;
        }

        if (diff < 604800) {
            return `há ${Math.floor(diff / 86400)} d`;
        }

        return new Date(dataISO).toLocaleDateString("pt-MZ", {
            day: "2-digit",
            month: "short",
        });
    };


    /* ========================================================
       ESTADOS
       ======================================================== */

    if (carregando && !dashboard) {

        return (
            <div className="dashboard">
                <div className="dashboard-skeleton-hero" />
                <div className="dashboard-skeleton-row">
                    <div className="dashboard-skeleton-card" />
                    <div className="dashboard-skeleton-card" />
                    <div className="dashboard-skeleton-card" />
                    <div className="dashboard-skeleton-card" />
                </div>
                <div className="dashboard-skeleton-block" />
            </div>
        );
    }


    if (erro) {

        return (
            <div className="dashboard">
                <div className="dashboard-error">
                    <strong>
                        Não foi possível carregar
                    </strong>
                    <span>{erro}</span>
                </div>
            </div>
        );
    }


    /* ========================================================
       DADOS
       ======================================================== */

    const produtosAtivos = dashboard?.produtosAtivos ?? 0;
    const produtosInativos = dashboard?.produtosInativos ?? 0;
    const quantidadeTotalStock = dashboard?.quantidadeTotalStock ?? 0;
    const stockBaixo = dashboard?.stockBaixo ?? 0;
    const semStock = dashboard?.semStock ?? 0;
    const alertasAtivos = dashboard?.alertasAtivos ?? 0;
    const vendasHoje = dashboard?.vendasHoje ?? 0;
    const valorVendasHoje = dashboard?.valorVendasHoje ?? 0;
    const vendasMes = dashboard?.vendasMes ?? 0;
    const valorVendasMes = dashboard?.valorVendasMes ?? 0;
    const lucroMes = dashboard?.lucroMes ?? 0;
    const ticketMedioMes = dashboard?.ticketMedioMes ?? 0;

    const vendasGrafico = dashboard?.vendasGrafico ?? [];
    const ultimasVendas = dashboard?.ultimasVendas ?? [];
    const stockCritico = dashboard?.stockCritico ?? [];
    const produtosMaisVendidos = dashboard?.produtosMaisVendidos ?? [];
    const atividadeRecente = dashboard?.atividadeRecente ?? [];

    const stockNormal = Math.max(
        produtosAtivos - stockBaixo - semStock,
        0
    );

    const maxVendido = Math.max(
        ...produtosMaisVendidos.map((p) => p.quantidadeVendida),
        1
    );

    const primeiroNome =
        usuario?.nome?.split(" ")[0] || "utilizador";


    /* ========================================================
       RENDER
       ======================================================== */

    return (
        <div className="dashboard">

            {/* =================================================
                CABEÇALHO
               ================================================= */}

            <header className="dashboard-header">

                <div>

                    <span className="dashboard-eyebrow">
                        Painel de gestão
                    </span>

                    <h1>
                        Olá, {primeiroNome} <span className="dashboard-emoji">👋</span>
                    </h1>

                    <p className="dashboard-header-date">
                        {formatarData(agora)}
                    </p>

                </div>


                <div className="dashboard-periodo">

                    <label htmlFor="dashboard-periodo">
                        Período
                    </label>

                    <select
                        id="dashboard-periodo"
                        value={periodo}
                        onChange={(event) =>
                            setPeriodo(
                                Number(event.target.value)
                            )
                        }
                    >
                        <option value={7}>
                            Últimos 7 dias
                        </option>
                        <option value={30}>
                            Últimos 30 dias
                        </option>
                        <option value={365}>
                            Este ano
                        </option>
                    </select>

                </div>

            </header>


            {/* =================================================
                LINHA 1 — HERO + KPIs
               ================================================= */}

            <section className="dashboard-hero-row">

                {/* HERO — VENDAS DE HOJE */}

                <article className="dashboard-hero">

                    <div className="dashboard-hero-top">

                        <span className="dashboard-hero-label">
                            Vendas hoje
                        </span>

                        <span className="dashboard-hero-badge">
                            {vendasHoje}{" "}
                            {vendasHoje === 1
                                ? "venda"
                                : "vendas"}
                        </span>

                    </div>


                    <strong className="dashboard-hero-value">
                        {formatarMoeda(valorVendasHoje)}
                    </strong>


                    <div className="dashboard-hero-sparkline">
                        <Sparkline dados={vendasGrafico} />
                    </div>


                    <div className="dashboard-hero-footer">

                        <div className="dashboard-hero-metric">
                            <span>Receita mês</span>
                            <strong>
                                {formatarMoedaCompacta(
                                    valorVendasMes
                                )}
                            </strong>
                        </div>

                        <div className="dashboard-hero-metric">
                            <span>Lucro mês</span>
                            <strong>
                                {formatarMoedaCompacta(lucroMes)}
                            </strong>
                        </div>

                        <div className="dashboard-hero-metric">
                            <span>Ticket médio</span>
                            <strong>
                                {formatarMoedaCompacta(
                                    ticketMedioMes
                                )}
                            </strong>
                        </div>

                    </div>

                </article>


                {/* KPI 1 — RECEITA MÊS */}

                <article className="dashboard-kpi">

                    <span className="dashboard-kpi-label">
                        Receita mês
                    </span>

                    <strong className="dashboard-kpi-value">
                        {formatarMoeda(valorVendasMes)}
                    </strong>

                    <span className="dashboard-kpi-footnote">
                        {vendasMes}{" "}
                        {vendasMes === 1 ? "venda" : "vendas"}
                    </span>

                </article>


                {/* KPI 2 — LUCRO MÊS */}

                <article className="dashboard-kpi dashboard-kpi-success">

                    <span className="dashboard-kpi-label">
                        Lucro mês
                    </span>

                    <strong className="dashboard-kpi-value">
                        {formatarMoeda(lucroMes)}
                    </strong>

                    <span className="dashboard-kpi-footnote">
                        Margem da operação
                    </span>

                </article>


                {/* KPI 3 — STOCK BAIXO */}

                <article
                    className={
                        "dashboard-kpi " +
                        (stockBaixo > 0
                            ? "dashboard-kpi-warning"
                            : "")
                    }
                >

                    <span className="dashboard-kpi-label">
                        Stock baixo
                    </span>

                    <strong className="dashboard-kpi-value">
                        {stockBaixo}
                    </strong>

                    <span className="dashboard-kpi-footnote">
                        {stockBaixo === 0
                            ? "Tudo em ordem"
                            : "Precisam de atenção"}
                    </span>

                </article>


                {/* KPI 4 — ALERTAS */}

                <article
                    className={
                        "dashboard-kpi " +
                        (alertasAtivos > 0
                            ? "dashboard-kpi-danger"
                            : "")
                    }
                >

                    <span className="dashboard-kpi-label">
                        Alertas activos
                    </span>

                    <strong className="dashboard-kpi-value">
                        {alertasAtivos}
                    </strong>

                    <span className="dashboard-kpi-footnote">
                        {semStock} sem stock ·{" "}
                        {quantidadeTotalStock} un.
                    </span>

                </article>

            </section>


            {/* =================================================
                LINHA 2 — GRÁFICO + TOP PRODUTOS
               ================================================= */}

            <section className="dashboard-chart-row">

                {/* GRÁFICO */}

                <article className="dashboard-panel dashboard-chart-panel">

                    <header className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                Evolução
                            </span>

                            <h2>
                                Desempenho das vendas
                            </h2>

                        </div>

                        <span className="dashboard-panel-meta">
                            {periodo === 7
                                ? "7 dias"
                                : periodo === 30
                                    ? "30 dias"
                                    : "Este ano"}
                        </span>

                    </header>


                    <div className="dashboard-chart">

                        <ResponsiveContainer width="100%" height={280}>

                            <LineChart
                                data={vendasGrafico}
                                margin={{
                                    top: 12,
                                    right: 8,
                                    left: 0,
                                    bottom: 0,
                                }}
                            >

                                <CartesianGrid
                                    strokeDasharray="3 3"
                                    vertical={false}
                                    stroke="#e2e8f0"
                                />

                                <XAxis
                                    dataKey="data"
                                    tickFormatter={(data) =>
                                        formatarDataGrafico(data)
                                    }
                                    tick={{
                                        fontSize: 11,
                                        fill: "#94a3b8",
                                    }}
                                    axisLine={false}
                                    tickLine={false}
                                />

                                <YAxis
                                    tickFormatter={(valor) =>
                                        valor >= 1000
                                            ? `${valor / 1000}k`
                                            : valor
                                    }
                                    tick={{
                                        fontSize: 11,
                                        fill: "#94a3b8",
                                    }}
                                    axisLine={false}
                                    tickLine={false}
                                    width={48}
                                />

                                <Tooltip
                                    labelFormatter={(data) =>
                                        formatarDataGrafico(
                                            data,
                                            "completo"
                                        )
                                    }
                                    formatter={(valor) => [
                                        formatarMoeda(valor),
                                        "Vendas",
                                    ]}
                                    contentStyle={{
                                        borderRadius: 12,
                                        border:
                                            "1px solid #e2e8f0",
                                        fontSize: 12,
                                    }}
                                />

                                <Line
                                    type="monotone"
                                    dataKey="valor"
                                    stroke="#00aeef"
                                    strokeWidth={3}
                                    dot={{
                                        r: 3.5,
                                        fill: "#00aeef",
                                        strokeWidth: 0,
                                    }}
                                    activeDot={{
                                        r: 6,
                                        fill: "#00aeef",
                                        stroke: "#ffffff",
                                        strokeWidth: 2,
                                    }}
                                />

                            </LineChart>

                        </ResponsiveContainer>

                    </div>

                </article>


                {/* TOP PRODUTOS */}

                <article className="dashboard-panel dashboard-top-panel">

                    <header className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                Ranking
                            </span>

                            <h2>
                                Produtos mais vendidos
                            </h2>

                        </div>

                    </header>


                    {produtosMaisVendidos.length === 0 ? (

                        <div className="dashboard-empty">

                            <span>
                                Ainda não há vendas este mês.
                            </span>

                        </div>

                    ) : (

                        <ul className="dashboard-top-list">

                            {produtosMaisVendidos.map(
                                (produto, index) => (

                                    <li
                                        key={produto.produtoId}
                                        className="dashboard-top-item"
                                    >

                                        <span className="dashboard-top-rank">
                                            {index + 1}
                                        </span>


                                        <div className="dashboard-top-info">

                                            <strong>
                                                {produto.produtoNome}
                                            </strong>

                                            <div className="dashboard-top-bar">

                                                <div
                                                    className="dashboard-top-bar-fill"
                                                    style={{
                                                        width: `${(produto.quantidadeVendida /
                                                            maxVendido) *
                                                            100
                                                            }%`,
                                                    }}
                                                />

                                            </div>

                                        </div>


                                        <span className="dashboard-top-value">
                                            {produto.quantidadeVendida}
                                        </span>

                                    </li>
                                )
                            )}

                        </ul>

                    )}

                </article>

            </section>


            {/* =================================================
                LINHA 3 — ÚLTIMAS VENDAS + STOCK CRÍTICO
               ================================================= */}

            <section className="dashboard-lists-row">

                {/* ÚLTIMAS VENDAS */}

                <article className="dashboard-panel">

                    <header className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                Recentes
                            </span>

                            <h2>
                                Últimas vendas
                            </h2>

                        </div>

                    </header>


                    {ultimasVendas.length === 0 ? (

                        <div className="dashboard-empty">
                            <span>
                                Ainda não há vendas registadas.
                            </span>
                        </div>

                    ) : (

                        <ul className="dashboard-sales-list">

                            {ultimasVendas.map((venda) => (

                                <li
                                    key={venda.id}
                                    className="dashboard-sale-item"
                                >

                                    <div className="dashboard-sale-id">
                                        #{venda.id}
                                    </div>


                                    <div className="dashboard-sale-info">

                                        <strong>
                                            {venda.usuarioNome}
                                        </strong>

                                        <span>
                                            {tempoRelativo(venda.dataVenda, agora)}
                                        </span>

                                    </div>


                                    <span className="dashboard-sale-total">
                                        {formatarMoeda(venda.total)}
                                    </span>

                                </li>
                            ))}

                        </ul>

                    )}

                </article>


                {/* STOCK CRÍTICO */}

                <article className="dashboard-panel">

                    <header className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                Atenção
                            </span>

                            <h2>
                                Stock crítico
                            </h2>

                        </div>

                    </header>


                    {stockCritico.length === 0 ? (

                        <div className="dashboard-empty">
                            <span>
                                Todos os produtos com stock saudável.
                            </span>
                        </div>

                    ) : (

                        <ul className="dashboard-stock-list">

                            {stockCritico.map((item) => {

                                const semStockAtual =
                                    item.quantidade === 0;

                                return (

                                    <li
                                        key={item.produtoId}
                                        className={
                                            "dashboard-stock-item " +
                                            (semStockAtual
                                                ? "dashboard-stock-item-danger"
                                                : "")
                                        }
                                    >

                                        <div className="dashboard-stock-info">

                                            <strong>
                                                {item.produtoNome}
                                            </strong>

                                            <span>
                                                Mínimo:{" "}
                                                {item.quantidadeMinima}
                                            </span>

                                        </div>


                                        <div className="dashboard-stock-quantity">

                                            <strong>
                                                {item.quantidade}
                                            </strong>

                                            {semStockAtual && (
                                                <span className="dashboard-stock-badge">
                                                    Sem stock
                                                </span>
                                            )}

                                        </div>

                                    </li>
                                );
                            })}

                        </ul>

                    )}

                </article>

            </section>


            {/* =================================================
                LINHA 4 — ATIVIDADE RECENTE
               ================================================= */}

            <section className="dashboard-panel">

                <header className="dashboard-panel-header">

                    <div>

                        <span className="dashboard-panel-label">
                            Actividade
                        </span>

                        <h2>
                            O que aconteceu recentemente
                        </h2>

                    </div>

                </header>


                {atividadeRecente.length === 0 ? (

                    <div className="dashboard-empty">
                        <span>
                            Sem actividade registada.
                        </span>
                    </div>

                ) : (

                    <ul className="dashboard-activity-list">

                        {atividadeRecente.map((evento, index) => (

                            <li
                                key={index}
                                className="dashboard-activity-item"
                                data-gravidade={evento.gravidade}
                            >

                                <div className="dashboard-activity-icon">
                                    {ICONES_ATIVIDADE[evento.tipo] || "•"}
                                </div>


                                <div className="dashboard-activity-info">

                                    <strong>
                                        {evento.descricao}
                                    </strong>

                                    <span>
                                        {ROTULOS_ATIVIDADE[evento.tipo] ||
                                            evento.tipo}
                                        {" · "}
                                        {evento.usuarioNome}
                                    </span>

                                </div>


                                <span className="dashboard-activity-time">
                                    {tempoRelativo(evento.data, agora)}
                                </span>

                            </li>
                        ))}

                    </ul>

                )}

            </section>

        </div>
    );
}


/* ============================================================
   SPARKLINE
   ============================================================ */

function Sparkline({ dados, altura = 56 }) {

    if (!dados || dados.length < 2) {
        return null;
    }

    const largura = 400;
    const padding = 4;

    const valores = dados.map((d) => Number(d.valor) || 0);

    const max = Math.max(...valores);
    const min = Math.min(...valores, 0);
    const range = max - min || 1;

    const pontos = valores.map((valor, index) => {

        const x =
            (index / (valores.length - 1)) * largura;

        const y =
            altura -
            padding -
            ((valor - min) / range) *
            (altura - padding * 2);

        return { x, y };
    });

    let path = `M ${pontos[0].x.toFixed(2)} ${pontos[0].y.toFixed(2)}`;

    for (let i = 1; i < pontos.length; i++) {

        const anterior = pontos[i - 1];
        const atual = pontos[i];
        const cx = (anterior.x + atual.x) / 2;

        path += ` Q ${cx.toFixed(2)} ${anterior.y.toFixed(2)} ${atual.x.toFixed(2)} ${atual.y.toFixed(2)}`;
    }

    return (
        <svg
            viewBox={`0 0 ${largura} ${altura}`}
            preserveAspectRatio="none"
            className="dashboard-sparkline-svg"
            aria-hidden="true"
        >
            <path
                d={path}
                fill="none"
                stroke="#00aeef"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
            />
        </svg>
    );
}


export default Dashboard;