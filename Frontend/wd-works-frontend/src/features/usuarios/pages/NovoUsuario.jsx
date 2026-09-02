
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { criarUsuario } from "../services/usuarioService";
import "../style/NovoUsuario.css";

function calcularForcaSenha(senha) {

    if (!senha) {
        return {
            nivel: 0,
            texto: "",
            classe: ""
        };
    }

    let pontos = 0;

    // Pelo menos 8 caracteres
    if (senha.length >= 8) {
        pontos++;
    }

    // Letra maiúscula
    if (/[A-Z]/.test(senha)) {
        pontos++;
    }

    // Letra minúscula
    if (/[a-z]/.test(senha)) {
        pontos++;
    }

    // Número
    if (/[0-9]/.test(senha)) {
        pontos++;
    }

    // Caractere especial
    if (/[^A-Za-z0-9]/.test(senha)) {
        pontos++;
    }

    if (pontos <= 2) {
        return {
            nivel: 33,
            texto: "Fraca",
            classe: "senha-fraca"
        };
    }

    if (pontos <= 4) {
        return {
            nivel: 66,
            texto: "Média",
            classe: "senha-media"
        };
    }

    return {
        nivel: 100,
        texto: "Forte",
        classe: "senha-forte"
    };
}

function NovoUsuario() {

    const navigate = useNavigate();

    const [nome, setNome] = useState("");
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [perfil, setPerfil] = useState("FUNCIONARIO");

    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState("");
    const [sucesso, setSucesso] = useState("");

    const forcaSenha = calcularForcaSenha(senha);

    async function handleSubmit(event) {

        event.preventDefault();

        setErro("");
        setSucesso("");
        setCarregando(true);

        try {

            await criarUsuario({
                nome,
                email,
                senha,
                perfil
            });

            navigate("/usuarios");

        } catch (error) {

            console.error(error);

            setErro(
                "Não foi possível criar o usuário."
            );

        } finally {

            setCarregando(false);

        }
    }

    return (
        <div className="novo-usuario-page">

            <div className="page-header">

                <div>

                    <h1>
                        Novo usuário
                    </h1>

                    <p>
                        Crie um novo usuário para a sua empresa.
                    </p>

                </div>

            </div>


            <div className="novo-usuario-card">

                <div className="novo-usuario-card-header">

                    <h2>
                        Dados do usuário
                    </h2>

                    <span>
                        Preencha os dados para criar o acesso.
                    </span>

                </div>


                <form
                    className="novo-usuario-form"
                    onSubmit={handleSubmit}
                >

                    {/* NOME */}

                    <div className="form-group">

                        <label htmlFor="nome">
                            Nome
                        </label>

                        <input
                            id="nome"
                            type="text"
                            value={nome}
                            onChange={(event) =>
                                setNome(event.target.value)
                            }
                            placeholder="Digite o nome"
                            required
                        />

                    </div>


                    {/* EMAIL */}

                    <div className="form-group">

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
                            placeholder="Digite o email"
                            required
                        />

                    </div>


                    {/* SENHA */}

                    <div className="form-group">

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
                            placeholder="Digite uma senha segura"
                            required
                        />


                        {senha && (

                            <div className="password-strength">

                                <div className="password-strength-track">

                                    <div
                                        className={`password-strength-bar ${forcaSenha.classe}`}
                                        style={{
                                            width: `${forcaSenha.nivel}%`
                                        }}
                                    />

                                </div>


                                <div className="password-strength-info">

                                    <span>
                                        Força da senha
                                    </span>

                                    <strong
                                        className={forcaSenha.classe}
                                    >
                                        {forcaSenha.texto}
                                    </strong>

                                </div>

                            </div>

                        )}

                    </div>


                    {/* PERFIL */}

                    <div className="form-group">

                        <label htmlFor="perfil">
                            Perfil
                        </label>

                        <select
                            id="perfil"
                            value={perfil}
                            onChange={(event) =>
                                setPerfil(event.target.value)
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


                    {/* ERRO */}

                    {erro && (

                        <p className="form-error">
                            {erro}
                        </p>

                    )}


                    {/* SUCESSO */}

                    {sucesso && (

                        <p className="form-success">
                            {sucesso}
                        </p>

                    )}


                    {/* AÇÕES */}

                    <div className="form-actions">

                        <button
                            type="button"
                            className="ui-button ui-button-secondary"
                            onClick={() => navigate("/usuarios")}
                            disabled={carregando}
                        >
                            Cancelar
                        </button>


                        <button
                            type="submit"
                            className="ui-button ui-button-primary"
                            disabled={carregando}
                        >
                            {carregando
                                ? "Criando..."
                                : "Criar usuário"
                            }
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default NovoUsuario;

