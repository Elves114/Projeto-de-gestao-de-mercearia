import { useState } from "react";
import { criarVenda } from "../services/vendaService";
import ItemVendaForm from "./ItemVendaForm";

function VendaForm() {
    const [itens, setItens] = useState([]);
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState(null);
    const [sucesso, setSucesso] = useState(false);

    function adicionarItem(item) {
        setItens((itensAtuais) => [
            ...itensAtuais,
            item,
        ]);

        setErro(null);
        setSucesso(false);
    }

    function removerItem(index) {
        setItens((itensAtuais) =>
            itensAtuais.filter(
                (_, itemIndex) => itemIndex !== index
            )
        );
    }

    function calcularSubtotal(item) {
        return item.precoVenda * item.quantidade;
    }

    function calcularTotal() {
        return itens.reduce(
            (total, item) =>
                total + calcularSubtotal(item),
            0
        );
    }

    function formatarPreco(valor) {
        return Number(valor).toLocaleString(
            "pt-MZ",
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
            }
        ) + " MT";
    }

    async function finalizarVenda() {
        if (itens.length === 0) {
            setErro(
                "Adicione pelo menos um produto à venda."
            );
            return;
        }

        try {
            setCarregando(true);
            setErro(null);
            setSucesso(false);

            await criarVenda({
                itens: itens.map((item) => ({
                    produtoId: item.produtoId,
                    quantidade: item.quantidade,
                })),
            });

            setItens([]);
            setSucesso(
                "Venda registada com sucesso!"
            );

        } catch (error) {
            console.error(
                "Erro ao registar venda:",
                error
            );

            setErro(
                "Não foi possível registar a venda."
            );
        } finally {
            setCarregando(false);
        }
    }

    return (
        <div className="venda-form">

            <div className="venda-form-header">
                <div>
                    <h2>Registar Venda</h2>

                    <p>
                        Adicione os produtos que fazem
                        parte desta venda.
                    </p>
                </div>
            </div>

            <ItemVendaForm
                onAdicionar={adicionarItem}
            />

            <section className="sale-items-section">

                <div className="sale-items-section-header">
                    <div>
                        <h3>Itens da venda</h3>

                        <span>
                            {itens.length}{" "}
                            {itens.length === 1
                                ? "produto"
                                : "produtos"}
                        </span>
                    </div>
                </div>

                {itens.length === 0 ? (
                    <div className="empty-state">
                        <p>
                            Nenhum produto adicionado.
                        </p>

                        <span>
                            Pesquise um produto acima
                            para começar a venda.
                        </span>
                    </div>
                ) : (
                    <div className="sale-items">

                        <div className="sale-items-header">
                            <span>Produto</span>
                            <span>Quantidade</span>
                            <span>Preço</span>
                            <span>Subtotal</span>
                            <span></span>
                        </div>

                        {itens.map((item, index) => (
                            <div
                                className="sale-item"
                                key={`${item.produtoId}-${index}`}
                            >
                                <strong>
                                    {item.produtoNome}
                                </strong>

                                <span>
                                    {item.quantidade}
                                </span>

                                <span>
                                    {formatarPreco(
                                        item.precoVenda
                                    )}
                                </span>

                                <strong>
                                    {formatarPreco(
                                        calcularSubtotal(item)
                                    )}
                                </strong>

                                <button
                                    type="button"
                                    className="sale-remove-button"
                                    onClick={() =>
                                        removerItem(index)
                                    }
                                    disabled={carregando}
                                >
                                    Remover
                                </button>
                            </div>
                        ))}

                    </div>
                )}

            </section>

            {itens.length > 0 && (
                <div className="sale-total">

                    <span>
                        Total da venda
                    </span>

                    <strong>
                        {formatarPreco(
                            calcularTotal()
                        )}
                    </strong>

                </div>
            )}

            {erro && (
                <p className="form-error">
                    {erro}
                </p>
            )}

            {sucesso && (
                <p className="form-success">
                    {sucesso}
                </p>
            )}

            <div className="sale-actions">

                <button
                    type="button"
                    className="finish-sale-button"
                    onClick={finalizarVenda}
                    disabled={
                        carregando ||
                        itens.length === 0
                    }
                >
                    {carregando
                        ? "A registar..."
                        : "Finalizar Venda"}
                </button>

            </div>

        </div>
    );
}

export default VendaForm;