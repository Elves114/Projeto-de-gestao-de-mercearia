import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { buscarVenda, cancelarVenda } from "../services/vendaService";
import { obterMensagemErro } from "../../../services/api";
import CancelarVendaModal from "../components/CancelarVendaModal";
import "../style/Venda.css";

function formatarValor(valor) {
    return Number(valor).toLocaleString("pt-MZ", {
        style: "currency",
        currency: "MZN",
    });
}

function DetalhesVenda() {

    const { id } = useParams();

    const [venda, setVenda] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    /* ============================================================
       CANCELAMENTO
       ============================================================ */

    const [modalAberto, setModalAberto] = useState(false);
    const [cancelando, setCancelando] = useState(false);
    const [sucesso, setSucesso] = useState("");

    /* ============================================================
       CARREGAR VENDA
       ============================================================ */

    useEffect(() => {

        let ativo = true;

        async function carregarVenda() {
            try {

                const data = await buscarVenda(id);

                if (ativo) {
                    setVenda(data);
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

        carregarVenda();

        return () => {
            ativo = false;
        };

    }, [id]);

    /* ============================================================
       CONFIRMAR CANCELAMENTO
       ============================================================ */

    async function confirmarCancelamento(motivo) {

        if (!venda) return;

        try {

            setCancelando(true);

            const vendaAtualizada = await cancelarVenda(venda.id, motivo);

            setVenda(vendaAtualizada);
            setModalAberto(false);
            setSucesso("Venda cancelada. O stock foi devolvido.");

        } catch (error) {

            console.error(error);
            alert(obterMensagemErro(error));

        } finally {

            setCancelando(false);
        }
    }

    function fecharModal() {
        if (cancelando) return;
        setModalAberto(false);
    }

    /* ============================================================
       ESTADOS
       ============================================================ */

    if (carregando) {
        return (
            <div className="page-container">
                <p>Carregando venda...</p>
            </div>
        );
    }

    if (erro) {
        return (
            <div className="page-container">
                <p>{erro}</p>
                <Link to="/vendas">Voltar para vendas</Link>
            </div>
        );
    }

    if (!venda) {
        return (
            <div className="page-container">
                <p>Venda não encontrada.</p>
                <Link to="/vendas">Voltar para vendas</Link>
            </div>
        );
    }

    const cancelada = venda.status === "CANCELADA";

    /* ============================================================
       RENDER
       ============================================================ */

    return (

        <div className="page-container">

            {/* =================================================
                CABEÇALHO
               ================================================= */}

            <div className="page-header">

                <div>

                    <h1>
                        Venda #{venda.id}

                        {cancelada ? (
                            <span className="venda-status venda-status-cancelada">
                                Cancelada
                            </span>
                        ) : (
                            <span className="venda-status venda-status-concluida">
                                Concluída
                            </span>
                        )}
                    </h1>

                    <p>
                        Realizada em{" "}
                        {new Date(venda.dataVenda).toLocaleString("pt-MZ")}
                    </p>

                </div>


                <div className="venda-detalhes-actions">

                    {!cancelada && (
                        <button
                            type="button"
                            className="venda-cancelar-btn-destaque"
                            onClick={() => {
                                setSucesso("");
                                setModalAberto(true);
                            }}
                        >
                            Cancelar venda
                        </button>
                    )}

                    <Link
                        to="/vendas"
                        className="button-secondary"
                    >
                        ← Voltar para vendas
                    </Link>

                </div>

            </div>


            {/* =================================================
                MENSAGEM DE SUCESSO
               ================================================= */}

            {sucesso && (
                <div className="venda-sucesso">
                    {sucesso}
                </div>
            )}


            {/* =================================================
                PAINEL DE CANCELAMENTO (se aplicável)
               ================================================= */}

            {cancelada && (
                <section className="venda-cancelada-info">

                    <div className="venda-cancelada-badge">
                        Cancelada
                    </div>


                    <div className="venda-cancelada-detalhes">

                        <div>
                            <span>Cancelada em</span>
                            <strong>
                                {new Date(venda.canceladaEm)
                                    .toLocaleString("pt-MZ")}
                            </strong>
                        </div>

                        <div>
                            <span>Por</span>
                            <strong>{venda.canceladaPorNome}</strong>
                        </div>

                        <div className="venda-cancelada-motivo">
                            <span>Motivo</span>
                            <strong>{venda.motivoCancelamento}</strong>
                        </div>

                    </div>

                </section>
            )}


            {/* =================================================
                CARDS DE RESUMO
               ================================================= */}

            <section className="venda-info">

                <div className="info-card">
                    <span>Funcionário</span>
                    <strong>{venda.usuarioNome}</strong>
                </div>

                <div className="info-card">
                    <span>Total</span>
                    <strong>{formatarValor(venda.total)}</strong>
                </div>

                <div className="info-card">
                    <span>Lucro</span>
                    <strong>{formatarValor(venda.lucroTotal)}</strong>
                </div>

            </section>


            {/* =================================================
                PRODUTOS DA VENDA
               ================================================= */}

            <section className="content-card">

                <div className="content-card-header">

                    <div>
                        <h2>Produtos vendidos</h2>
                        <p>Produtos incluídos nesta venda.</p>
                    </div>

                </div>


                {venda.itens.length === 0 ? (

                    <div className="empty-state">
                        <p>Esta venda não possui produtos.</p>
                    </div>

                ) : (

                    <div className="table-container">

                        <table>

                            <thead>
                                <tr>
                                    <th>Produto</th>
                                    <th>Quantidade</th>
                                    <th>Preço unitário</th>
                                    <th>Subtotal</th>
                                    <th>Lucro</th>
                                </tr>
                            </thead>

                            <tbody>

                                {venda.itens.map((item) => (

                                    <tr
                                        key={item.id}
                                        className="venda-produto-row"
                                    >
                                        <td>
                                            <strong>
                                                {item.produtoNome}
                                            </strong>
                                        </td>

                                        <td>{item.quantidade}</td>

                                        <td>{formatarValor(item.precoUnitario)}</td>

                                        <td>{formatarValor(item.subtotal)}</td>

                                        <td>{formatarValor(item.lucro)}</td>
                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </div>

                )}

            </section>


            {/* =================================================
                MODAL DE CANCELAMENTO
               ================================================= */}

            <CancelarVendaModal
                key={modalAberto ? venda.id : "fechado"}
                venda={modalAberto ? venda : null}
                onConfirmar={confirmarCancelamento}
                onFechar={fecharModal}
                carregando={cancelando}
            />

        </div>
    );
}

export default DetalhesVenda;