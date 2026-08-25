import { useState } from "react";
import { criarVenda } from "../services/vendaService";
import ItemVendaForm from "./ItemVendaForm";

function VendaForm() {

    const [itens, setItens] = useState([]);
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState(null);
    const [sucesso, setSucesso] = useState(false);

    const [formularioEmFoco, setFormularioEmFoco] =
        useState(false);


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

        return (
            item.precoVenda *
            item.quantidade
        );
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


    /*
     * ============================================================
     * FOCUS MODE
     * ============================================================
     */

    function entrarNoModoFoco() {

        setFormularioEmFoco(true);
    }


    function sairDoModoFoco(event) {

        /*
         * relatedTarget representa o elemento
         * para onde o utilizador está a clicar.
         *
         * Se o novo elemento ainda estiver dentro
         * do formulário de produto, continuamos
         * em Focus Mode.
         */

        if (
            !event.currentTarget.contains(
                event.relatedTarget
            )
        ) {
            setFormularioEmFoco(false);
        }
    }


    return (

        <div
            className={
                `venda-form ${
                    formularioEmFoco
                        ? "venda-form-focus"
                        : ""
                }`
            }
        >

            <div className="venda-form-header">

                <div>

                    <h2>
                        Registar Venda
                    </h2>

                    <p>
                        Adicione os produtos que fazem
                        parte desta venda.
                    </p>

                </div>

            </div>


            {/* =====================================================
                ÁREA DE PESQUISA / ADIÇÃO
               ===================================================== */}

            <div
                className="venda-produto-area"

                onFocus={entrarNoModoFoco}

                onBlur={sairDoModoFoco}
            >

                <ItemVendaForm
                    onAdicionar={adicionarItem}
                />

            </div>


            {/* =====================================================
                CARRINHO
               ===================================================== */}

            <section
                className={
                    `sale-items-section ${
                        formularioEmFoco
                            ? "sale-items-focus-secondary"
                            : ""
                    }`
                }
            >

                <div className="sale-items-section-header">

                    <div>

                        <h3>
                            Itens da venda
                        </h3>

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

                            <span>
                                Produto
                            </span>

                            <span>
                                Quantidade
                            </span>

                            <span>
                                Preço
                            </span>

                            <span>
                                Subtotal
                            </span>

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


            {/* =====================================================
                TOTAL
               ===================================================== */}

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


            {/* =====================================================
                MENSAGENS
               ===================================================== */}

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


            {/* =====================================================
                AÇÕES
               ===================================================== */}

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