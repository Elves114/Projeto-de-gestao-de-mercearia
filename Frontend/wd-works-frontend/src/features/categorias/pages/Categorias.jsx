import {
    useEffect,
    useRef,
    useState
} from "react";

import {
    listarCategorias,
    atualizarCategoria,
    eliminarCategoria,
} from "../services/categoriaService";

import CategoriaForm from "../components/CategoriaForm";

import "../style/Categoria.css";


function Categorias() {

    /*
     * ============================================================
     * ESTADO DAS CATEGORIAS
     * ============================================================
     */

    const [estado, setEstado] = useState({
        carregando: true,
        categorias: [],
        erro: "",
    });


    /*
     * ============================================================
     * PESQUISA
     * ============================================================
     */

    const [pesquisa, setPesquisa] = useState("");

    const [pesquisaAplicada, setPesquisaAplicada] =
        useState("");


    /*
     * ============================================================
     * EDIÇÃO
     * ============================================================
     */

    const [editandoId, setEditandoId] =
        useState(null);

    const [nomeEditado, setNomeEditado] =
        useState("");


    /*
     * ============================================================
     * CARD EXPANDIDO
     *
     * Usaremos este estado futuramente para as
     * subcategorias.
     * ============================================================
     */

    const [categoriaExpandida, setCategoriaExpandida] =
        useState(null);


    /*
     * ============================================================
     * CONTADOR ANIMADO
     *
     * Guarda os valores que estão sendo mostrados
     * visualmente nos cards.
     * ============================================================
     */

    const [contadores, setContadores] =
        useState({});
    const contadoresRef =
        useRef({});
    /*
     * ============================================================
     * CARREGAR CATEGORIAS
     * ============================================================
     */

    async function carregarCategorias(nome = "") {

        try {

            setEstado((atual) => ({
                ...atual,
                carregando: true,
                erro: "",
            }));


            const response =
                await listarCategorias(
                    0,
                    10,
                    nome
                );


            setEstado({
                carregando: false,
                categorias:
                    response.content || [],
                erro: "",
            });


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


    /*
     * ============================================================
     * CARREGAMENTO INICIAL
     * ============================================================
     */

    useEffect(() => {

        let ativo = true;


        async function carregar() {

            try {

                const response =
                    await listarCategorias();


                if (!ativo) {
                    return;
                }


                setEstado({
                    carregando: false,
                    categorias:
                        response.content || [],
                    erro: "",
                });


            } catch (error) {

                console.error(
                    "Erro ao carregar categorias:",
                    error
                );


                if (!ativo) {
                    return;
                }


                setEstado({
                    carregando: false,
                    categorias: [],
                    erro:
                        "Não foi possível carregar as categorias.",
                });

            }

        }


        carregar();


        return () => {

            ativo = false;

        };

    }, []);


    /*
     * ============================================================
     * ANIMAÇÃO DOS CONTADORES
     * ============================================================
     *
     * Neste momento usamos:
     *
     * categoria.quantidadeProdutos
     *
     * Caso o backend ainda não envie esse campo,
     * o valor será 0.
     * ============================================================
     */

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


    /*
     * ============================================================
     * PESQUISAR
     * ============================================================
     */

    function pesquisar(event) {

        event.preventDefault();


        const nome =
            pesquisa.trim();


        setPesquisaAplicada(
            nome
        );


        carregarCategorias(
            nome
        );

    }


    /*
     * ============================================================
     * LIMPAR PESQUISA
     * ============================================================
     */

    function limparPesquisa() {

        setPesquisa("");

        setPesquisaAplicada("");


        carregarCategorias("");

    }


    /*
     * ============================================================
     * CRIAÇÃO DE CATEGORIA
     * ============================================================
     */

    function handleCategoriaCriada(
        categoria
    ) {

        setEstado((atual) => ({

            ...atual,

            categorias: [
                categoria,
                ...atual.categorias,
            ],

        }));


        /*
         * Começa o contador da nova categoria
         * em 0.
         */

        setContadores((atual) => ({

            ...atual,

            [categoria.id]: 0,

        }));

    }


    /*
     * ============================================================
     * INICIAR EDIÇÃO
     * ============================================================
     */

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


    /*
     * ============================================================
     * CANCELAR EDIÇÃO
     * ============================================================
     */

    function cancelarEdicao() {

        setEditandoId(null);

        setNomeEditado("");

    }


    /*
     * ============================================================
     * GUARDAR EDIÇÃO
     * ============================================================
     */

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
                                ? categoriaAtualizada
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
                "Não foi possível atualizar a categoria."
            );

        }

    }


    /*
     * ============================================================
     * ELIMINAR
     * ============================================================
     */

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


            setEstado((atual) => ({

                ...atual,

                categorias:
                    atual.categorias.filter(
                        (categoria) =>
                            categoria.id !== id
                    ),

            }));


            /*
             * Remove também o contador.
             */

            setContadores((atual) => {

                const novosContadores = {
                    ...atual,
                };


                delete novosContadores[id];


                return novosContadores;

            });


            /*
             * Se a categoria eliminada estava
             * expandida, fechamos o card.
             */

            if (
                categoriaExpandida === id
            ) {

                setCategoriaExpandida(
                    null
                );

            }


        } catch (error) {

            console.error(
                "Erro ao eliminar categoria:",
                error
            );


            alert(
                "Não foi possível eliminar a categoria."
            );

        }

    }


    /*
     * ============================================================
     * EXPANDIR / FECHAR CATEGORIA
     * ============================================================
     */

    function alternarCategoria(
        id
    ) {

        setCategoriaExpandida(
            (atual) =>
                atual === id
                    ? null
                    : id
        );

    }


    /*
     * ============================================================
     * ESTADO DE CARREGAMENTO
     * ============================================================
     */

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


    /*
     * ============================================================
     * ERRO
     * ============================================================
     */

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


    /*
     * ============================================================
     * RENDER
     * ============================================================
     */

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

                        {
                            estado.categorias.length
                        }

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

                                const expandida =
                                    categoriaExpandida ===
                                    categoria.id;


                                const subcategorias =
                                    Array.isArray(
                                        categoria.subcategorias
                                    )
                                        ? categoria.subcategorias
                                        : [];


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
                                        className={`
                                            categoria-card
                                            ${expandida
                                                ? "categoria-card-expandida"
                                                : ""
                                            }
                                        `}
                                        style={{
                                            "--stagger-delay":
                                                `${index * 30}ms`,
                                        }}
                                    >


                                        {/* ==================================================
                                            PARTE PRINCIPAL
                                            ================================================== */}

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


                                                        <button
                                                            type="button"
                                                            className="categoria-action categoria-action-expand"
                                                            onClick={() =>
                                                                alternarCategoria(
                                                                    categoria.id
                                                                )
                                                            }
                                                            aria-expanded={
                                                                expandida
                                                            }
                                                        >

                                                            {expandida
                                                                ? "Fechar"
                                                                : "Abrir"}

                                                        </button>

                                                    </>

                                                )}

                                            </div>

                                        </div>


                                        {/* ==================================================
                                            SUBCATEGORIAS
                                            ================================================== */}

                                        <div
                                            className={`
                                                categoria-subcategorias-wrapper
                                                ${expandida
                                                    ? "aberta"
                                                    : ""
                                                }
                                            `}
                                        >

                                            <div className="categoria-subcategorias">

                                                {subcategorias.length >
                                                    0 ? (

                                                    <div className="subcategorias-lista">

                                                        {subcategorias.map(
                                                            (
                                                                subcategoria
                                                            ) => (

                                                                <div
                                                                    key={
                                                                        subcategoria.id
                                                                    }
                                                                    className="subcategoria-item"
                                                                >

                                                                    <span>

                                                                        {
                                                                            subcategoria.nome
                                                                        }

                                                                    </span>

                                                                    <span>

                                                                        {
                                                                            subcategoria.quantidadeProdutos ??
                                                                            0
                                                                        }{" "}
                                                                        produtos

                                                                    </span>

                                                                </div>

                                                            )
                                                        )}

                                                    </div>

                                                ) : (

                                                    <div className="subcategorias-vazio">

                                                        <span>
                                                            Esta categoria não possui subcategorias.
                                                        </span>

                                                    </div>

                                                )}

                                            </div>

                                        </div>


                                    </article>

                                );

                            }
                        )}

                    </div>

                )}

            </section>

        </div>

    );

}


export default Categorias;