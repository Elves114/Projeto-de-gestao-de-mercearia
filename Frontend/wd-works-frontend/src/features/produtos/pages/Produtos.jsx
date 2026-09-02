
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
    ativarProduto
} from "../services/produtoService";


function Produtos() {

    const [produtos, setProdutos] = useState([]);

    const [totalProdutos, setTotalProdutos] = useState(0);

    const [totalPaginas, setTotalPaginas] = useState(0);

    const [categoriasFiltro, setCategoriasFiltro] = useState([]);

    const [pesquisaCategoria, setPesquisaCategoria] = useState("");

    const [pesquisa, setPesquisa] = useState("");

    const [produtoEditando, setProdutoEditando] = useState(null);

    const [formularioAberto, setFormularioAberto] = useState(false);

    const [formularioKey, setFormularioKey] = useState(0);

    const [produtoAnimando, setProdutoAnimando] = useState(null);

    const [produtoRemovendo, setProdutoRemovendo] = useState(null);

    const [formFlash, setFormFlash] = useState(false);


    const [filtros, setFiltros] = useState({
        status: "ATIVO",
        categoriaId: "",
        precoMin: "",
        precoMax: "",
        quantidadeMin: "",
        quantidadeMax: "",
    });


    /*
     * Página atual.
     *
     * Spring começa em 0:
     *
     * pagina 0 = página 1
     * pagina 1 = página 2
     */
    const [pagina, setPagina] = useState(0);

    const [carregando, setCarregando] = useState(true);

    const [erro, setErro] = useState("");

    const [mensagem, setMensagem] = useState("");


    /*
     * =========================================================
     * PESQUISA DE CATEGORIAS
     * =========================================================
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
                    response.content || []
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
     * =========================================================
     * CARREGAR PRODUTOS
     * =========================================================
     */

    useEffect(() => {

        let ativo = true;

        const timeout = setTimeout(async () => {

            try {

                setCarregando(true);

                setErro("");

                const response = await listarProdutos(
                    pagina,
                    10,
                    pesquisa,
                    filtros
                );

                console.log(
                    "RESPOSTA DA API:",
                    response
                );


                if (!ativo) {
                    return;
                }


                /*
                 * Produtos da página atual.
                 */
                setProdutos(
                    response.content || []
                );


                /*
                 * IMPORTANTE:
                 *
                 * totalElements e totalPages
                 * estão dentro de response.page.
                 */
                setTotalProdutos(
                    response.page?.totalElements || 0
                );

                setTotalPaginas(
                    response.page?.totalPages || 0
                );


                /*
                 * Caso a página atual tenha deixado
                 * de existir.
                 */
                if (
                    response.page?.totalPages > 0 &&
                    pagina >= response.page.totalPages
                ) {

                    setPagina(
                        response.page.totalPages - 1
                    );

                }

            } catch (error) {

                console.error(
                    "Erro ao carregar produtos:",
                    error
                );

                if (!ativo) {
                    return;
                }

                setProdutos([]);

                setTotalProdutos(0);

                setTotalPaginas(0);

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
     * =========================================================
     * PESQUISA POR NOME
     * =========================================================
     */

    function handlePesquisa(event) {

        const valor = event.target.value;

        setPesquisa(valor);

        setPagina(0);

        setMensagem("");

    }


    /*
     * =========================================================
     * ALTERAR FILTRO
     * =========================================================
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
     * =========================================================
     * LIMPAR FILTROS
     * =========================================================
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

        setMensagem("");

    }


    /*
     * =========================================================
     * ABRIR FORMULÁRIO
     * =========================================================
     */

    function abrirFormulario() {

        setProdutoEditando(null);

        setErro("");

        setMensagem("");

        setFormularioKey(
            (valor) => valor + 1
        );

        setFormularioAberto(true);

    }


    /*
     * =========================================================
     * CRIAR PRODUTO
     * =========================================================
     */

    async function handleCriarProduto(event) {

        event.preventDefault();

        setErro("");

        setMensagem("");

        const formData = new FormData(
            event.target
        );


        const produto = {

            nome:
                formData.get("nome"),

            precoCompra:
                Number(
                    formData.get("precoCompra")
                ),

            precoVenda:
                Number(
                    formData.get("precoVenda")
                ),

            categoriaId:
                Number(
                    formData.get("categoriaId")
                ),

        };


        try {

            const novoProduto =
                await criarProduto(produto);


            /*
             * Voltar para a primeira página.
             */
            setPagina(0);


            /*
             * Se já estamos na primeira página,
             * fazemos o carregamento manual.
             */
            if (pagina === 0) {

                const response =
                    await listarProdutos(
                        0,
                        10,
                        pesquisa,
                        filtros
                    );


                setProdutos(
                    response.content || []
                );

                setTotalProdutos(
                    response.page?.totalElements || 0
                );

                setTotalPaginas(
                    response.page?.totalPages || 0
                );

            }


            /*
             * Anima o produto criado.
             */
            if (
                pagina === 0 &&
                !pesquisa.trim()
            ) {

                setProdutoAnimando(
                    novoProduto.id
                );

                setTimeout(() => {

                    setProdutoAnimando(null);

                }, 1600);

            }


            setMensagem(
                "Produto criado com sucesso."
            );


            setFormularioKey(
                (valorAtual) =>
                    valorAtual + 1
            );


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
                error?.response?.data?.message ||
                "Não foi possível criar o produto."
            );

        }

    }


    /*
     * =========================================================
     * COMEÇAR EDIÇÃO
     * =========================================================
     */

    function handleEditarProduto(produto) {

        setErro("");

        setMensagem("");

        setProdutoEditando(produto);

        setFormularioAberto(true);

        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });

    }


    /*
     * =========================================================
     * DESATIVAR PRODUTO
     * =========================================================
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
             * Animação de remoção.
             */
            setProdutoRemovendo(
                produto.id
            );


            setTimeout(() => {

                setProdutos(
                    (produtosAtuais) =>
                        produtosAtuais.filter(
                            (item) =>
                                item.id !== produto.id
                        )
                );

                setProdutoRemovendo(null);

            }, 350);


            setMensagem(
                "Produto desativado com sucesso."
            );


            /*
             * Recarrega a página atual.
             */
            setTimeout(async () => {

                try {

                    const response =
                        await listarProdutos(
                            pagina,
                            10,
                            pesquisa,
                            filtros
                        );


                    setProdutos(
                        response.content || []
                    );

                    setTotalProdutos(
                        response.page?.totalElements || 0
                    );

                    setTotalPaginas(
                        response.page?.totalPages || 0
                    );


                    /*
                     * Se a página atual deixou
                     * de existir, volta para a última.
                     */
                    if (
                        response.page?.totalPages > 0 &&
                        pagina >= response.page.totalPages
                    ) {

                        setPagina(
                            response.page.totalPages - 1
                        );

                    }

                } catch (error) {

                    console.error(
                        "Erro ao atualizar paginação:",
                        error
                    );

                }

            }, 400);


        } catch (error) {

            console.error(
                "Erro ao desativar produto:",
                error
            );

            setErro(
                error?.response?.data?.message ||
                "Não foi possível desativar o produto."
            );

        }

    }


    /*
     * =========================================================
     * ATIVAR PRODUTO
     * =========================================================
     */

    async function handleAtivarProduto(produto) {

        const confirmar = window.confirm(
            `Tem certeza que deseja ativar o produto "${produto.nome}"?`
        );

        if (!confirmar) {
            return;
        }


        try {

            setErro("");

            setMensagem("");


            await ativarProduto(
                produto.id
            );


            /*
             * Animação de remoção.
             */
            setProdutoRemovendo(
                produto.id
            );


            setTimeout(() => {

                setProdutos(
                    (produtosAtuais) =>
                        produtosAtuais.filter(
                            (item) =>
                                item.id !== produto.id
                        )
                );

                setProdutoRemovendo(null);

            }, 350);


            setMensagem(
                "Produto ativado com sucesso."
            );


            /*
             * Recarrega a página.
             */
            setTimeout(async () => {

                try {

                    const response =
                        await listarProdutos(
                            pagina,
                            10,
                            pesquisa,
                            filtros
                        );


                    setProdutos(
                        response.content || []
                    );

                    setTotalProdutos(
                        response.page?.totalElements || 0
                    );

                    setTotalPaginas(
                        response.page?.totalPages || 0
                    );

                } catch (error) {

                    console.error(
                        "Erro ao atualizar produtos:",
                        error
                    );

                }

            }, 400);


        } catch (error) {

            console.error(
                "Erro ao ativar produto:",
                error
            );

            setErro(
                error?.response?.data?.message ||
                "Não foi possível ativar o produto."
            );

        }

    }


    /*
     * =========================================================
     * ATUALIZAR PRODUTO
     * =========================================================
     */

    async function handleAtualizarProduto(event) {

        event.preventDefault();


        if (!produtoEditando) {
            return;
        }


        setErro("");

        setMensagem("");


        const formData =
            new FormData(event.target);


        const produto = {

            nome:
                formData.get("nome"),

            precoCompra:
                Number(
                    formData.get("precoCompra")
                ),

            precoVenda:
                Number(
                    formData.get("precoVenda")
                ),

            categoriaId:
                Number(
                    formData.get("categoriaId")
                ),

        };


        try {

            const produtoAtualizado =
                await atualizarProduto(
                    produtoEditando.id,
                    produto
                );


            /*
             * Atualiza somente a linha.
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


            setProdutoEditando(null);

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
                error?.response?.data?.message ||
                "Não foi possível atualizar o produto."
            );

        }

    }


    /*
     * =========================================================
     * CANCELAR EDIÇÃO
     * =========================================================
     */

    function cancelarEdicao() {

        setProdutoEditando(null);

        setFormularioAberto(false);

        setErro("");

        setMensagem("");

    }


    /*
     * =========================================================
     * PAGINAÇÃO
     * =========================================================
     */

    function paginaAnterior() {

        if (pagina > 0) {

            setPagina(
                (paginaAtual) =>
                    paginaAtual - 1
            );

        }

    }


    function proximaPagina() {

        if (
            pagina < totalPaginas - 1
        ) {

            setPagina(
                (paginaAtual) =>
                    paginaAtual + 1
            );

        }

    }


    /*
     * =========================================================
     * ESTADO INICIAL
     * =========================================================
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


    /*
     * =========================================================
     * ERRO
     * =========================================================
     */

    if (erro) {

        return (
            <p>
                {erro}
            </p>
        );

    }


    return (

        <div className="produtos-page">


            {/* =================================================
                CABEÇALHO
            ================================================= */}

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


            {/* =================================================
                MENSAGEM
            ================================================= */}

            {mensagem && (

                <div className="alert-success">

                    {mensagem}

                </div>

            )}


            {/* =================================================
                CONTEÚDO PRINCIPAL
            ================================================= */}

            <div className="produtos-content">


                {/* =================================================
                    FORMULÁRIO
                ================================================= */}

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
                        onCancelar={
                            cancelarEdicao
                        }
                    />

                </section>


                {/* =================================================
                    LISTAGEM
                ================================================= */}

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

                            {totalProdutos}

                        </span>

                    </div>


                    {/* =================================================
                        FILTROS
                    ================================================= */}

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
                                            categoriaId: "",
                                        })
                                    );

                                    setPagina(0);

                                }}
                                placeholder="Pesquisar categoria..."
                            />


                            {categoriasFiltro.length > 0 && (

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

                                                    setCategoriasFiltro([]);

                                                    setPagina(0);

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


                    {/* =================================================
                        LISTA
                    ================================================= */}

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
                            produtos={produtos}
                            onEditar={
                                handleEditarProduto
                            }
                            onDesativar={
                                handleDesativarProduto
                            }
                            onAtivar={
                                handleAtivarProduto
                            }
                            produtoAnimando={
                                produtoAnimando
                            }
                            produtoRemovendo={
                                produtoRemovendo
                            }
                        />

                    )}


                    {/* =================================================
                        PAGINAÇÃO
                    ================================================= */}

                    {totalPaginas > 0 && (

                        <div className="produto-paginacao">

                            <button
                                type="button"
                                onClick={
                                    paginaAnterior
                                }
                                disabled={
                                    pagina === 0 ||
                                    carregando
                                }
                            >
                                ← Anterior
                            </button>


                            <span>

                                Página{" "}

                                <strong>
                                    {pagina + 1}
                                </strong>

                                {" "}de{" "}

                                <strong>
                                    {totalPaginas}
                                </strong>

                            </span>


                            <button
                                type="button"
                                onClick={
                                    proximaPagina
                                }
                                disabled={
                                    pagina >=
                                    totalPaginas - 1 ||
                                    carregando
                                }
                            >
                                Próxima →
                            </button>

                        </div>

                    )}


                </section>

            </div>

        </div>

    );
}


export default Produtos;

