
import { useEffect, useState } from "react";
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip
} from "recharts";

import "../style/Dashboard.css";
import { buscarDashboard } from "../service/DashboardService";


function Dashboard() {

    const [dashboard, setDashboard] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    useEffect(() => {

        const carregarDashboard = async () => {

            try {

                setCarregando(true);
                setErro("");

                const dados =
                    await buscarDashboard();

                setDashboard(dados);


            } catch (error) {

                console.error(
                    "Erro ao carregar dashboard:",
                    error
                );

                setErro(
                    "Não foi possível carregar os dados do dashboard."
                );

            } finally {

                setCarregando(false);
            }
        };

        carregarDashboard();

    }, []);


    /*
     * ============================================================
     * FORMATAÇÃO
     * ============================================================
     */

    const formatarMoeda = (valor) => {

        return new Intl.NumberFormat(
            "pt-MZ",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        ).format(valor || 0) + " MT";
    };



    const formatarDataGrafico = (data, formato = "curto") => {

        const dataFormatada =
            new Date(`${data}T00:00:00`);

        if (formato === "completo") {

            return dataFormatada.toLocaleDateString(
                "pt-MZ",
                {
                    day: "2-digit",
                    month: "long",
                    year: "numeric"
                }
            );
        }

        return dataFormatada.toLocaleDateString(
            "pt-MZ",
            {
                day: "2-digit",
                month: "short"
            }
        );
    };


    const formatarData = () => {

        return new Intl.DateTimeFormat(
            "pt-MZ",
            {
                day: "2-digit",
                month: "short",
                year: "numeric"
            }
        ).format(new Date());
    };


    /*
     * ============================================================
     * ESTADOS
     * ============================================================
     */

    if (carregando) {

        return (
            <div className="dashboard">

                <section className="dashboard-welcome">

                    <div>
                        <span className="dashboard-eyebrow">
                            PAINEL DE GESTÃO
                        </span>

                        <h1>
                            Carregando dashboard...
                        </h1>

                        <p>
                            Estamos a buscar os dados da sua empresa.
                        </p>
                    </div>

                </section>

            </div>
        );
    }


    if (erro) {

        return (
            <div className="dashboard">

                <section className="dashboard-welcome">

                    <div>
                        <span className="dashboard-eyebrow">
                            PAINEL DE GESTÃO
                        </span>

                        <h1>
                            Não foi possível carregar
                        </h1>

                        <p>
                            {erro}
                        </p>
                    </div>

                </section>

            </div>
        );
    }


    /*
     * ============================================================
     * DADOS
     * ============================================================
     */

    const produtosAtivos =
        dashboard?.produtosAtivos ?? 0;

    const produtosInativos =
        dashboard?.produtosInativos ?? 0;

    const quantidadeTotalStock =
        dashboard?.quantidadeTotalStock ?? 0;

    const stockBaixo =
        dashboard?.stockBaixo ?? 0;

    const semStock =
        dashboard?.semStock ?? 0;

    const alertasAtivos =
        dashboard?.alertasAtivos ?? 0;

    const vendasHoje =
        dashboard?.vendasHoje ?? 0;

    const valorVendasHoje =
        dashboard?.valorVendasHoje ?? 0;

    const vendasMes =
        dashboard?.vendasMes ?? 0;

    const valorVendasMes =
        dashboard?.valorVendasMes ?? 0;

    const lucroMes =
        dashboard?.lucroMes ?? 0;
    const vendasGrafico =
        dashboard?.vendasGrafico ?? [];

    /*
     * Produtos em situação normal.
     *
     * Como o backend já devolve:
     * - produtos ativos
     * - stock baixo
     * - sem stock
     *
     * podemos calcular os restantes.
     */

    const stockNormal =
        Math.max(
            produtosAtivos - stockBaixo - semStock,
            0
        );


    return (
        <div className="dashboard">

            {/* =========================
                CABEÇALHO
            ========================== */}

            <section className="dashboard-welcome">

                <div>

                    <span className="dashboard-eyebrow">
                        PAINEL DE GESTÃO
                    </span>

                    <h1>
                        Olá, Elves 👋
                    </h1>

                    <p>
                        Aqui está o resumo da sua empresa.
                    </p>

                </div>

                <div className="dashboard-date">

                    <span>
                        Hoje
                    </span>

                    <strong>
                        {formatarData()}
                    </strong>

                </div>

            </section>


            {/* =========================
                INDICADORES PRINCIPAIS
            ========================== */}

            <section className="dashboard-kpis">

                {/* VENDAS HOJE */}

                <div className="dashboard-kpi">

                    <div className="dashboard-kpi-top">

                        <span>
                            Vendas hoje
                        </span>

                        <span className="dashboard-kpi-icon">
                            💰
                        </span>

                    </div>

                    <strong className="dashboard-kpi-value">
                        {formatarMoeda(valorVendasHoje)}
                    </strong>

                    <span className="dashboard-kpi-description">
                        {vendasHoje} venda{vendasHoje !== 1 ? "s" : ""} realizada{vendasHoje !== 1 ? "s" : ""}
                    </span>

                </div>


                {/* PRODUTOS */}

                <div className="dashboard-kpi">

                    <div className="dashboard-kpi-top">

                        <span>
                            Produtos ativos
                        </span>

                        <span className="dashboard-kpi-icon">
                            📦
                        </span>

                    </div>

                    <strong className="dashboard-kpi-value">
                        {produtosAtivos}
                    </strong>

                    <span className="dashboard-kpi-description">
                        {produtosInativos} produto{produtosInativos !== 1 ? "s" : ""} inativo{produtosInativos !== 1 ? "s" : ""}
                    </span>

                </div>


                {/* STOCK BAIXO */}

                <div className="dashboard-kpi">

                    <div className="dashboard-kpi-top">

                        <span>
                            Stock baixo
                        </span>

                        <span className="dashboard-kpi-icon warning">
                            ⚠
                        </span>

                    </div>

                    <strong className="dashboard-kpi-value">
                        {stockBaixo}
                    </strong>

                    <span className="dashboard-kpi-change warning">
                        Precisam de atenção
                    </span>

                </div>


                {/* SEM STOCK */}

                <div className="dashboard-kpi">

                    <div className="dashboard-kpi-top">

                        <span>
                            Sem stock
                        </span>

                        <span className="dashboard-kpi-icon danger">
                            !
                        </span>

                    </div>

                    <strong className="dashboard-kpi-value">
                        {semStock}
                    </strong>

                    <span className="dashboard-kpi-change danger">
                        Produto{semStock !== 1 ? "s" : ""} esgotado{semStock !== 1 ? "s" : ""}
                    </span>

                </div>

            </section>


            {/* =========================
                VENDAS + STOCK
            ========================== */}

            <section className="dashboard-main-grid">

                {/* VENDAS */}

                <div className="dashboard-panel dashboard-sales">

                    <div className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                VENDAS
                            </span>

                            <h2>
                                Desempenho das vendas
                            </h2>

                        </div>

                        <select defaultValue="30">

                            <option value="7">
                                Últimos 7 dias
                            </option>

                            <option value="30">
                                Últimos 30 dias
                            </option>

                            <option value="365">
                                Este ano
                            </option>

                        </select>

                    </div>

                    <div className="dashboard-sales-summary">

                        <span>
                            Vendas deste mês
                        </span>

                        <strong>
                            {formatarMoeda(valorVendasMes)}
                        </strong>

                        <small>
                            {vendasMes} venda{vendasMes !== 1 ? "s" : ""} realizadas
                        </small>

                    </div>


                    <div className="dashboard-chart">

                        <ResponsiveContainer width="100%" height={280}>

                            <LineChart
                                data={vendasGrafico}
                                margin={{
                                    top: 15,
                                    right: 10,
                                    left: 10,
                                    bottom: 5
                                }}
                            >

                                <CartesianGrid
                                    strokeDasharray="3 3"
                                    vertical={false}
                                />

                                <XAxis
                                    dataKey="data"
                                    tickFormatter={(data) =>
                                        formatarDataGrafico(data)
                                    }
                                    tick={{
                                        fontSize: 11
                                    }}
                                    axisLine={false}
                                    tickLine={false}
                                />

                                <YAxis
                                    tickFormatter={(valor) =>
                                        `${valor / 1000}k`
                                    }
                                    tick={{
                                        fontSize: 11
                                    }}
                                    axisLine={false}
                                    tickLine={false}
                                    width={45}
                                />

                                <Tooltip
                                    labelFormatter={(data) =>
                                        formatarDataGrafico(data, "completo")
                                    }
                                    formatter={(valor) => [
                                        formatarMoeda(valor),
                                        "Vendas"
                                    ]}
                                />

                                <Line
                                    type="monotone"
                                    dataKey="valor"
                                    stroke="#00AEEF"
                                    strokeWidth={3}
                                    dot={{
                                        r: 4,
                                        fill: "#00AEEF"
                                    }}
                                    activeDot={{
                                        r: 6,
                                        fill: "#00AEEF"
                                    }}
                                />

                            </LineChart>

                        </ResponsiveContainer>

                    </div>



                    <div className="dashboard-sales-footer">

                        <div>

                            <span>
                                Lucro do mês
                            </span>

                            <strong>
                                {formatarMoeda(lucroMes)}
                            </strong>

                        </div>

                    </div>

                </div>


                {/* STOCK */}

                <div className="dashboard-panel dashboard-stock">

                    <div className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                ESTOQUE
                            </span>

                            <h2>
                                Situação do stock
                            </h2>

                        </div>

                    </div>


                    <div className="stock-status">

                        <div className="stock-status-item">

                            <span className="stock-status-dot normal"></span>

                            <div>

                                <strong>
                                    Normal
                                </strong>

                                <span>
                                    {stockNormal} produtos
                                </span>

                            </div>

                        </div>


                        <div className="stock-status-item">

                            <span className="stock-status-dot warning"></span>

                            <div>

                                <strong>
                                    Stock baixo
                                </strong>

                                <span>
                                    {stockBaixo} produtos
                                </span>

                            </div>

                        </div>


                        <div className="stock-status-item">

                            <span className="stock-status-dot danger"></span>

                            <div>

                                <strong>
                                    Sem stock
                                </strong>

                                <span>
                                    {semStock} produtos
                                </span>

                            </div>

                        </div>

                    </div>


                    <div className="dashboard-stock-total">

                        <span>
                            Quantidade total em stock
                        </span>

                        <strong>
                            {quantidadeTotalStock}
                        </strong>

                    </div>

                </div>

            </section>


            {/* =========================
                PRODUTOS + ATIVIDADE
            ========================== */}

            <section className="dashboard-secondary-grid">

                {/* PRODUTOS MAIS VENDIDOS */}

                <div className="dashboard-panel">

                    <div className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                PRODUTOS
                            </span>

                            <h2>
                                Mais vendidos
                            </h2>

                        </div>

                    </div>


                    <div className="dashboard-empty-state">

                        <strong>
                            Dados ainda não disponíveis
                        </strong>

                        <span>
                            O endpoint atual do dashboard ainda
                            não fornece os produtos mais vendidos.
                        </span>

                    </div>

                </div>


                {/* ATIVIDADE */}

                <div className="dashboard-panel">

                    <div className="dashboard-panel-header">

                        <div>

                            <span className="dashboard-panel-label">
                                AUDITORIA
                            </span>

                            <h2>
                                Atividade recente
                            </h2>

                        </div>

                    </div>


                    <div className="dashboard-empty-state">

                        <strong>
                            Atividade da empresa
                        </strong>

                        <span>
                            Os dados de auditoria serão ligados
                            ao Dashboard posteriormente.
                        </span>

                    </div>

                </div>

            </section>


            {/* =========================
                ALERTAS
            ========================== */}

            <section className="dashboard-panel dashboard-alerts">

                <div className="dashboard-panel-header">

                    <div>

                        <span className="dashboard-panel-label">
                            ATENÇÃO
                        </span>

                        <h2>
                            Alertas de stock
                        </h2>

                    </div>

                </div>


                <div className="dashboard-alert-list">

                    {alertasAtivos > 0 ? (

                        <div className="dashboard-alert warning">

                            <div className="dashboard-alert-icon">
                                !
                            </div>

                            <div className="dashboard-alert-info">

                                <strong>
                                    {alertasAtivos} alerta{alertasAtivos !== 1 ? "s" : ""} ativo{alertasAtivos !== 1 ? "s" : ""}
                                </strong>

                                <span>
                                    Existem produtos que precisam
                                    de atenção no stock.
                                </span>

                            </div>

                        </div>

                    ) : (

                        <div className="dashboard-alert normal">

                            <div className="dashboard-alert-info">

                                <strong>
                                    Stock em situação normal
                                </strong>

                                <span>
                                    Não existem alertas de stock ativos.
                                </span>

                            </div>

                        </div>

                    )}

                </div>

            </section>

        </div>
    );
}

export default Dashboard;

