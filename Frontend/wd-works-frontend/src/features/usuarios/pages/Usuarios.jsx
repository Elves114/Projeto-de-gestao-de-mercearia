
import { useEffect, useState } from "react";

import {
    listarUsuarios,
    pesquisarUsuarios,
    ativarUsuario,
    desativarUsuario
} from "../services/usuarioService";

import UsuarioTabela from "../components/UsuarioTabela";
import {
    obterMensagemErro
} from "../../../services/api";

import { useNavigate } from "react-router-dom";
import "../style/Usuario.css";


function Usuarios() {

    const [usuarios, setUsuarios] = useState([]);

    const [termo, setTermo] = useState("");

    const [paginaAtual, setPaginaAtual] = useState(0);

    const [totalPaginas, setTotalPaginas] = useState(0);

    const [carregando, setCarregando] = useState(true);

    const [pesquisando, setPesquisando] = useState(false);

    const navigate = useNavigate();

    const [erro, setErro] = useState(null);


    async function carregarUsuarios(pagina = 0) {

        setErro(null);

        try {

            const data =
                await listarUsuarios(pagina);

            setUsuarios(data.content);

            setPaginaAtual(data.number);

            setTotalPaginas(data.totalPages);

        } catch (error) {

            console.error(error);

            setErro(
                obterMensagemErro(error)
            );
        } finally {

            setCarregando(false);

        }
    }


    async function executarPesquisa(
        termoPesquisa,
        pagina = 0
    ) {

        setErro(null);

        try {

            const data =
                await pesquisarUsuarios(
                    termoPesquisa,
                    pagina
                );

            setUsuarios(data.content);

            setPaginaAtual(data.number);

            setTotalPaginas(data.totalPages);

        } catch (error) {

            console.error(error);

            setErro(
                "Não foi possível realizar a pesquisa."
            );

        }
    }


    useEffect(() => {

        async function carregarInicialmente() {

            try {

                const data =
                    await listarUsuarios(0);

                setUsuarios(data.content);
                setPaginaAtual(data.number);
                setTotalPaginas(data.totalPages);

            } catch (error) {

                console.error(error);

                setErro(
                    "Não foi possível carregar os usuários."
                );

            } finally {

                setCarregando(false);

            }
        }

        carregarInicialmente();

    }, []);


    async function handlePesquisar(event) {

        event.preventDefault();

        const termoPesquisa =
            termo.trim();

        if (!termoPesquisa) {

            setTermo("");

            await carregarUsuarios(0);

            return;
        }

        setPesquisando(true);

        try {

            await executarPesquisa(
                termoPesquisa,
                0
            );

        } finally {

            setPesquisando(false);

        }
    }


    async function handleLimparPesquisa() {

        setTermo("");

        setErro(null);

        setCarregando(true);

        await carregarUsuarios(0);
    }


    async function handlePagina(pagina) {

        setCarregando(true);

        if (termo.trim()) {

            await executarPesquisa(
                termo.trim(),
                pagina
            );

        } else {

            await carregarUsuarios(pagina);

        }

        setCarregando(false);
    }

    function handleUsuarioAtualizado(usuarioAtualizado) {

        setUsuarios((usuariosAtuais) =>
            usuariosAtuais.map((usuario) =>
                usuario.id === usuarioAtualizado.id
                    ? usuarioAtualizado
                    : usuario
            )
        );
    }

    async function handleAtivar(id) {

        try {

            const usuarioAtualizado =
                await ativarUsuario(id);

            setUsuarios((usuariosAtuais) =>
                usuariosAtuais.map((usuario) =>
                    usuario.id === id
                        ? usuarioAtualizado
                        : usuario
                )
            );

        } catch (error) {

            console.error(error);

            setErro(
                "Não foi possível ativar o usuário."
            );
        }
    }


    async function handleDesativar(id) {

        try {

            const usuarioAtualizado =
                await desativarUsuario(id);

            setUsuarios((usuariosAtuais) =>
                usuariosAtuais.map((usuario) =>
                    usuario.id === id
                        ? usuarioAtualizado
                        : usuario
                )
            );

        } catch (error) {

            console.error(error);

            setErro(
                "Não foi possível desativar o usuário."
            );
        }
    }


    if (carregando) {

        return (
            <p>
                Carregando usuários...
            </p>
        );
    }


    return (
        <div className="usuarios-page">


            <div className="page-header">

                <div>

                    <h1>
                        Usuários
                    </h1>

                    <p>
                        Gerencie os usuários da sua empresa.
                    </p>

                </div>


                <button
                    type="button"
                    className="button-primary"
                    onClick={() => navigate("/usuarios/novo")}
                >
                    + Criar usuário
                </button>

            </div>



            <form
                className="usuario-search"
                onSubmit={handlePesquisar}
            >

                <div className="usuario-search-input-wrapper">

                    <span className="usuario-search-icon">

                    </span>

                    <input
                        type="text"
                        value={termo}
                        onChange={(event) =>
                            setTermo(event.target.value)
                        }
                        placeholder="Pesquisar por nome ou email..."
                        aria-label="Pesquisar usuários"
                    />

                    {termo && !pesquisando && (

                        <button
                            type="button"
                            className="usuario-search-clear"
                            onClick={handleLimparPesquisa}
                            aria-label="Limpar pesquisa"
                        >
                            ×
                        </button>

                    )}

                </div>


                <button
                    type="submit"
                    className="usuario-search-button"
                    disabled={pesquisando}
                >

                    {pesquisando ? (

                        <>
                            <span className="search-spinner"></span>
                            Pesquisando...
                        </>

                    ) : (

                        <>
                            Pesquisar
                        </>

                    )}

                </button>

            </form>
            {termo.trim() && !pesquisando && (
                <div className="usuario-search-info">
                    <span className="search-active-dot"></span>

                    Pesquisando por
                    <strong>“{termo.trim()}”</strong>
                </div>
            )}


            {erro && (

                <div className="message message-error">
                    {erro}
                </div>

            )}


            {usuarios.length === 0 ? (

                <div className="empty-state">

                    <h2>
                        Nenhum usuário encontrado
                    </h2>

                    <p>
                        {termo
                            ? "Não encontramos nenhum usuário com esse nome ou email."
                            : "Ainda não existem usuários para apresentar."
                        }
                    </p>

                </div>

            ) : (

                <>

                    <UsuarioTabela
                        usuarios={usuarios}
                        onAtivar={handleAtivar}
                        onDesativar={handleDesativar}
                        onUsuarioAtualizado={handleUsuarioAtualizado}
                    />


                    {totalPaginas > 1 && (

                        <div className="pagination">

                            <button
                                type="button"
                                className="button-secondary"
                                disabled={paginaAtual === 0}
                                onClick={() =>
                                    handlePagina(
                                        paginaAtual - 1
                                    )
                                }
                            >
                                Anterior
                            </button>


                            <span>
                                Página {paginaAtual + 1} de {totalPaginas}
                            </span>


                            <button
                                type="button"
                                className="button-secondary"
                                disabled={
                                    paginaAtual >=
                                    totalPaginas - 1
                                }
                                onClick={() =>
                                    handlePagina(
                                        paginaAtual + 1
                                    )
                                }
                            >
                                Próxima
                            </button>

                        </div>

                    )}

                </>

            )}

        </div>
    );
}


export default Usuarios;
