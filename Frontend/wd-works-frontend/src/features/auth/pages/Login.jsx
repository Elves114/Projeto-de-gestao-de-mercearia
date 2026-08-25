import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { login as loginApi } from "../service/authService";
import { useAuth } from "../../../contexts/useAuth";

import "../styles/Auth.css";


function Login() {

    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");

    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();


    async function handleSubmit(event) {

        event.preventDefault();

        setErro("");
        setCarregando(true);

        try {

            const response = await loginApi(email, senha);

            login(response.token);

            navigate("/dashboard");

        } catch (error) {

            console.error(error);

            setErro("Email ou senha incorretos.");

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
                            Entre na sua conta para gerir
                            a sua empresa, produtos, vendas
                            e muito mais.
                        </p>

                    </div>


                    <div className="auth-brand-footer">
                        WD WORKS · Gestão empresarial
                    </div>

                </div>


                {/* =================================================
                    LOGIN
                ================================================== */}

                <div
                    className={`auth-card ${erro ? "has-error" : ""}`}
                >

                    <div className="auth-header">

                        <h1>
                            Bem-vindo de volta
                        </h1>

                        <p>
                            Entre na sua conta para continuar
                        </p>

                    </div>


                    <form
                        className="auth-form"
                        onSubmit={handleSubmit}
                    >


                        {/* EMAIL */}
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


                        <div className="auth-field">

                            <label htmlFor="senha">
                                Senha
                            </label>

                            <div className="auth-input-wrapper">

                                <span className="auth-input-icon">
                                    
                                </span>

                                <input
                                    id="senha"
                                    type="password"
                                    value={senha}
                                    onChange={(event) =>
                                        setSenha(event.target.value)
                                    }
                                    placeholder="Digite a sua senha"
                                    autoComplete="current-password"
                                    required
                                />

                            </div>

                        </div>


                        {/* ERRO */}

                        {erro && (

                            <p className="auth-error">
                                {erro}
                            </p>

                        )}


                        {/* BOTÃO */}

                        <button
                            className="auth-button"
                            type="submit"
                            disabled={carregando}
                        >

                            {carregando
                                ? "A entrar..."
                                : "Entrar"
                            }

                        </button>

                    </form>


                    {/* CADASTRO */}

                    <div className="auth-switch">

                        <span>
                            Ainda não tem uma conta?
                        </span>

                        <Link to="/cadastro">
                            Criar conta
                        </Link>

                    </div>

                </div>

            </div>

        </div>
    );
}


export default Login;