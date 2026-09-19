import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { cadastrar } from "../service/authService";
import { useAuth } from "../../../contexts/useAuth";

import "../styles/Auth.css";


/* ============================================================
   VALIDAÇÃO CLIENT-SIDE
   ============================================================ */

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const REGEX_DIGITOS = /^\d+$/;

function validarFormulario(formulario) {

    const erros = {};

    /* -------- Empresa -------- */

    if (!formulario.nomeEmpresa.trim()) {
        erros.nomeEmpresa = "O nome da empresa é obrigatório.";
    } else if (formulario.nomeEmpresa.trim().length < 2) {
        erros.nomeEmpresa = "O nome deve ter pelo menos 2 caracteres.";
    } else if (formulario.nomeEmpresa.length > 150) {
        erros.nomeEmpresa = "O nome não pode ultrapassar 150 caracteres.";
    }

    if (!formulario.nuit.trim()) {
        erros.nuit = "O NUIT é obrigatório.";
    } else if (!REGEX_DIGITOS.test(formulario.nuit)) {
        erros.nuit = "O NUIT deve conter apenas dígitos.";
    } else if (formulario.nuit.length !== 9) {
        erros.nuit = "O NUIT deve conter exatamente 9 dígitos.";
    }

    if (!formulario.emailEmpresa.trim()) {
        erros.emailEmpresa = "O email da empresa é obrigatório.";
    } else if (!REGEX_EMAIL.test(formulario.emailEmpresa)) {
        erros.emailEmpresa = "Informe um email válido.";
    }

    if (!formulario.contacto.trim()) {
        erros.contacto = "O contacto é obrigatório.";
    } else if (!REGEX_DIGITOS.test(formulario.contacto)) {
        erros.contacto = "O contacto deve conter apenas dígitos.";
    } else if (formulario.contacto.length !== 9) {
        erros.contacto = "O contacto deve conter exatamente 9 dígitos.";
    }

    if (formulario.endereco && formulario.endereco.length > 255) {
        erros.endereco = "O endereço não pode ultrapassar 255 caracteres.";
    }

    /* -------- Administrador -------- */

    if (!formulario.nomeAdministrador.trim()) {
        erros.nomeAdministrador = "O nome do administrador é obrigatório.";
    } else if (formulario.nomeAdministrador.length > 150) {
        erros.nomeAdministrador = "O nome não pode ultrapassar 150 caracteres.";
    }

    if (!formulario.emailAdministrador.trim()) {
        erros.emailAdministrador = "O email do administrador é obrigatório.";
    } else if (!REGEX_EMAIL.test(formulario.emailAdministrador)) {
        erros.emailAdministrador = "Informe um email válido.";
    }

    if (!formulario.senhaAdministrador) {
        erros.senhaAdministrador = "A senha é obrigatória.";
    } else if (formulario.senhaAdministrador.length < 8) {
        erros.senhaAdministrador = "A senha deve ter pelo menos 8 caracteres.";
    } else if (formulario.senhaAdministrador.length > 100) {
        erros.senhaAdministrador = "A senha não pode ultrapassar 100 caracteres.";
    }

    return erros;
}


/* ============================================================
   COMPONENTE
   ============================================================ */

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
    const [errosCampos, setErrosCampos] = useState({});
    const [carregando, setCarregando] = useState(false);


    function handleChange(event) {

        const { name, value } = event.target;

        setFormulario((anterior) => ({
            ...anterior,
            [name]: value,
        }));

        /* Limpa o erro deste campo assim que o utilizador começa a corrigir */
        setErrosCampos((anterior) => {
            if (!anterior[name]) return anterior;
            const copia = { ...anterior };
            delete copia[name];
            return copia;
        });
    }


    async function handleSubmit(event) {

        event.preventDefault();

        setErro("");

        /* 1) Validação client-side */
        const errosCliente = validarFormulario(formulario);

        if (Object.keys(errosCliente).length > 0) {
            setErrosCampos(errosCliente);
            return;
        }

        setErrosCampos({});
        setCarregando(true);

        try {

            const response = await cadastrar(formulario);

            login(response.token);
            navigate("/dashboard");

        } catch (error) {

            console.error(error);

            const dados = error.response?.data;

            /*
             * Se o backend devolveu erros de validação por campo,
             * mostra-os em cada campo específico.
             */
            if (dados?.erros && typeof dados.erros === "object") {

                setErrosCampos(dados.erros);
                setErro(dados.mensagem || "Existem erros de validação.");

            } else {

                setErro(
                    dados?.mensagem ||
                    dados?.message ||
                    "Não foi possível realizar o cadastro."
                );
            }

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

                            <span>WD WORKS</span>

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

                <div className={`auth-card auth-card-large ${erro ? "has-error" : ""}`}>

                    <div className="auth-header">

                        <h1>Criar conta</h1>

                        <p>
                            Registe a sua empresa e crie o administrador
                        </p>

                    </div>


                    <form className="auth-form" onSubmit={handleSubmit} noValidate>

                        {/* =================================================
                            EMPRESA
                        ================================================== */}

                        <div className="auth-section">

                            <h2>Dados da empresa</h2>

                            <div className="auth-grid">

                                <div className={`auth-field ${errosCampos.nomeEmpresa ? "has-error" : ""}`}>

                                    <label htmlFor="nomeEmpresa">Nome da empresa</label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="nomeEmpresa"
                                            type="text"
                                            name="nomeEmpresa"
                                            value={formulario.nomeEmpresa}
                                            onChange={handleChange}
                                            placeholder="Nome da empresa"
                                            autoComplete="organization"
                                        />

                                    </div>

                                    {errosCampos.nomeEmpresa && (
                                        <p className="auth-field-error">
                                            {errosCampos.nomeEmpresa}
                                        </p>
                                    )}

                                </div>


                                <div className={`auth-field ${errosCampos.nuit ? "has-error" : ""}`}>

                                    <label htmlFor="nuit">NUIT</label>

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
                                        />

                                    </div>

                                    {errosCampos.nuit && (
                                        <p className="auth-field-error">
                                            {errosCampos.nuit}
                                        </p>
                                    )}

                                </div>


                                <div className={`auth-field ${errosCampos.emailEmpresa ? "has-error" : ""}`}>

                                    <label htmlFor="emailEmpresa">Email da empresa</label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="emailEmpresa"
                                            type="email"
                                            name="emailEmpresa"
                                            value={formulario.emailEmpresa}
                                            onChange={handleChange}
                                            placeholder="email@empresa.com"
                                            autoComplete="email"
                                        />

                                    </div>

                                    {errosCampos.emailEmpresa && (
                                        <p className="auth-field-error">
                                            {errosCampos.emailEmpresa}
                                        </p>
                                    )}

                                </div>


                                <div className={`auth-field ${errosCampos.contacto ? "has-error" : ""}`}>

                                    <label htmlFor="contacto">Contacto</label>

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
                                        />

                                    </div>

                                    {errosCampos.contacto && (
                                        <p className="auth-field-error">
                                            {errosCampos.contacto}
                                        </p>
                                    )}

                                </div>


                                <div className={`auth-field auth-field-full ${errosCampos.endereco ? "has-error" : ""}`}>

                                    <label htmlFor="endereco">Endereço</label>

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

                                    {errosCampos.endereco && (
                                        <p className="auth-field-error">
                                            {errosCampos.endereco}
                                        </p>
                                    )}

                                </div>

                            </div>

                        </div>


                        {/* =================================================
                            ADMINISTRADOR
                        ================================================== */}

                        <div className="auth-section">

                            <h2>Dados do administrador</h2>

                            <div className="auth-grid">

                                <div className={`auth-field ${errosCampos.nomeAdministrador ? "has-error" : ""}`}>

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
                                        />

                                    </div>

                                    {errosCampos.nomeAdministrador && (
                                        <p className="auth-field-error">
                                            {errosCampos.nomeAdministrador}
                                        </p>
                                    )}

                                </div>


                                <div className={`auth-field ${errosCampos.emailAdministrador ? "has-error" : ""}`}>

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
                                        />

                                    </div>

                                    {errosCampos.emailAdministrador && (
                                        <p className="auth-field-error">
                                            {errosCampos.emailAdministrador}
                                        </p>
                                    )}

                                </div>


                                <div className={`auth-field auth-field-full ${errosCampos.senhaAdministrador ? "has-error" : ""}`}>

                                    <label htmlFor="senhaAdministrador">Senha</label>

                                    <div className="auth-input-wrapper">

                                        <input
                                            id="senhaAdministrador"
                                            type="password"
                                            name="senhaAdministrador"
                                            value={formulario.senhaAdministrador}
                                            onChange={handleChange}
                                            placeholder="Mínimo de 8 caracteres"
                                            autoComplete="new-password"
                                        />

                                    </div>

                                    {errosCampos.senhaAdministrador && (
                                        <p className="auth-field-error">
                                            {errosCampos.senhaAdministrador}
                                        </p>
                                    )}

                                </div>

                            </div>

                        </div>


                        {/* =================================================
                            ERRO GERAL
                        ================================================== */}

                        {erro && (
                            <p className="auth-error">{erro}</p>
                        )}


                        {/* =================================================
                            BOTÃO
                        ================================================== */}

                        <button
                            className="auth-button"
                            type="submit"
                            disabled={carregando}
                        >
                            {carregando ? "A criar conta..." : "Criar conta"}
                        </button>

                    </form>


                    {/* LOGIN */}

                    <div className="auth-switch">

                        <span>Já tem uma conta?</span>

                        <Link to="/login">Entrar</Link>

                    </div>

                </div>

            </div>

        </div>
    );
}


export default Cadastro;