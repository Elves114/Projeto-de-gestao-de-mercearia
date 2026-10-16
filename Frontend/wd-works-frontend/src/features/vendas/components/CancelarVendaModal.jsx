import { useEffect, useState } from "react";
import "../style/Venda.css";

function CancelarVendaModal({ venda, onConfirmar, onFechar, carregando }) {

    const [motivo, setMotivo] = useState("");
    const [erro, setErro] = useState("");

    /* Fechar com Escape */
    useEffect(() => {
        function onKey(e) {
            if (e.key === "Escape" && !carregando) {
                onFechar();
            }
        }
        window.addEventListener("keydown", onKey);
        return () => window.removeEventListener("keydown", onKey);
    }, [onFechar, carregando]);

    if (!venda) {
        return null;
    }

    function handleSubmit(e) {
        e.preventDefault();

        const texto = motivo.trim();

        if (!texto) {
            setErro("O motivo é obrigatório.");
            return;
        }

        if (texto.length > 500) {
            setErro("O motivo não pode ultrapassar 500 caracteres.");
            return;
        }

        onConfirmar(texto);
    }

    return (
        <div
            className="venda-modal-overlay"
            onClick={(e) => {
                if (e.target === e.currentTarget && !carregando) {
                    onFechar();
                }
            }}
        >
            <div className="venda-modal">

                <header className="venda-modal-header">
                    <div>
                        <span className="venda-modal-overline">
                            Cancelamento
                        </span>
                        <h3>Cancelar venda #{venda.id}</h3>
                    </div>

                    <button
                        type="button"
                        className="venda-modal-close"
                        onClick={onFechar}
                        disabled={carregando}
                        aria-label="Fechar"
                    >
                        ×
                    </button>
                </header>


                <div className="venda-modal-body">

                    <div className="venda-modal-info">
                        <div>
                            <span>Total</span>
                            <strong>
                                {Number(venda.total).toLocaleString(
                                    "pt-MZ",
                                    {
                                        minimumFractionDigits: 2,
                                        maximumFractionDigits: 2,
                                    }
                                )} MT
                            </strong>
                        </div>

                        <div>
                            <span>Data</span>
                            <strong>
                                {new Date(venda.dataVenda)
                                    .toLocaleString("pt-MZ")}
                            </strong>
                        </div>
                    </div>


                    <div className="venda-modal-aviso">
                        ⚠️ O stock dos produtos vendidos será devolvido
                        automaticamente. Esta operação fica registada na
                        auditoria.
                    </div>


                    <form onSubmit={handleSubmit}>

                        <div className="venda-modal-field">
                            <label htmlFor="motivo-cancelamento">
                                Motivo do cancelamento *
                            </label>

                            <textarea
                                id="motivo-cancelamento"
                                value={motivo}
                                onChange={(e) => {
                                    setMotivo(e.target.value);
                                    setErro("");
                                }}
                                placeholder="Ex: Cliente desistiu da compra"
                                rows={3}
                                maxLength={500}
                                autoFocus
                                disabled={carregando}
                            />

                            <div className="venda-modal-counter">
                                {motivo.length}/500
                            </div>
                        </div>


                        {erro && (
                            <p className="venda-modal-erro">
                                {erro}
                            </p>
                        )}


                        <div className="venda-modal-actions">

                            <button
                                type="button"
                                className="button-secondary"
                                onClick={onFechar}
                                disabled={carregando}
                            >
                                Voltar
                            </button>

                            <button
                                type="submit"
                                className="venda-modal-confirmar"
                                disabled={carregando}
                            >
                                {carregando
                                    ? "A cancelar..."
                                    : "Confirmar cancelamento"}
                            </button>

                        </div>

                    </form>

                </div>

            </div>
        </div>
    );
}

export default CancelarVendaModal;