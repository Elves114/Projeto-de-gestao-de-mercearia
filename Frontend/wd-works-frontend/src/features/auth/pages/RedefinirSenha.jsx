import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";

import { redefinirSenha } from "../service/authService";

import "../styles/Auth.css";

function RedefinirSenha() {


const [searchParams] = useSearchParams();

const token = searchParams.get("token");

const navigate = useNavigate();


const [novaSenha, setNovaSenha] = useState("");
const [confirmarSenha, setConfirmarSenha] = useState("");

const [erro, setErro] = useState("");
const [sucesso, setSucesso] = useState("");
const [carregando, setCarregando] = useState(false);


async function handleSubmit(event) {

    event.preventDefault();

    setErro("");
    setSucesso("");


    if (!token) {

        setErro(
            "O link de recuperação é inválido ou está incompleto."
        );

        return;
    }


    if (novaSenha !== confirmarSenha) {

        setErro(
            "As senhas não coincidem."
        );

        return;
    }


    setCarregando(true);


    try {

        await redefinirSenha(
            token,
            novaSenha
        );


        setSucesso(
            "Senha redefinida com sucesso. Você será redirecionado para o login."
        );


        setTimeout(() => {
            navigate("/login");
        }, 2000);


    } catch (error) {

        console.error(error);

        setErro(
            error.response?.data?.message ||
            "Não foi possível redefinir a senha."
        );

    } finally {

        setCarregando(false);

    }
}


return (

    <div className="auth-page">

        <div className="auth-layout">


            {/* =================================================
                BRANDING
            ================================================== */}

            <div className="auth-brand">

                <div className="auth-brand-content">

                    <div className="auth-brand-logo">

                        <div className="auth-brand-logo-mark">
                            W
                        </div>

                        <span>
                            WD WORKS
                        </span>

                    </div>


                    <h2>
                        Gestão simples.
                        <br />
                        <span>Resultados melhores.</span>
                    </h2>


                    <p>
                        Defina uma nova senha para
                        recuperar o acesso à sua conta.
                    </p>

                </div>


                <div className="auth-brand-footer">
                    WD WORKS · Gestão empresarial
                </div>

            </div>


            {/* =================================================
                REDEFINIÇÃO
            ================================================== */}

            <div
                className={`auth-card ${erro ? "has-error" : ""}`}
            >

                <div className="auth-header">

                    <h1>
                        Nova senha
                    </h1>

                    <p>
                        Escolha uma nova senha para
                        a sua conta.
                    </p>

                </div>


                <form
                    className="auth-form"
                    onSubmit={handleSubmit}
                >


                    {/* NOVA SENHA */}

                    <div className="auth-field">

                        <label htmlFor="novaSenha">
                            Nova senha
                        </label>

                        <div className="auth-input-wrapper">

                            <span className="auth-input-icon">
                            </span>

                            <input
                                id="novaSenha"
                                type="password"
                                value={novaSenha}
                                onChange={(event) =>
                                    setNovaSenha(event.target.value)
                                }
                                placeholder="Digite a nova senha"
                                autoComplete="new-password"
                                required
                            />

                        </div>

                    </div>


                    {/* CONFIRMAR SENHA */}

                    <div className="auth-field">

                        <label htmlFor="confirmarSenha">
                            Confirmar senha
                        </label>

                        <div className="auth-input-wrapper">

                            <span className="auth-input-icon">
                            </span>

                            <input
                                id="confirmarSenha"
                                type="password"
                                value={confirmarSenha}
                                onChange={(event) =>
                                    setConfirmarSenha(event.target.value)
                                }
                                placeholder="Digite novamente a senha"
                                autoComplete="new-password"
                                required
                            />

                        </div>

                    </div>


                    {erro && (

                        <p className="auth-error">
                            {erro}
                        </p>

                    )}


                    {sucesso && (

                        <p className="auth-success">
                            {sucesso}
                        </p>

                    )}


                    <button
                        className="auth-button"
                        type="submit"
                        disabled={carregando}
                    >

                        {carregando
                            ? "A redefinir..."
                            : "Redefinir senha"
                        }

                    </button>

                </form>


                <div className="auth-switch">

                    <span>
                        Deseja voltar?
                    </span>

                    <Link to="/login">
                        Voltar ao login
                    </Link>

                </div>

            </div>

        </div>

    </div>
);

}

export default RedefinirSenha;
