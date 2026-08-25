import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { cadastrar } from "../service/authService";
import { useAuth } from "../../../contexts/useAuth";

import "../styles/Auth.css";


function Cadastro() {

    const navigate = useNavigate();

    const { login } = useAuth();


    const [formulario, setFormulario] = useState({

        nomeEmpresa: "",
        nuit: "",
        emailEmpresa: "",
        contacto: "",
        endereco: "",

        nomeAdministrador: "",
        emailAdministrador: "",
        senhaAdministrador: "",

    });


    const [erro, setErro] = useState("");
    const [carregando, setCarregando] = useState(false);


    function handleChange(event) {

        const { name, value } = event.target;

        setFormulario((estadoAnterior) => ({

            ...estadoAnterior,

            [name]: value,

        }));
    }


    async function handleSubmit(event) {

        event.preventDefault();

        setErro("");
        setCarregando(true);

        try {

            const response = await cadastrar(formulario);

            login(response.token);

            navigate("/dashboard");

        } catch (error) {

            console.error(error);

            setErro(
                error.response?.data?.message ||
                "Não foi possível realizar o cadastro."
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
                            Comece a gerir
                            <br />
                            <span>de forma inteligente.</span>
                        </h2>


                        <p>
                            Registe a sua empresa e tenha
                            tudo o que precisa para controlar
                            produtos, vendas e operações.
                        </p>

                    </div>


                    <div className="auth-brand-footer">
                        WD WORKS · Gestão empresarial
                    </div>

                </div>


                {/* =================================================
                    CADASTRO
                ================================================== */}

                <div
                    className={`auth-card auth-card-large ${erro ? "has-error" : ""
                        }`}
                >

                    <div className="auth-header">

                        <h1>
                            Criar conta
                        </h1>

                        <p>
                            Registe a sua empresa e crie o administrador
                        </p>

                    </div>


                    <form
                        className="auth-form"
                        onSubmit={handleSubmit}
                    >


                        {/* =================================================
                            EMPRESA
                        ================================================== */}
                        <div className="auth-section">

                            <h2>Dados da empresa</h2>

                            <div className="auth-grid">

                                <div className="auth-field">

                                    <label htmlFor="nomeEmpresa">
                                        Nome da empresa
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="nomeEmpresa"
                                            type="text"
                                            name="nomeEmpresa"
                                            value={formulario.nomeEmpresa}
                                            onChange={handleChange}
                                            placeholder="Nome da empresa"
                                            autoComplete="organization"
                                            required
                                        />

                                    </div>

                                </div>


                                <div className="auth-field">

                                    <label htmlFor="nuit">
                                        NUIT
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="nuit"
                                            type="text"
                                            name="nuit"
                                            value={formulario.nuit}
                                            onChange={handleChange}
                                            placeholder="Ex: 123456789"
                                            maxLength="9"
                                            inputMode="numeric"
                                            required
                                        />

                                    </div>

                                </div>


                                <div className="auth-field">

                                    <label htmlFor="emailEmpresa">
                                        Email da empresa
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="emailEmpresa"
                                            type="email"
                                            name="emailEmpresa"
                                            value={formulario.emailEmpresa}
                                            onChange={handleChange}
                                            placeholder="email@empresa.com"
                                            autoComplete="email"
                                            required
                                        />

                                    </div>

                                </div>


                                <div className="auth-field">

                                    <label htmlFor="contacto">
                                        Contacto
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="contacto"
                                            type="text"
                                            name="contacto"
                                            value={formulario.contacto}
                                            onChange={handleChange}
                                            placeholder="Ex: 841234567"
                                            maxLength="9"
                                            inputMode="numeric"
                                            required
                                        />

                                    </div>

                                </div>


                                <div className="auth-field auth-field-full">

                                    <label htmlFor="endereco">
                                        Endereço
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="endereco"
                                            type="text"
                                            name="endereco"
                                            value={formulario.endereco}
                                            onChange={handleChange}
                                            placeholder="Endereço da empresa"
                                            autoComplete="street-address"
                                        />

                                    </div>

                                </div>

                            </div>

                        </div>


                        {/* =================================================
                            ADMINISTRADOR
                        ================================================== */}

                        <div className="auth-section">

                            <h2>Dados do administrador</h2>

                            <div className="auth-grid">

                                <div className="auth-field">

                                    <label htmlFor="nomeAdministrador">
                                        Nome do administrador
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="nomeAdministrador"
                                            type="text"
                                            name="nomeAdministrador"
                                            value={formulario.nomeAdministrador}
                                            onChange={handleChange}
                                            placeholder="Nome completo"
                                            autoComplete="name"
                                            required
                                        />

                                    </div>

                                </div>


                                <div className="auth-field">

                                    <label htmlFor="emailAdministrador">
                                        Email do administrador
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="emailAdministrador"
                                            type="email"
                                            name="emailAdministrador"
                                            value={formulario.emailAdministrador}
                                            onChange={handleChange}
                                            placeholder="email@exemplo.com"
                                            autoComplete="email"
                                            required
                                        />

                                    </div>

                                </div>


                                <div className="auth-field auth-field-full">

                                    <label htmlFor="senhaAdministrador">
                                        Senha
                                    </label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="senhaAdministrador"
                                            type="password"
                                            name="senhaAdministrador"
                                            value={formulario.senhaAdministrador}
                                            onChange={handleChange}
                                            placeholder="Mínimo de 8 caracteres"
                                            autoComplete="new-password"
                                            minLength="8"
                                            required
                                        />

                                    </div>

                                </div>

                            </div>

                        </div>


                        {/* =================================================
                            ERRO
                        ================================================== */}

                        {erro && (

                            <p className="auth-error">
                                {erro}
                            </p>

                        )}


                        {/* =================================================
                            BOTÃO
                        ================================================== */}

                        <button
                            className="auth-button"
                            type="submit"
                            disabled={carregando}
                        >

                            {carregando
                                ? "A criar conta..."
                                : "Criar conta"
                            }

                        </button>

                    </form>


                    {/* LOGIN */}

                    <div className="auth-switch">

                        <span>
                            Já tem uma conta?
                        </span>

                        <Link to="/login">
                            Entrar
                        </Link>

                    </div>

                </div>

            </div>

        </div>
    );
}


export default Cadastro;