import { useState } from "react";
import { Pencil, X, Check } from "lucide-react";

import { useAuth } from "../../../contexts/useAuth";
import { atualizarUsuario } from "../../usuarios/services/usuarioService";
import { obterMensagemErro } from "../../../services/api";


function PerfilFormulario({ usuario }) {

    const { atualizarUsuarioLocal } = useAuth();

    const [editando, setEditando] = useState(false);

    /*
     * Estado dos campos de edição.
     *
     * Não precisa de useEffect para sincronizar com "usuario":
     * na visualização lemos directamente de "usuario",
     * no formulário preenchemos ao entrar em modo edição.
     */
    const [nome, setNome] = useState("");
    const [email, setEmail] = useState("");

    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");
    const [sucesso, setSucesso] = useState("");


    const podeEditar =
        usuario?.perfil === "ADMIN" ||
        usuario?.perfil === "GERENTE";


    /* ============================================================
       ENTRAR EM EDIÇÃO
       ============================================================ */

    function iniciarEdicao() {

        setNome(usuario?.nome || "");
        setEmail(usuario?.email || "");

        setErro("");
        setSucesso("");
        setEditando(true);
    }


    /* ============================================================
       CANCELAR
       ============================================================ */

    function cancelar() {

        if (salvando) {
            return;
        }

        setEditando(false);
        setErro("");
        setSucesso("");
    }


    /* ============================================================
       GUARDAR
       ============================================================ */

    async function guardar(event) {

        event.preventDefault();

        setErro("");
        setSucesso("");
        setSalvando(true);

        try {

            const atualizado = await atualizarUsuario(
                usuario.id,
                { nome, email }
            );

            atualizarUsuarioLocal({
                nome: atualizado.nome,
                email: atualizado.email,
            });

            setSucesso("Dados atualizados com sucesso.");
            setEditando(false);

        } catch (error) {

            console.error(error);

            setErro(obterMensagemErro(error));

        } finally {

            setSalvando(false);

        }
    }


    return (

        <section className="perfil-painel">

            <header className="perfil-painel-header">

                <div>
                    <h3>Informações pessoais</h3>
                    <p>Nome e email associados à sua conta.</p>
                </div>


                {podeEditar && !editando && (
                    <button
                        type="button"
                        className="perfil-btn perfil-btn-secundario"
                        onClick={iniciarEdicao}
                    >
                        <Pencil size={14} strokeWidth={2.4} />
                        Editar
                    </button>
                )}

            </header>


            {!editando ? (

                <div className="perfil-campos">

                    <div className="perfil-campo">
                        <span>Nome</span>
                        <strong>{usuario?.nome || "—"}</strong>
                    </div>

                    <div className="perfil-campo">
                        <span>Email</span>
                        <strong>{usuario?.email || "—"}</strong>
                    </div>

                </div>

            ) : (

                <form
                    className="perfil-formulario"
                    onSubmit={guardar}
                >

                    <div className="perfil-campo-editavel">
                        <label htmlFor="perfil-nome">Nome</label>
                        <input
                            id="perfil-nome"
                            type="text"
                            value={nome}
                            onChange={(e) =>
                                setNome(e.target.value)
                            }
                            autoFocus
                            required
                        />
                    </div>

                    <div className="perfil-campo-editavel">
                        <label htmlFor="perfil-email">Email</label>
                        <input
                            id="perfil-email"
                            type="email"
                            value={email}
                            onChange={(e) =>
                                setEmail(e.target.value)
                            }
                            required
                        />
                    </div>

                    {erro && (
                        <div className="perfil-aviso perfil-aviso-erro">
                            {erro}
                        </div>
                    )}

                    <div className="perfil-formulario-acoes">

                        <button
                            type="button"
                            className="perfil-btn perfil-btn-secundario"
                            onClick={cancelar}
                            disabled={salvando}
                        >
                            <X size={14} strokeWidth={2.4} />
                            Cancelar
                        </button>

                        <button
                            type="submit"
                            className="perfil-btn perfil-btn-primario"
                            disabled={salvando}
                        >
                            <Check size={14} strokeWidth={2.4} />
                            {salvando
                                ? "A guardar..."
                                : "Guardar"}
                        </button>

                    </div>

                </form>

            )}


            {!podeEditar && (
                <p className="perfil-nota">
                    Para alterar o seu nome ou email,
                    contacte um administrador da empresa.
                </p>
            )}


            {sucesso && (
                <div className="perfil-aviso perfil-aviso-sucesso">
                    {sucesso}
                </div>
            )}

        </section>
    );
}


export default PerfilFormulario;