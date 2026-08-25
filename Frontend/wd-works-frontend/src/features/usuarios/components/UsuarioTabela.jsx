import { useState } from "react";
import { atualizarUsuario } from "../services/usuarioService";

function UsuarioTabela({
    usuarios,
    onAtivar,
    onDesativar,
    onUsuarioAtualizado
}) {

    const [usuarioEditando, setUsuarioEditando] = useState(null);

    const [nome, setNome] = useState("");
    const [email, setEmail] = useState("");
    const [perfil, setPerfil] = useState("");

    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");

    function iniciarEdicao(usuario) {

        setUsuarioEditando(usuario);

        setNome(usuario.nome);
        setEmail(usuario.email);
        setPerfil(usuario.perfil);

        setErro("");
    }

    function cancelarEdicao() {

        if (salvando) {
            return;
        }

        setUsuarioEditando(null);
        setErro("");
    }

    // restante do componente...

    async function salvarEdicao() {

        if (!usuarioEditando) {
            return;
        }

        setErro("");
        setSalvando(true);

        try {

            const usuarioAtualizado = await atualizarUsuario(
                usuarioEditando.id,
                {
                    nome,
                    email,
                    perfil
                }
            );

            onUsuarioAtualizado(usuarioAtualizado);

            setUsuarioEditando(null);

        } catch (error) {

            console.error(error);

            setErro(
                "Não foi possível atualizar o usuário."
            );

        } finally {

            setSalvando(false);

        }
    }

    function obterClassePerfil(perfil) {

        switch (perfil) {

            case "ADMIN":
                return "role-badge role-admin";

            case "GERENTE":
                return "role-badge role-gerente";

            case "FUNCIONARIO":
                return "role-badge role-funcionario";

            default:
                return "role-badge";
        }
    }

    function formatarPerfil(perfil) {

        switch (perfil) {

            case "ADMIN":
                return "Administrador";

            case "GERENTE":
                return "Gerente";

            case "FUNCIONARIO":
                return "Funcionário";

            default:
                return perfil;
        }
    }

    return (
        <div className="table-container">

            <table className="data-table">

                <thead>

                    <tr>

                        <th>Nome</th>

                        <th>Email</th>

                        <th>Perfil</th>

                        <th>Status</th>

                        <th>Ações</th>

                    </tr>

                </thead>

                <tbody>

                    {usuarios.map((usuario) => {

                        const editando =
                            usuarioEditando?.id === usuario.id;

                        return (

                            <tr
                                key={usuario.id}
                                className={
                                    editando
                                        ? "usuario-row usuario-row-editando"
                                        : "usuario-row"
                                }
                            >

                                {editando ? (

                                    <>
                                        <td colSpan="5">

                                            <div className="usuario-inline-edit">

                                                <div className="inline-edit-fields">

                                                    <div className="inline-field">

                                                        <label>
                                                            Nome
                                                        </label>

                                                        <input
                                                            type="text"
                                                            value={nome}
                                                            onChange={(event) =>
                                                                setNome(
                                                                    event.target.value
                                                                )
                                                            }
                                                            autoFocus
                                                        />

                                                    </div>

                                                    <div className="inline-field">

                                                        <label>
                                                            Email
                                                        </label>

                                                        <input
                                                            type="email"
                                                            value={email}
                                                            onChange={(event) =>
                                                                setEmail(
                                                                    event.target.value
                                                                )
                                                            }
                                                        />

                                                    </div>

                                                    <div className="inline-field">

                                                        <label>
                                                            Perfil
                                                        </label>

                                                        <select
                                                            value={perfil}
                                                            onChange={(event) =>
                                                                setPerfil(
                                                                    event.target.value
                                                                )
                                                            }
                                                        >

                                                            <option value="FUNCIONARIO">
                                                                Funcionário
                                                            </option>

                                                            <option value="GERENTE">
                                                                Gerente
                                                            </option>

                                                            <option value="ADMIN">
                                                                Administrador
                                                            </option>

                                                        </select>

                                                    </div>

                                                    <div className="inline-role-preview">

                                                        <span
                                                            className={`${obterClassePerfil(
                                                                perfil
                                                            )} role-badge-morph`}
                                                            key={perfil}
                                                        >
                                                            {formatarPerfil(perfil)}
                                                        </span>

                                                    </div>

                                                </div>

                                                {erro && (

                                                    <div className="inline-edit-error">
                                                        {erro}
                                                    </div>

                                                )}

                                                <div className="inline-edit-actions">

                                                    <button
                                                        type="button"
                                                        className="button button-secondary"
                                                        onClick={cancelarEdicao}
                                                        disabled={salvando}
                                                    >
                                                        Cancelar
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="button button-primary"
                                                        onClick={salvarEdicao}
                                                        disabled={salvando}
                                                    >
                                                        {salvando
                                                            ? "Salvando..."
                                                            : "Salvar alterações"
                                                        }
                                                    </button>

                                                </div>

                                            </div>

                                        </td>
                                    </>

                                ) : (

                                    <>

                                        <td>
                                            <strong>
                                                {usuario.nome}
                                            </strong>
                                        </td>

                                        <td>
                                            {usuario.email}
                                        </td>

                                        <td>

                                            <span
                                                className={obterClassePerfil(
                                                    usuario.perfil
                                                )}
                                            >
                                                {formatarPerfil(
                                                    usuario.perfil
                                                )}
                                            </span>

                                        </td>

                                        <td>

                                            {usuario.status === "ATIVO" ? (

                                                <span className="status-badge status-ativo">
                                                    Ativo
                                                </span>

                                            ) : (

                                                <span className="status-badge status-inativo">
                                                    {usuario.status}
                                                </span>

                                            )}

                                        </td>

                                        <td>

                                            <div className="usuario-actions">

                                                <button
                                                    type="button"
                                                    className="button button-primary"
                                                    onClick={() =>
                                                        iniciarEdicao(usuario)
                                                    }
                                                >
                                                    Editar
                                                </button>

                                                {usuario.status === "ATIVO" ? (

                                                    <button
                                                        type="button"
                                                        className="button button-secondary"
                                                        onClick={() =>
                                                            onDesativar(
                                                                usuario.id
                                                            )
                                                        }
                                                    >
                                                        Desativar
                                                    </button>

                                                ) : (

                                                    <button
                                                        type="button"
                                                        className="button button-primary"
                                                        onClick={() =>
                                                            onAtivar(
                                                                usuario.id
                                                            )
                                                        }
                                                    >
                                                        Ativar
                                                    </button>

                                                )}

                                            </div>

                                        </td>

                                    </>

                                )}

                            </tr>

                        );

                    })}

                </tbody>

            </table>

        </div>
    );
}

export default UsuarioTabela;