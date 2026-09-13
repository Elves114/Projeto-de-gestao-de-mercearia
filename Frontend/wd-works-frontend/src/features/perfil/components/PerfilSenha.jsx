import { useState } from "react";
import { KeyRound, Eye, EyeOff } from "lucide-react";

import { alterarMinhaSenha } from "../services/perfilService";
import { obterMensagemErro } from "../../../services/api";


function PerfilSenha() {

    const [senhaAtual, setSenhaAtual] = useState("");
    const [novaSenha, setNovaSenha] = useState("");
    const [confirmar, setConfirmar] = useState("");

    const [mostrarAtual, setMostrarAtual] = useState(false);
    const [mostrarNova, setMostrarNova] = useState(false);

    const [salvando, setSalvando] = useState(false);
    const [erro, setErro] = useState("");
    const [sucesso, setSucesso] = useState("");


    function limpar() {

        setSenhaAtual("");
        setNovaSenha("");
        setConfirmar("");
    }


    async function guardar(event) {

        event.preventDefault();

        setErro("");
        setSucesso("");

        if (novaSenha.length < 8) {
            setErro(
                "A nova senha deve ter pelo menos 8 caracteres."
            );
            return;
        }

        if (novaSenha !== confirmar) {
            setErro("As senhas não coincidem.");
            return;
        }

        if (novaSenha === senhaAtual) {
            setErro(
                "A nova senha não pode ser igual à senha atual."
            );
            return;
        }

        setSalvando(true);

        try {

            await alterarMinhaSenha(senhaAtual, novaSenha);

            setSucesso(
                "Senha alterada com sucesso. Utilize a nova senha no próximo login."
            );

            limpar();

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
                    <h3>Alterar senha</h3>
                    <p>
                        Recomendamos uma senha com letras,
                        números e símbolos.
                    </p>
                </div>

            </header>


            <form
                className="perfil-formulario"
                onSubmit={guardar}
            >

                {/* SENHA ATUAL */}

                <div className="perfil-campo-editavel">

                    <label htmlFor="senha-atual">
                        Senha atual
                    </label>

                    <div className="perfil-campo-senha">

                        <input
                            id="senha-atual"
                            type={mostrarAtual ? "text" : "password"}
                            value={senhaAtual}
                            onChange={(e) =>
                                setSenhaAtual(e.target.value)
                            }
                            autoComplete="current-password"
                            required
                        />

                        <button
                            type="button"
                            className="perfil-olho"
                            onClick={() =>
                                setMostrarAtual((v) => !v)
                            }
                            aria-label={
                                mostrarAtual
                                    ? "Esconder senha"
                                    : "Mostrar senha"
                            }
                        >
                            {mostrarAtual
                                ? <EyeOff size={15} strokeWidth={2.2} />
                                : <Eye    size={15} strokeWidth={2.2} />}
                        </button>

                    </div>

                </div>


                {/* NOVA SENHA */}

                <div className="perfil-campo-editavel">

                    <label htmlFor="nova-senha">
                        Nova senha
                    </label>

                    <div className="perfil-campo-senha">

                        <input
                            id="nova-senha"
                            type={mostrarNova ? "text" : "password"}
                            value={novaSenha}
                            onChange={(e) =>
                                setNovaSenha(e.target.value)
                            }
                            autoComplete="new-password"
                            minLength={8}
                            required
                        />

                        <button
                            type="button"
                            className="perfil-olho"
                            onClick={() =>
                                setMostrarNova((v) => !v)
                            }
                            aria-label={
                                mostrarNova
                                    ? "Esconder senha"
                                    : "Mostrar senha"
                            }
                        >
                            {mostrarNova
                                ? <EyeOff size={15} strokeWidth={2.2} />
                                : <Eye    size={15} strokeWidth={2.2} />}
                        </button>

                    </div>

                </div>


                {/* CONFIRMAR */}

                <div className="perfil-campo-editavel">

                    <label htmlFor="confirmar-senha">
                        Confirmar nova senha
                    </label>

                    <input
                        id="confirmar-senha"
                        type="password"
                        value={confirmar}
                        onChange={(e) =>
                            setConfirmar(e.target.value)
                        }
                        autoComplete="new-password"
                        required
                    />

                </div>


                {erro && (
                    <div className="perfil-aviso perfil-aviso-erro">
                        {erro}
                    </div>
                )}

                {sucesso && (
                    <div className="perfil-aviso perfil-aviso-sucesso">
                        {sucesso}
                    </div>
                )}


                <div className="perfil-formulario-acoes">

                    <button
                        type="submit"
                        className="perfil-btn perfil-btn-primario"
                        disabled={salvando}
                    >
                        <KeyRound size={14} strokeWidth={2.4} />
                        {salvando
                            ? "A alterar..."
                            : "Alterar senha"}
                    </button>

                </div>

            </form>

        </section>
    );
}


export default PerfilSenha;