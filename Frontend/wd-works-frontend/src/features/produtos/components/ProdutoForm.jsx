import { useEffect, useState } from "react";
import { listarCategorias } from "../../categorias/services/categoriaService";


function ProdutoForm({
    produto = null,
    onSubmit,
    onCancelar,
}) {

    const [nome, setNome] = useState("");

    const [precoCompra, setPrecoCompra] = useState("");

    const [precoVenda, setPrecoVenda] = useState("");

    const [pesquisaCategoria, setPesquisaCategoria] =
        useState("");

    const [categorias, setCategorias] =
        useState([]);

    const [categoriaSelecionada, setCategoriaSelecionada] =
        useState(null);

    const [carregandoCategorias, setCarregandoCategorias] =
        useState(false);

    const [erroCategoria, setErroCategoria] =
        useState("");


    /*
     * Preenche o formulário quando
     * um produto é selecionado para edição.
     */
    useEffect(() => {

        if (!produto) {
            return;
        }


        const categoria = produto.categoriaId
            ? {
                id: produto.categoriaId,
                nome: produto.categoriaNome,
            }
            : null;


        const timeout = setTimeout(() => {

            setNome(
                produto.nome ?? ""
            );

            setPrecoCompra(
                produto.precoCompra ?? ""
            );

            setPrecoVenda(
                produto.precoVenda ?? ""
            );

            setCategoriaSelecionada(
                categoria
            );

            setPesquisaCategoria(
                produto.categoriaNome ?? ""
            );

            setCategorias([]);

            setErroCategoria("");

        }, 0);


        return () => {

            clearTimeout(timeout);

        };

    }, [produto]);


    /*
     * Pesquisa de categorias
     */
    useEffect(() => {

        const pesquisa =
            pesquisaCategoria.trim();


        if (
            !pesquisa ||
            categoriaSelecionada
        ) {
            return;
        }


        let ativo = true;


        const timeout = setTimeout(async () => {

            try {

                setCarregandoCategorias(true);

                setErroCategoria("");


                const response =
                    await listarCategorias(
                        0,
                        10,
                        pesquisa
                    );


                if (!ativo) {
                    return;
                }


                setCategorias(
                    response.content
                );


            } catch (error) {

                console.error(
                    "Erro ao pesquisar categorias:",
                    error
                );


                if (!ativo) {
                    return;
                }


                setCategorias([]);

                setErroCategoria(
                    "Não foi possível pesquisar categorias."
                );


            } finally {

                if (ativo) {

                    setCarregandoCategorias(
                        false
                    );

                }

            }

        }, 300);


        return () => {

            ativo = false;

            clearTimeout(timeout);

        };

    }, [
        pesquisaCategoria,
        categoriaSelecionada
    ]);


    /*
     * Selecionar categoria
     */
    function selecionarCategoria(categoria) {

        setCategoriaSelecionada(
            categoria
        );

        setPesquisaCategoria(
            categoria.nome
        );

        setCategorias([]);

        setErroCategoria("");

    }


    /*
     * Alterar categoria
     */
    function alterarCategoria() {

        setCategoriaSelecionada(
            null
        );

        setPesquisaCategoria("");

        setCategorias([]);

        setErroCategoria("");

    }


    /*
     * Submeter formulário
     */
    function handleSubmit(event) {

        event.preventDefault();


        if (!categoriaSelecionada) {

            setErroCategoria(
                "Selecione uma categoria."
            );

            return;
        }


        onSubmit(event);

    }


    /*
     * Cancelar edição
     */
    function handleCancelar() {

        if (onCancelar) {

            onCancelar();

        }

    }


    return (

        <form
            className="produto-form"
            onSubmit={handleSubmit}
        >

            {/* ==================================================
                NOME
                ================================================== */}

            <div className="form-group">

                <label htmlFor="produto-nome">
                    Nome
                </label>


                <input
                    id="produto-nome"
                    type="text"
                    name="nome"
                    value={nome}
                    onChange={(event) =>
                        setNome(
                            event.target.value
                        )
                    }
                    placeholder="Ex: Coca-Cola 2L"
                    required
                />

            </div>


            {/* ==================================================
                PREÇO DE COMPRA
                ================================================== */}

            <div className="form-group">

                <label htmlFor="preco-compra">
                    Preço de compra
                </label>


                <input
                    id="preco-compra"
                    type="number"
                    name="precoCompra"
                    value={precoCompra}
                    onChange={(event) =>
                        setPrecoCompra(
                            event.target.value
                        )
                    }
                    step="0.01"
                    min="0"
                    placeholder="0,00"
                    required
                />

            </div>


            {/* ==================================================
                PREÇO DE VENDA
                ================================================== */}

            <div className="form-group">

                <label htmlFor="preco-venda">
                    Preço de venda
                </label>


                <input
                    id="preco-venda"
                    type="number"
                    name="precoVenda"
                    value={precoVenda}
                    onChange={(event) =>
                        setPrecoVenda(
                            event.target.value
                        )
                    }
                    step="0.01"
                    min="0"
                    placeholder="0,00"
                    required
                />

            </div>


            {/* ==================================================
                CATEGORIA
                ================================================== */}

            <div className="form-group">

                <label htmlFor="categoria">
                    Categoria
                </label>


                <input
                    id="categoria"
                    type="text"
                    value={pesquisaCategoria}
                    onChange={(event) => {

                        setPesquisaCategoria(
                            event.target.value
                        );

                        setCategoriaSelecionada(
                            null
                        );

                        setCategorias([]);

                        setErroCategoria("");

                    }}
                    placeholder="Digite o nome da categoria..."
                    autoComplete="off"
                    required
                />


                {/* CARREGANDO */}

                {carregandoCategorias && (

                    <p>
                        A pesquisar categorias...
                    </p>

                )}


                {/* RESULTADOS */}

                {!carregandoCategorias &&
                    categorias.length > 0 && (

                    <div className="categoria-resultados">

                        {categorias.map(
                            (categoria) => (

                                <button
                                    key={
                                        categoria.id
                                    }
                                    type="button"
                                    onClick={() =>
                                        selecionarCategoria(
                                            categoria
                                        )
                                    }
                                >

                                    {
                                        categoria.nome
                                    }

                                </button>

                            )
                        )}

                    </div>

                )}


                {/* NENHUMA CATEGORIA */}

                {pesquisaCategoria.trim() &&
                    !carregandoCategorias &&
                    categorias.length === 0 &&
                    !categoriaSelecionada && (

                    <p>
                        Nenhuma categoria encontrada.
                    </p>

                )}


                {/* CATEGORIA SELECIONADA */}

                {categoriaSelecionada && (

                    <div>

                        Categoria selecionada:{" "}

                        <strong>
                            {
                                categoriaSelecionada.nome
                            }
                        </strong>


                        <button
                            type="button"
                            onClick={
                                alterarCategoria
                            }
                        >
                            Alterar
                        </button>

                    </div>

                )}


                {/* ERRO */}

                {erroCategoria && (

                    <p>
                        {erroCategoria}
                    </p>

                )}


                {/* ID DA CATEGORIA */}

                <input
                    type="hidden"
                    name="categoriaId"
                    value={
                        categoriaSelecionada
                            ? categoriaSelecionada.id
                            : ""
                    }
                />

            </div>


            {/* ==================================================
                BOTÕES
                ================================================== */}

            <div className="form-actions">

                <button
                    className="button-primary"
                    type="submit"
                >

                    {produto
                        ? "Guardar alterações"
                        : "Criar produto"}

                </button>


                {produto && (

                    <button
                        type="button"
                        className="button-secondary"
                        onClick={
                            handleCancelar
                        }
                    >
                        Cancelar
                    </button>

                )}

            </div>

        </form>

    );

}

export default ProdutoForm;