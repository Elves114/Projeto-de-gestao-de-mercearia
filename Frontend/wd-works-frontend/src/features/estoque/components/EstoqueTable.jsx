import { useEffect, useRef, useState } from "react";
import { alterarQuantidadeMinima } from "../services/estoqueService";
import "../style/Estoque.css"

function EstoqueTable({ estoques, onAtualizar }) {

    const [editandoId, setEditandoId] = useState(null);
    const [quantidadeMinima, setQuantidadeMinima] = useState("");
    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");

    // Controla as animações de alteração do stock
    const [animacoes, setAnimacoes] = useState({});

    // Guarda a quantidade anterior de cada produto
    const valoresAnteriores = useRef({});

    // Guarda os timers das animações
    const timersAnimacao = useRef({});


    /* ============================================================
       EDITAR QUANTIDADE MÍNIMA
       ============================================================ */

    function iniciarEdicao(estoque) {

        setEditandoId(estoque.id);

        setQuantidadeMinima(
            estoque.quantidadeMinima
        );

        setErro("");
    }


    function cancelarEdicao() {

        setEditandoId(null);

        setQuantidadeMinima("");

        setErro("");
    }


    async function salvarQuantidadeMinima(estoque) {

        const valor = Number(
            quantidadeMinima
        );

        if (
            quantidadeMinima === "" ||
            valor < 0
        ) {

            setErro(
                "A quantidade mínima não pode ser negativa."
            );

            return;
        }


        try {

            setSalvando(true);

            setErro("");


            const estoqueAtualizado =
                await alterarQuantidadeMinima(
                    estoque.produtoId,
                    valor
                );


            onAtualizar(
                estoqueAtualizado
            );


            cancelarEdicao();


        } catch (error) {

            console.error(
                "Erro ao alterar quantidade mínima:",
                error
            );


            setErro(
                "Não foi possível alterar a quantidade mínima."
            );


        } finally {

            setSalvando(false);

        }
    }


    /* ============================================================
       ESTADO DO STOCK
       ============================================================ */

    function obterEstado(estoque) {

        if (estoque.quantidade === 0) {

            return {
                texto: "Sem stock",
                classe: "stock-sem"
            };
        }


        if (
            estoque.quantidade <=
            estoque.quantidadeMinima
        ) {

            return {
                texto: "Stock baixo",
                classe: "stock-baixo"
            };
        }


        return {
            texto: "Normal",
            classe: "stock-normal"
        };
    }


    /* ============================================================
       ANIMAÇÃO DO CONTADOR
       ============================================================ */

    useEffect(() => {

    const timersAtuais =
        timersAnimacao.current;

    estoques.forEach((estoque) => {

        const id =
            estoque.id;

        const quantidadeAtual =
            estoque.quantidade;

        const quantidadeAnterior =
            valoresAnteriores.current[id];


        /*
         * Primeira vez que o produto aparece.
         * Não devemos animar.
         */
        if (
            quantidadeAnterior === undefined
        ) {

            valoresAnteriores.current[id] =
                quantidadeAtual;

            return;
        }


        /*
         * A quantidade não mudou.
         */
        if (
            quantidadeAnterior ===
            quantidadeAtual
        ) {

            return;
        }


        /*
         * Descobrir o tipo da movimentação.
         */

        let tipo = "ajuste";


        if (
            quantidadeAtual >
            quantidadeAnterior
        ) {

            tipo = "entrada";

        } else if (
            quantidadeAtual <
            quantidadeAnterior
        ) {

            tipo = "saida";
        }


        /*
         * Cancelar animação anterior
         * deste produto, se existir.
         */

        if (
            timersAtuais[id]
        ) {

            clearTimeout(
                timersAtuais[id]
            );
        }


        /*
         * Criar identificador único
         * para forçar a animação CSS.
         */

        const chave =
            `${id}-${Date.now()}`;


        setAnimacoes((atuais) => ({
            ...atuais,

            [id]: {
                anterior:
                    quantidadeAnterior,

                atual:
                    quantidadeAtual,

                tipo,

                chave
            }
        }));


        /*
         * Remover a animação depois
         * de terminar.
         */

        timersAtuais[id] =
            setTimeout(() => {

                setAnimacoes((atuais) => {

                    const copia = {
                        ...atuais
                    };

                    delete copia[id];

                    return copia;
                });


                delete timersAtuais[id];

            }, 500);


        /*
         * Guardar a nova quantidade
         * como quantidade anterior.
         */

        valoresAnteriores.current[id] =
            quantidadeAtual;

    });


    /*
     * Cleanup.
     *
     * Usamos a referência capturada
     * no início do effect, em vez de
     * acessar timersAnimacao.current
     * diretamente.
     */

    return () => {

        Object.values(
            timersAtuais
        ).forEach((timer) => {

            clearTimeout(timer);

        });

    };

}, [estoques]);

    /* ============================================================
       RENDER
       ============================================================ */

    return (

        <div className="table-container">

            <table className="data-table">

                <thead>

                    <tr>

                        <th>
                            Produto
                        </th>

                        <th>
                            Quantidade
                        </th>

                        <th>
                            Quantidade mínima
                        </th>

                        <th>
                            Estado
                        </th>

                        <th>
                            Ações
                        </th>

                    </tr>

                </thead>


                <tbody>

                    {estoques.map((estoque) => {

                        const estado =
                            obterEstado(
                                estoque
                            );


                        const editando =
                            editandoId ===
                            estoque.id;


                        const animacao =
                            animacoes[
                                estoque.id
                            ];


                        /*
                         * Estado visual da linha.
                         */

                        let classeLinha = "";


                        if (
                            estoque.quantidade === 0
                        ) {

                            classeLinha =
                                "stock-row-empty";

                        } else if (
                            estoque.quantidade <=
                            estoque.quantidadeMinima
                        ) {

                            classeLinha =
                                "stock-row-critical";

                        }


                        return (

                            <tr
                                key={estoque.id}
                                className={classeLinha}
                            >

                                {/* ==================================================
                                    PRODUTO
                                   ================================================== */}

                                <td className="produto-nome">

                                    {estoque.produtoNome}

                                </td>


                                {/* ==================================================
                                    QUANTIDADE
                                   ================================================== */}

                                <td>

                                    <span
                                        className={`
                                            stock-quantidade
                                            ${
                                                animacao
                                                    ? `stock-flash-${animacao.tipo}`
                                                    : ""
                                            }
                                        `}
                                    >

                                        {animacao ? (

                                            <>

                                                {/* Número antigo */}

                                                <span
                                                    key={
                                                        `old-${animacao.chave}`
                                                    }
                                                    className="stock-number-old"
                                                >

                                                    {
                                                        animacao.anterior
                                                    }

                                                </span>


                                                {/* Número novo */}

                                                <span
                                                    key={
                                                        `new-${animacao.chave}`
                                                    }
                                                    className="stock-number-new"
                                                >

                                                    {
                                                        animacao.atual
                                                    }

                                                </span>

                                            </>

                                        ) : (

                                            estoque.quantidade

                                        )}

                                    </span>

                                </td>


                                {/* ==================================================
                                    QUANTIDADE MÍNIMA
                                   ================================================== */}

                                <td>

                                    {editando ? (

                                        <input
                                            type="number"
                                            min="0"
                                            value={
                                                quantidadeMinima
                                            }
                                            onChange={(event) =>
                                                setQuantidadeMinima(
                                                    event.target.value
                                                )
                                            }
                                        />

                                    ) : (

                                        estoque.quantidadeMinima

                                    )}

                                </td>


                                {/* ==================================================
                                    ESTADO
                                   ================================================== */}

                                <td>

                                    <span
                                        className={
                                            `stock-status ${estado.classe}`
                                        }
                                    >

                                        {estado.texto}

                                    </span>

                                </td>


                                {/* ==================================================
                                    AÇÕES
                                   ================================================== */}

                                <td>

                                    {editando ? (

                                        <>

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    salvarQuantidadeMinima(
                                                        estoque
                                                    )
                                                }
                                                disabled={
                                                    salvando
                                                }
                                            >

                                                {salvando
                                                    ? "Salvando..."
                                                    : "Salvar"}

                                            </button>


                                            <button
                                                type="button"
                                                onClick={
                                                    cancelarEdicao
                                                }
                                                disabled={
                                                    salvando
                                                }
                                            >

                                                Cancelar

                                            </button>

                                        </>

                                    ) : (

                                        <button
                                            type="button"
                                            onClick={() =>
                                                iniciarEdicao(
                                                    estoque
                                                )
                                            }
                                        >

                                            Alterar mínimo

                                        </button>

                                    )}

                                </td>

                            </tr>

                        );

                    })}

                </tbody>

            </table>


            {/* ====================================================
                ERRO
               ==================================================== */}

            {erro && (

                <p className="form-error">

                    {erro}

                </p>

            )}

        </div>
    );
}

export default EstoqueTable;