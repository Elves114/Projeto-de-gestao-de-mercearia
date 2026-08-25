import { useEffect, useState } from "react";

import { listarCategorias } from "../../categorias/services/categoriaService";

import ProdutoTable from "../components/ProdutoTable";
import ProdutoForm from "../components/ProdutoForm";
import "../style/Produto.css";

import {
    listarProdutos,
    criarProduto,
    atualizarProduto,
    desativarProduto,
} from "../services/produtoService";


function Produtos() {

    const [produtos, setProdutos] = useState([]);

    const [categoriasFiltro, setCategoriasFiltro] = useState([]);

    const [pesquisaCategoria, setPesquisaCategoria] = useState("");

    const [pesquisa, setPesquisa] = useState("");

    const [produtoEditando, setProdutoEditando] = useState(null);

    /*
     * Controla a abertura/fecho do formulário lateral
     */
    const [formularioAberto, setFormularioAberto] = useState(false);

    const [formularioKey, setFormularioKey] = useState(0);

    /*
     * Produto que acabou de ser criado.
     * Usado para destacar a nova linha da tabela.
     */
    const [produtoAnimando, setProdutoAnimando] = useState(null);

    /*
     * Produto que está a ser removido.
     * Usado para fazer a animação de eliminação.
     */
    const [produtoRemovendo, setProdutoRemovendo] = useState(null);

    /*
     * Animação rápida do formulário depois de guardar.
     */
    const [formFlash, setFormFlash] = useState(false);


    const [filtros, setFiltros] = useState({
        status: "ATIVO",
        categoriaId: "",
        precoMin: "",
        precoMax: "",
        quantidadeMin: "",
        quantidadeMax: "",
    });

    const [pagina, setPagina] = useState(0);

    const [carregando, setCarregando] = useState(true);

    const [erro, setErro] = useState("");

    const [mensagem, setMensagem] = useState("");


    /*
     * Pesquisa de categorias para o filtro
     */
    useEffect(() => {

        const termo = pesquisaCategoria.trim();

        if (!termo) {
            return;
        }

        let ativo = true;

        const timeout = setTimeout(async () => {

            try {

                const response = await listarCategorias(
                    0,
                    10,
                    termo
                );

                if (!ativo) {
                    return;
                }

                setCategoriasFiltro(
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

                setCategoriasFiltro([]);

            }

        }, 300);


        return () => {

            ativo = false;

            clearTimeout(timeout);

        };

    }, [pesquisaCategoria]);


    /*
     * Carregar produtos
     */
    useEffect(() => {

        let ativo = true;

        const timeout = setTimeout(async () => {

            try {

                setCarregando(true);

                setErro("");

                const response =
                    await listarProdutos(
                        pagina,
                        10,
                        pesquisa,
                        filtros
                    );

                if (!ativo) {
                    return;
                }

                setProdutos(
                    response.content
                );

            } catch (error) {

                console.error(
                    "Erro ao carregar produtos:",
                    error
                );

                if (!ativo) {
                    return;
                }

                setProdutos([]);

                setErro(
                    "Não foi possível carregar os produtos."
                );

            } finally {

                if (ativo) {
                    setCarregando(false);
                }

            }

        }, 300);


        return () => {

            ativo = false;

            clearTimeout(timeout);

        };

    }, [
        pesquisa,
        pagina,
        filtros
    ]);


    /*
     * Pesquisa por nome
     */
    function handlePesquisa(event) {

        const valor =
            event.target.value;

        setPesquisa(valor);

        setPagina(0);

        setMensagem("");

    }


    /*
     * Alterar filtro
     */
    function handleFiltro(event) {

        const {
            name,
            value
        } = event.target;

        setFiltros(
            (filtrosAtuais) => ({
                ...filtrosAtuais,
                [name]: value,
            })
        );

        setPagina(0);

        setMensagem("");

    }


    /*
     * Limpar filtros
     */
    function limparFiltros() {

        setPesquisa("");

        setPesquisaCategoria("");

        setCategoriasFiltro([]);

        setFiltros({
            status: "ATIVO",
            categoriaId: "",
            precoMin: "",
            precoMax: "",
            quantidadeMin: "",
            quantidadeMax: "",
        });

        setPagina(0);

    }


    /*
     * Abrir formulário para adicionar produto
     */
    function abrirFormulario() {

        setProdutoEditando(null);

        setErro("");

        setMensagem("");

        setFormularioAberto(true);

    }


    /*
     * Criar produto
     */
    async function handleCriarProduto(event) {

        event.preventDefault();

        setErro("");

        setMensagem("");

        const formData =
            new FormData(event.target);


        const produto = {

            nome:
                formData.get("nome"),

            precoCompra:
                Number(
                    formData.get(
                        "precoCompra"
                    )
                ),

            precoVenda:
                Number(
                    formData.get(
                        "precoVenda"
                    )
                ),

            categoriaId:
                Number(
                    formData.get(
                        "categoriaId"
                    )
                ),
        };


        try {

            const novoProduto =
                await criarProduto(
                    produto
                );


            /*
             * Verifica se existem filtros reais.
             *
             * O status "ATIVO" é o estado padrão,
             * portanto NÃO deve ser considerado
             * como um filtro adicional.
             */
            const existemFiltros =
                filtros.status !== "ATIVO" ||
                filtros.categoriaId !== "" ||
                filtros.precoMin !== "" ||
                filtros.precoMax !== "" ||
                filtros.quantidadeMin !== "" ||
                filtros.quantidadeMax !== "";


            /*
             * Se estamos na primeira página,
             * sem pesquisa e sem filtros,
             * colocamos o novo produto
             * diretamente no topo da tabela.
             */
            if (
                pagina === 0 &&
                !pesquisa.trim() &&
                !existemFiltros
            ) {

                setProdutos(
                    (produtosAtuais) => [
                        novoProduto,
                        ...produtosAtuais,
                    ]
                );

                /*
                 * Ativa o destaque visual
                 * da nova linha.
                 */
                setProdutoAnimando(
                    novoProduto.id
                );

                /*
                 * Remove o destaque depois
                 * de aproximadamente 1.6 segundos.
                 */
                setTimeout(() => {

                    setProdutoAnimando(null);

                }, 1600);

            } else {

                /*
                 * Se houver pesquisa, filtros
                 * ou estivermos noutra página,
                 * o useEffect fará a listagem
                 * novamente.
                 *
                 * Alterar para o mesmo valor
                 * não provoca uma mudança desnecessária.
                 */
                setPagina(
                    (paginaAtual) =>
                        paginaAtual
                );

            }


            /*
             * Feedback de sucesso
             */
            setMensagem(
                "Produto criado com sucesso."
            );


            /*
             * Limpa os campos do formulário.
             */
            setFormularioKey(
                (valorAtual) => valorAtual + 1
            );


            /*
             * Pequeno flash visual no formulário.
             */
            setFormFlash(true);

            setTimeout(() => {

                setFormFlash(false);

            }, 180);


        } catch (error) {

            console.error(
                "Erro ao criar produto:",
                error
            );

            setErro(
                "Não foi possível criar o produto."
            );

        }

    }


    /*
     * Começar edição
     */
    function handleEditarProduto(produto) {

        setErro("");

        setMensagem("");

        setProdutoEditando(
            produto
        );

        /*
         * Abre automaticamente o painel
         * quando o utilizador clica em editar.
         */
        setFormularioAberto(true);

        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });

    }


    /*
     * Desativar produto
     */
    async function handleDesativarProduto(produto) {

        const confirmar = window.confirm(
            `Tem certeza que deseja desativar o produto "${produto.nome}"?`
        );


        if (!confirmar) {
            return;
        }


        try {

            setErro("");

            setMensagem("");


            await desativarProduto(
                produto.id
            );


            /*
             * Primeiro marcamos o produto
             * como "a ser removido".
             *
             * O ProdutoTable poderá utilizar
             * este ID para iniciar a animação.
             */
            setProdutoRemovendo(
                produto.id
            );


            /*
             * Esperamos a animação terminar
             * antes de remover realmente
             * o produto do estado.
             */
            setTimeout(() => {

                setProdutos(
                    (produtosAtuais) =>
                        produtosAtuais.filter(
                            (item) =>
                                item.id !==
                                produto.id
                        )
                );

                setProdutoRemovendo(null);

            }, 350);


            setMensagem(
                "Produto desativado com sucesso."
            );


        } catch (error) {

            console.error(
                "Erro ao desativar produto:",
                error
            );

            setErro(
                "Não foi possível desativar o produto."
            );

        }

    }


    /*
     * Atualizar produto
     */
    async function handleAtualizarProduto(
        event
    ) {

        event.preventDefault();


        if (!produtoEditando) {
            return;
        }


        setErro("");

        setMensagem("");


        const formData =
            new FormData(
                event.target
            );


        const produto = {

            nome:
                formData.get("nome"),

            precoCompra:
                Number(
                    formData.get(
                        "precoCompra"
                    )
                ),

            precoVenda:
                Number(
                    formData.get(
                        "precoVenda"
                    )
                ),

            categoriaId:
                Number(
                    formData.get(
                        "categoriaId"
                    )
                ),
        };


        try {

            const produtoAtualizado =
                await atualizarProduto(
                    produtoEditando.id,
                    produto
                );


            /*
             * Atualiza somente a linha
             * correspondente na tabela.
             */
            setProdutos(
                (produtosAtuais) =>
                    produtosAtuais.map(
                        (item) =>
                            item.id ===
                                produtoAtualizado.id
                                ? produtoAtualizado
                                : item
                    )
            );


            /*
             * Sai do modo de edição.
             */
            setProdutoEditando(null);


            /*
             * Fecha o painel.
             */
            setFormularioAberto(false);


            setMensagem(
                "Produto atualizado com sucesso."
            );


        } catch (error) {

            console.error(
                "Erro ao atualizar produto:",
                error
            );

            setErro(
                "Não foi possível atualizar o produto."
            );

        }

    }


    /*
     * Cancelar edição
     */
    function cancelarEdicao() {

        setProdutoEditando(null);

        setFormularioAberto(false);

        setErro("");

        setMensagem("");

    }


    /*
     * Estado inicial
     */
    if (
        carregando &&
        produtos.length === 0
    ) {

        return (
            <p>
                A carregar produtos...
            </p>
        );

    }


    if (erro) {

        return (
            <p>
                {erro}
            </p>
        );

    }


    return (

        <div className="produtos-page">


            {/* CABEÇALHO */}

            <div className="page-header">

                <div>

                    <h1>
                        Produtos
                    </h1>

                    <p>
                        Gerencie os produtos
                        da sua empresa.
                    </p>

                </div>


                {/* BOTÃO ADICIONAR */}

                <button
                    type="button"
                    className="button-primary"
                    onClick={
                        abrirFormulario
                    }
                >
                    + Adicionar Produto
                </button>

            </div>


            {/* MENSAGEM */}

            {mensagem && (

                <div className="alert-success">

                    {mensagem}

                </div>

            )}


            {/* CONTEÚDO PRINCIPAL */}

            <div className="produtos-content">


                {/* FORMULÁRIO LATERAL */}

                <section
                    className={`
                        produto-form-panel
                        ${formularioAberto ? "aberto" : ""}
                        ${formFlash ? "form-flash" : ""}
                    `}
                >

                    <div className="section-header">

                        <div>

                            <h2>

                                {produtoEditando
                                    ? "Editar produto"
                                    : "Novo produto"}

                            </h2>


                            <p>

                                {produtoEditando
                                    ? "Atualize os dados do produto."
                                    : "Cadastre um novo produto no sistema."}

                            </p>

                        </div>

                    </div>


                    <ProdutoForm
                        key={formularioKey}
                        produto={produtoEditando}
                        onSubmit={
                            produtoEditando
                                ? handleAtualizarProduto
                                : handleCriarProduto
                        }
                        onCancelar={cancelarEdicao}
                    />

                </section>


                {/* LISTAGEM */}

                <section className="produtos-list-card">


                    <div className="section-header">

                        <div>

                            <h2>
                                Produtos cadastrados
                            </h2>

                            <p>
                                Pesquise e filtre
                                os produtos disponíveis.
                            </p>

                        </div>


                        <span className="categoria-count">

                            {produtos.length}

                        </span>

                    </div>


                    {/* FILTROS */}

                    <div className="produto-filtros">


                        {/* STATUS */}

                        <div className="form-group">

                            <label htmlFor="status-produto">
                                Status
                            </label>


                            <select
                                id="status-produto"
                                name="status"
                                value={
                                    filtros.status
                                }
                                onChange={
                                    handleFiltro
                                }
                            >

                                <option value="ATIVO">
                                    Ativos
                                </option>

                                <option value="INATIVO">
                                    Inativos
                                </option>

                                <option value="">
                                    Todos
                                </option>

                            </select>

                        </div>


                        {/* PESQUISA */}

                        <div className="form-group">

                            <label htmlFor="pesquisa-produto">
                                Produto
                            </label>


                            <input
                                id="pesquisa-produto"
                                type="text"
                                value={pesquisa}
                                onChange={
                                    handlePesquisa
                                }
                                placeholder="Pesquisar por nome..."
                            />

                        </div>


                        {/* CATEGORIA */}

                        <div className="form-group">

                            <label htmlFor="categoria-filtro">
                                Categoria
                            </label>


                            <input
                                id="categoria-filtro"
                                type="text"
                                value={
                                    pesquisaCategoria
                                }
                                onChange={(event) => {

                                    setPesquisaCategoria(
                                        event.target.value
                                    );

                                    setFiltros(
                                        (atual) => ({
                                            ...atual,
                                            categoriaId:
                                                "",
                                        })
                                    );

                                }}
                                placeholder="Pesquisar categoria..."
                            />


                            {categoriasFiltro.length >
                                0 && (

                                    <div className="categoria-sugestoes">

                                        {categoriasFiltro.map(
                                            (categoria) => (

                                                <button
                                                    key={
                                                        categoria.id
                                                    }
                                                    type="button"
                                                    onClick={() => {

                                                        setPesquisaCategoria(
                                                            categoria.nome
                                                        );

                                                        setFiltros(
                                                            (atual) => ({
                                                                ...atual,
                                                                categoriaId:
                                                                    categoria.id,
                                                            })
                                                        );

                                                        setCategoriasFiltro(
                                                            []
                                                        );

                                                        setPagina(
                                                            0
                                                        );

                                                    }}
                                                >

                                                    {
                                                        categoria.nome
                                                    }

                                                </button>

                                            )
                                        )}

                                    </div>

                                )}

                        </div>


                        {/* PREÇO MÍNIMO */}

                        <div className="form-group">

                            <label htmlFor="preco-min">
                                Preço mínimo
                            </label>


                            <input
                                id="preco-min"
                                type="number"
                                name="precoMin"
                                value={
                                    filtros.precoMin
                                }
                                onChange={
                                    handleFiltro
                                }
                                min="0"
                                step="0.01"
                                placeholder="0,00"
                            />

                        </div>


                        {/* PREÇO MÁXIMO */}

                        <div className="form-group">

                            <label htmlFor="preco-max">
                                Preço máximo
                            </label>


                            <input
                                id="preco-max"
                                type="number"
                                name="precoMax"
                                value={
                                    filtros.precoMax
                                }
                                onChange={
                                    handleFiltro
                                }
                                min="0"
                                step="0.01"
                                placeholder="0,00"
                            />

                        </div>


                        {/* QUANTIDADE MÍNIMA */}

                        <div className="form-group">

                            <label htmlFor="quantidade-min">
                                Quantidade mínima
                            </label>


                            <input
                                id="quantidade-min"
                                type="number"
                                name="quantidadeMin"
                                value={
                                    filtros.quantidadeMin
                                }
                                onChange={
                                    handleFiltro
                                }
                                min="0"
                                placeholder="0"
                            />

                        </div>


                        {/* QUANTIDADE MÁXIMA */}

                        <div className="form-group">

                            <label htmlFor="quantidade-max">
                                Quantidade máxima
                            </label>


                            <input
                                id="quantidade-max"
                                type="number"
                                name="quantidadeMax"
                                value={
                                    filtros.quantidadeMax
                                }
                                onChange={
                                    handleFiltro
                                }
                                min="0"
                                placeholder="0"
                            />

                        </div>


                        {/* LIMPAR */}

                        <button
                            type="button"
                            className="button-secondary"
                            onClick={
                                limparFiltros
                            }
                        >
                            Limpar filtros
                        </button>

                    </div>


                    {/* LISTA */}

                    {carregando ? (

                        <p>
                            A pesquisar produtos...
                        </p>

                    ) : produtos.length === 0 ? (

                        <div className="empty-state">

                            <p>
                                Nenhum produto encontrado.
                            </p>

                        </div>

                    ) : (

                        <ProdutoTable

                            produtos={
                                produtos
                            }

                            onEditar={
                                handleEditarProduto
                            }

                            onDesativar={
                                handleDesativarProduto
                            }

                            produtoAnimando={
                                produtoAnimando
                            }

                            produtoRemovendo={
                                produtoRemovendo
                            }

                        />

                    )}


                </section>

            </div>

        </div>

    );
}


export default Produtos;