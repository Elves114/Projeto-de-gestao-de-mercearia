import { useEffect, useState } from "react";
import { buscarFechoCaixa } from "../services/caixaService";
import { obterMensagemErro } from "../../../services/api";
import "../style/Caixa.css";

function Caixa() {

    const hoje = new Date().toISOString().split("T")[0];

    const [data, setData] = useState(hoje);
    const [fecho, setFecho] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    /* ============================================================
       CARREGAR FECHO
       ============================================================ */

    useEffect(() => {

        let ativo = true;

        async function carregar() {

            try {

                setCarregando(true);
                setErro("");

                const dados = await buscarFechoCaixa(data);

                if (ativo) {
                    setFecho(dados);
                }

            } catch (error) {

                console.error(error);

                if (ativo) {
                    setErro(obterMensagemErro(error));
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

    }, [data]);

    /* ============================================================
       FORMATAÇÃO
       ============================================================ */

    function formatarMoeda(valor) {
        return (
            Number(valor || 0).toLocaleString("pt-MZ", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
            }) + " MT"
        );
    }

    function formatarData(d) {
        if (!d) return "—";
        const [ano, mes, dia] = d.split("-");
        return `${dia}/${mes}/${ano}`;
    }

    /* ============================================================
       RENDER
       ============================================================ */

    return (
        <div className="caixa-page">

            <header className="caixa-header">
                <div>
                    <span className="caixa-overline">
                        FECHO DE CAIXA
                    </span>
                    <h1>Resumo do dia</h1>
                    <p>
                        Consulta os totais de vendas e o desempenho
                        da equipa num dia específico.
                    </p>
                </div>

                <div className="caixa-filtro-data">
                    <label htmlFor="data-fecho">Data</label>
                    <input
                        id="data-fecho"
                        type="date"
                        value={data}
                        max={hoje}
                        onChange={(e) => setData(e.target.value)}
                    />
                </div>
            </header>


            {erro && (
                <div className="caixa-erro">{erro}</div>
            )}


            {carregando && !fecho ? (
                <div className="caixa-loading">A carregar fecho...</div>
            ) : fecho ? (
                <>
                    {/* =================================================
                        KPIs PRINCIPAIS
                       ================================================= */}

                    <section className="caixa-kpis">

                        <article className="caixa-kpi caixa-kpi-primario">
                            <span className="caixa-kpi-label">
                                Total vendido
                            </span>
                            <strong className="caixa-kpi-valor">
                                {formatarMoeda(fecho.totalVendas)}
                            </strong>
                            <span className="caixa-kpi-footnote">
                                {fecho.numeroVendas}{" "}
                                {fecho.numeroVendas === 1
                                    ? "venda"
                                    : "vendas"}
                            </span>
                        </article>


                        <article className="caixa-kpi caixa-kpi-sucesso">
                            <span className="caixa-kpi-label">
                                Lucro do dia
                            </span>
                            <strong className="caixa-kpi-valor">
                                {formatarMoeda(fecho.lucroTotal)}
                            </strong>
                            <span className="caixa-kpi-footnote">
                                Margem de lucro
                            </span>
                        </article>


                        <article className="caixa-kpi">
                            <span className="caixa-kpi-label">
                                Ticket médio
                            </span>
                            <strong className="caixa-kpi-valor">
                                {formatarMoeda(fecho.ticketMedio)}
                            </strong>
                            <span className="caixa-kpi-footnote">
                                Por venda
                            </span>
                        </article>


                        <article
                            className={
                                fecho.vendasCanceladas > 0
                                    ? "caixa-kpi caixa-kpi-perigo"
                                    : "caixa-kpi"
                            }
                        >
                            <span className="caixa-kpi-label">
                                Cancelamentos
                            </span>
                            <strong className="caixa-kpi-valor">
                                {fecho.vendasCanceladas}
                            </strong>
                            <span className="caixa-kpi-footnote">
                                {fecho.vendasCanceladas > 0
                                    ? formatarMoeda(fecho.totalCancelado)
                                    : "Sem cancelamentos"}
                            </span>
                        </article>

                    </section>


                    {/* =================================================
                        VENDAS POR FUNCIONÁRIO
                       ================================================= */}

                    <section className="caixa-painel">

                        <header className="caixa-painel-header">
                            <div>
                                <h2>Vendas por funcionário</h2>
                                <p>
                                    Desempenho individual no dia{" "}
                                    {formatarData(fecho.data)}.
                                </p>
                            </div>
                        </header>


                        {fecho.vendasPorUtilizador.length === 0 ? (
                            <div className="caixa-vazio">
                                Nenhuma venda registada neste dia.
                            </div>
                        ) : (
                            <div className="caixa-tabela-wrapper">
                                <table className="caixa-tabela">
                                    <thead>
                                        <tr>
                                            <th>Funcionário</th>
                                            <th className="caixa-col-num">
                                                Nº Vendas
                                            </th>
                                            <th className="caixa-col-num">
                                                Total Vendido
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {fecho.vendasPorUtilizador.map(
                                            (u) => (
                                                <tr key={u.usuarioId}>
                                                    <td>
                                                        <strong>
                                                            {u.usuarioNome}
                                                        </strong>
                                                    </td>
                                                    <td className="caixa-col-num">
                                                        {u.numeroVendas}
                                                    </td>
                                                    <td className="caixa-col-num caixa-valor">
                                                        {formatarMoeda(u.totalVendas)}
                                                    </td>
                                                </tr>
                                            )
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        )}

                    </section>
                </>
            ) : null}

        </div>
    );
}

export default Caixa;