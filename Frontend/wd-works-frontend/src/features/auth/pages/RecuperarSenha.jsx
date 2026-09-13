import { useState } from "react";
import { Link } from "react-router-dom";

import { recuperarSenha } from "../service/authService";

import "../styles/Auth.css";

function RecuperarSenha() {

const [email, setEmail] = useState("");

const [erro, setErro] = useState("");
const [sucesso, setSucesso] = useState("");
const [carregando, setCarregando] = useState(false);


async function handleSubmit(event) {

    event.preventDefault();

    setErro("");
    setSucesso("");
    setCarregando(true);

    try {

        await recuperarSenha(email);

        setSucesso(
            "Se o email estiver cadastrado, você receberá as instruções para recuperar a sua senha."
        );

        setEmail("");

    } catch (error) {

        console.error(error);

        setErro(
            error.response?.data?.message ||
            "Não foi possível processar a recuperação da senha."
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
                        Recupere o acesso à sua conta
                        de forma simples e segura.
                    </p>

                </div>


                <div className="auth-brand-footer">
                    WD WORKS · Gestão empresarial
                </div>

            </div>


            {/* =================================================
                RECUPERAÇÃO
            ================================================== */}

            <div
                className={`auth-card ${erro ? "has-error" : ""}`}
            >

                <div className="auth-header">

                    <h1>
                        Recuperar senha
                    </h1>

                    <p>
                        Informe o seu email para receber
                        as instruções de recuperação.
                    </p>

                </div>


                <form
                    className="auth-form"
                    onSubmit={handleSubmit}
                >

                    <div className="auth-field">

                        <label htmlFor="email">
                            Email
                        </label>

                        <div className="auth-input-wrapper">

                            <span className="auth-input-icon">
                            </span>

                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(event) =>
                                    setEmail(event.target.value)
                                }
                                placeholder="Digite o seu email"
                                autoComplete="email"
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
                            ? "A processar..."
                            : "Enviar instruções"
                        }

                    </button>

                </form>


                <div className="auth-switch">

                    <span>
                        Lembrou-se da senha?
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

export default RecuperarSenha;
