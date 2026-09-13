import {
    useEffect,
    useRef,
    useState
} from "react";

import {
    listarCategorias,
    atualizarCategoria,
    eliminarCategoria,
    contarProdutosPorCategoria,
} from "../services/categoriaService";

import {
    obterMensagemErro
} from "../../../services/api";

import CategoriaForm from "../components/CategoriaForm";

import "../style/Categoria.css";


/* ============================================================
   NÚMERO DE CATEGORIAS POR PÁGINA
   ============================================================ */

const TAMANHO_PAGINA = 10;


function Categorias() {

    /* ============================================================
       ESTADO
       ============================================================ */

    const [estado, setEstado] = useState({
        carregando: true,
        categorias: [],
        erro: "",
    });

    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);
    const [totalElementos, setTotalElementos] = useState(0);

    const [pesquisa, setPesquisa] = useState("");
    const [pesquisaAplicada, setPesquisaAplicada] = useState("");

    const [editandoId, setEditandoId] = useState(null);
    const [nomeEditado, setNomeEditado] = useState("");

    const [contadores, setContadores] = useState({});
    const contadoresRef = useRef({});


    /* ============================================================
       CARREGAR CATEGORIAS + CONTAGENS

       NOTA IMPORTANTE:
       Esta função NÃO escreve estado antes do primeiro await.
       Todo o setState acontece depois da fronteira assíncrona,
       evitando o aviso do React Compiler sobre setState
       síncrono dentro de um efeito.
       ============================================================ */

    async function carregarCategorias(
        nome,
        paginaAlvo
    ) {

        try {

            /* -------- 1) Lista de categorias -------- */

            const response =
                await listarCategorias(
                    paginaAlvo,
                    TAMANHO_PAGINA,
                    nome
                );

            const categorias =
                response.content || [];


            /* -------- 2) Contagens em paralelo -------- */

            const contagens =
                await Promise.all(
                    categorias.map(async (categoria) => {

                        try {

                            const total =
                                await contarProdutosPorCategoria(
                                    categoria.id
                                );

                            return {
                                id: categoria.id,
                                total,
                            };

                        } catch (error) {

                            console.error(
                                "Erro ao contar produtos da categoria " +
                                categoria.id,
                                error
                            );

                            return {
                                id: categoria.id,
                                total: 0,
                            };
                        }
                    })
                );


            /* -------- 3) Composto final -------- */

            const mapaContagens =
                Object.fromEntries(
                    contagens.map((c) => [
                        c.id,
                        c.total,
                    ])
                );


            const categoriasComContagem =
                categorias.map((categoria) => ({
                    ...categoria,
                    quantidadeProdutos:
                        mapaContagens[categoria.id] ?? 0,
                }));


            /* -------- 4) setState (após awaits) -------- */

            setEstado({
                carregando: false,
                categorias: categoriasComContagem,
                erro: "",
            });

            setTotalPaginas(
                response.page?.totalPages || 0
            );

            setTotalElementos(
                response.page?.totalElements || 0
            );


        } catch (error) {

            console.error(
                "Erro ao carregar categorias:",
                error
            );

            setEstado({
                carregando: false,
                categorias: [],
                erro:
                    "Não foi possível carregar as categorias.",
            });

        }
    }


    /* ============================================================
       CARREGAMENTO INICIAL E POR MUDANÇA DE FILTRO / PÁGINA
       ============================================================ */

    useEffect(() => {

        let ativo = true;

        async function carregar() {
            if (!ativo) {
                return;
            }

            await carregarCategorias(
                pesquisaAplicada,
                pagina
            );
        }

        carregar();

        return () => {
            ativo = false;
        };
    }, [pesquisaAplicada, pagina]);


    /* ============================================================
       ANIMAÇÃO DOS CONTADORES
       ============================================================ */

    useEffect(() => {

        const categorias =
            estado.categorias;


        if (!categorias.length) {
            return;
        }


        categorias.forEach((categoria) => {

            const valorFinal =
                Number(
                    categoria.quantidadeProdutos ?? 0
                );


            const inicio =
                Number(
                    contadoresRef.current[
                    categoria.id
                    ] ?? 0
                );


            if (inicio === valorFinal) {
                return;
            }


            const duracao = 300;

            const inicioTempo =
                performance.now();


            function animar(tempoAtual) {

                const progresso =
                    Math.min(
                        (tempoAtual - inicioTempo) /
                        duracao,
                        1
                    );


                const progressoSuave =
                    1 -
                    Math.pow(
                        1 - progresso,
                        3
                    );


                const valorAtual =
                    Math.round(
                        inicio +
                        (valorFinal - inicio) *
                        progressoSuave
                    );


                contadoresRef.current[
                    categoria.id
                ] = valorAtual;


                setContadores(
                    (atual) => ({
                        ...atual,
                        [categoria.id]:
                            valorAtual,
                    })
                );


                if (progresso < 1) {

                    requestAnimationFrame(
                        animar
                    );

                }

            }


            requestAnimationFrame(
                animar
            );

        });

    }, [estado.categorias]);


    /* ============================================================
       PESQUISAR

       O setEstado com "carregando: true" é feito AQUI,
       no handler do evento — onde é perfeitamente seguro.
       ============================================================ */

    function pesquisar(event) {

        event.preventDefault();

        setEstado((atual) => ({
            ...atual,
            carregando: true,
            erro: "",
        }));

        setPagina(0);

        setPesquisaAplicada(
            pesquisa.trim()
        );

    }


    /* ============================================================
       LIMPAR PESQUISA
       ============================================================ */

    function limparPesquisa() {

        setEstado((atual) => ({
            ...atual,
            carregando: true,
            erro: "",
        }));

        setPesquisa("");

        setPagina(0);

        setPesquisaAplicada("");

    }


    /* ============================================================
       CRIAÇÃO DE CATEGORIA
       ============================================================ */

    function handleCategoriaCriada(
        categoria
    ) {

        setEstado((atual) => ({

            ...atual,

            categorias: [
                {
                    ...categoria,
                    quantidadeProdutos: 0,
                },
                ...atual.categorias,
            ],

        }));


        setContadores((atual) => ({

            ...atual,

            [categoria.id]: 0,

        }));


        setTotalElementos(
            (total) => total + 1
        );

    }


    /* ============================================================
       INICIAR EDIÇÃO
       ============================================================ */

    function iniciarEdicao(
        categoria
    ) {

        setEditandoId(
            categoria.id
        );

        setNomeEditado(
            categoria.nome
        );

    }


    /* ============================================================
       CANCELAR EDIÇÃO
       ============================================================ */

    function cancelarEdicao() {

        setEditandoId(null);

        setNomeEditado("");

    }


    /* ============================================================
       GUARDAR EDIÇÃO
       ============================================================ */

    async function salvarEdicao(
        id
    ) {

        const nome =
            nomeEditado.trim();


        if (!nome) {
            return;
        }


        try {

            const categoriaAtualizada =
                await atualizarCategoria(
                    id,
                    nome
                );


            setEstado((atual) => ({

                ...atual,

                categorias:
                    atual.categorias.map(
                        (categoria) =>
                            categoria.id === id
                                ? {
                                    ...categoriaAtualizada,
                                    quantidadeProdutos:
                                        categoria.quantidadeProdutos ?? 0,
                                }
                                : categoria
                    ),

            }));


            cancelarEdicao();


        } catch (error) {

            console.error(
                "Erro ao atualizar categoria:",
                error
            );


            alert(
                obterMensagemErro(error)
            );
        }

    }


    /* ============================================================
       ELIMINAR
       ============================================================ */

    async function eliminar(
        id
    ) {

        const confirmar =
            window.confirm(
                "Tem certeza que deseja eliminar esta categoria?"
            );


        if (!confirmar) {
            return;
        }


        try {

            await eliminarCategoria(
                id
            );


            /*
             * Se a página actual ficar sem elementos,
             * retrocedemos uma página. Caso contrário,
             * recarregamos a página actual com o filtro.
             */

            const proximaPagina =
                estado.categorias.length === 1 &&
                    pagina > 0
                    ? pagina - 1
                    : pagina;


            setEstado((atual) => ({
                ...atual,
                carregando: true,
                erro: "",
            }));


            if (proximaPagina !== pagina) {

                setPagina(proximaPagina);

            } else {

                await carregarCategorias(
                    pesquisaAplicada,
                    proximaPagina
                );

            }


            /*
             * Remove o contador associado.
             */

            setContadores((atual) => {

                const novos = { ...atual };

                delete novos[id];

                return novos;
            });


            setTotalElementos(
                (total) => Math.max(0, total - 1)
            );


        } catch (error) {

            console.error(
                "Erro ao eliminar categoria:",
                error
            );


            alert(
                obterMensagemErro(error)
            );

        }

    }


    /* ============================================================
       PAGINAÇÃO

       O "carregando: true" também é definido aqui,
       no handler do clique — não no efeito.
       ============================================================ */

    function irParaPagina(numero) {

        if (
            numero >= 0 &&
            numero < totalPaginas &&
            numero !== pagina
        ) {

            setEstado((atual) => ({
                ...atual,
                carregando: true,
                erro: "",
            }));

            setPagina(numero);

        }

    }


    /* ============================================================
       ESTADO DE CARREGAMENTO
       ============================================================ */

    if (
        estado.carregando &&
        estado.categorias.length === 0
    ) {

        return (

            <div className="categorias-page">

                <div className="page-header">

                    <div>

                        <h1>
                            Categorias
                        </h1>

                        <p>
                            Gerencie as categorias
                            dos produtos da sua empresa.
                        </p>

                    </div>

                </div>


                <div className="categorias-loading">

                    <div className="categoria-loading-card" />

                    <div className="categoria-loading-card" />

                    <div className="categoria-loading-card" />

                </div>

            </div>

        );

    }


    /* ============================================================
       ERRO
       ============================================================ */

    if (estado.erro) {

        return (

            <div className="categorias-page">

                <div className="page-header">

                    <div>

                        <h1>
                            Categorias
                        </h1>

                        <p>
                            Gerencie as categorias
                            dos produtos da sua empresa.
                        </p>

                    </div>

                </div>


                <div className="categoria-error">

                    {estado.erro}

                </div>

            </div>

        );

    }


    /* ============================================================
       RENDER
       ============================================================ */

    return (

        <div className="categorias-page">


            {/* ==================================================
                CABEÇALHO
                ================================================== */}

            <div className="page-header">

                <div>

                    <h1>
                        Categorias
                    </h1>

                    <p>
                        Gerencie as categorias
                        dos produtos da sua empresa.
                    </p>

                </div>

            </div>


            {/* ==================================================
                NOVA CATEGORIA
                ================================================== */}

            <section className="categoria-form-card">

                <div className="section-header">

                    <div>

                        <h2>
                            Nova categoria
                        </h2>

                        <p>
                            Adicione uma nova categoria
                            para organizar os seus produtos.
                        </p>

                    </div>

                </div>


                <CategoriaForm
                    onCriada={
                        handleCategoriaCriada
                    }
                />

            </section>


            {/* ==================================================
                LISTA
                ================================================== */}

            <section className="categorias-list-card">


                {/* CABEÇALHO */}

                <div className="section-header">

                    <div>

                        <h2>
                            Categorias cadastradas
                        </h2>

                        <p>
                            Explore e gerencie as
                            categorias disponíveis.
                        </p>

                    </div>


                    <span className="categoria-count">

                        {totalElementos}

                    </span>

                </div>


                {/* ==================================================
                    PESQUISA
                    ================================================== */}

                <form
                    className="categoria-search"
                    onSubmit={pesquisar}
                >

                    <input
                        type="text"
                        placeholder="Pesquisar categoria..."
                        value={pesquisa}
                        onChange={(event) =>
                            setPesquisa(
                                event.target.value
                            )
                        }
                    />


                    <button
                        type="submit"
                        className="button-primary"
                    >
                        Pesquisar
                    </button>


                    {pesquisaAplicada && (

                        <button
                            type="button"
                            className="button-secondary"
                            onClick={
                                limparPesquisa
                            }
                        >
                            Limpar
                        </button>

                    )}

                </form>


                {/* ==================================================
                    RESULTADOS
                    ================================================== */}

                {estado.categorias.length === 0 ? (

                    <div className="empty-state">

                        <p>

                            {
                                pesquisaAplicada
                                    ? "Nenhuma categoria encontrada para essa pesquisa."
                                    : "Nenhuma categoria encontrada."
                            }

                        </p>

                    </div>

                ) : (

                    <div className="categorias-grid">

                        {estado.categorias.map(
                            (
                                categoria,
                                index
                            ) => {

                                const quantidadeProdutos =
                                    Number(
                                        contadores[
                                        categoria.id
                                        ] ??
                                        categoria.quantidadeProdutos ??
                                        0
                                    );


                                return (

                                    <article
                                        key={
                                            categoria.id
                                        }
                                        className="categoria-card"
                                        style={{
                                            "--stagger-delay":
                                                `${index * 30}ms`,
                                        }}
                                    >

                                        <div className="categoria-card-main">


                                            <div className="categoria-card-top">

                                                <div className="categoria-icon">

                                                    {(
                                                        categoria.nome ||
                                                        "C"
                                                    )
                                                        .charAt(0)
                                                        .toUpperCase()}

                                                </div>


                                                <div className="categoria-card-title">

                                                    {editandoId ===
                                                        categoria.id ? (

                                                        <input
                                                            type="text"
                                                            value={
                                                                nomeEditado
                                                            }
                                                            onChange={(
                                                                event
                                                            ) =>
                                                                setNomeEditado(
                                                                    event
                                                                        .target
                                                                        .value
                                                                )
                                                            }
                                                            autoFocus
                                                            className="categoria-edit-input"
                                                        />

                                                    ) : (

                                                        <h3>

                                                            {
                                                                categoria.nome
                                                            }

                                                        </h3>

                                                    )}

                                                </div>

                                            </div>


                                            {/* ==================================================
                                                CONTADOR
                                                ================================================== */}

                                            <div className="categoria-products">

                                                <strong>

                                                    {
                                                        quantidadeProdutos
                                                    }

                                                </strong>


                                                <span>

                                                    {
                                                        quantidadeProdutos ===
                                                            1
                                                            ? "produto"
                                                            : "produtos"
                                                    }

                                                </span>

                                            </div>


                                            {/* ==================================================
                                                AÇÕES
                                                ================================================== */}

                                            <div className="categoria-card-actions">

                                                {editandoId ===
                                                    categoria.id ? (

                                                    <>

                                                        <button
                                                            type="button"
                                                            className="categoria-action categoria-action-save"
                                                            onClick={() =>
                                                                salvarEdicao(
                                                                    categoria.id
                                                                )
                                                            }
                                                        >
                                                            Guardar
                                                        </button>


                                                        <button
                                                            type="button"
                                                            className="categoria-action categoria-action-cancel"
                                                            onClick={
                                                                cancelarEdicao
                                                            }
                                                        >
                                                            Cancelar
                                                        </button>

                                                    </>

                                                ) : (

                                                    <>

                                                        <button
                                                            type="button"
                                                            className="categoria-action categoria-action-edit"
                                                            onClick={() =>
                                                                iniciarEdicao(
                                                                    categoria
                                                                )
                                                            }
                                                        >
                                                            Editar
                                                        </button>


                                                        <button
                                                            type="button"
                                                            className="categoria-action categoria-action-delete"
                                                            onClick={() =>
                                                                eliminar(
                                                                    categoria.id
                                                                )
                                                            }
                                                        >
                                                            Eliminar
                                                        </button>

                                                    </>

                                                )}

                                            </div>

                                        </div>

                                    </article>

                                );

                            }
                        )}

                    </div>

                )}


                {/* ==================================================
                    PAGINAÇÃO
                    ================================================== */}

                {totalPaginas > 1 && (

                    <div className="categoria-pagination">

                        <div className="categoria-pagination-info">

                            <span>
                                Página
                            </span>

                            <strong>
                                {pagina + 1}
                            </strong>

                            <span>
                                de {totalPaginas}
                            </span>

                        </div>


                        <div className="categoria-pagination-actions">

                            <button
                                type="button"
                                className="categoria-pagination-btn"
                                disabled={pagina === 0}
                                onClick={() =>
                                    irParaPagina(pagina - 1)
                                }
                            >
                                <span className="categoria-pagination-icon">
                                    ←
                                </span>

                                Anterior
                            </button>


                            <button
                                type="button"
                                className="categoria-pagination-btn"
                                disabled={
                                    pagina >= totalPaginas - 1
                                }
                                onClick={() =>
                                    irParaPagina(pagina + 1)
                                }
                            >
                                Próxima

                                <span className="categoria-pagination-icon">
                                    →
                                </span>
                            </button>

                        </div>

                    </div>

                )}

            </section>

        </div>

    );

}
export default Categorias;