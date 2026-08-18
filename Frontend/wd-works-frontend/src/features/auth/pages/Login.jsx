
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

            <div className="auth-card">

                <div className="auth-header">
                    <h1>Login</h1>

                    <p>
                        Entre na sua conta para continuar
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

                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                            placeholder="Digite o seu email"
                            required
                        />

                    </div>


                    <div className="auth-field">

                        <label htmlFor="senha">
                            Senha
                        </label>

                        <input
                            id="senha"
                            type="password"
                            value={senha}
                            onChange={(event) =>
                                setSenha(event.target.value)
                            }
                            placeholder="Digite a sua senha"
                            required
                        />

                    </div>


                    {erro && (
                        <p className="auth-error">
                            {erro}
                        </p>
                    )}


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
    );
}

export default Login;
