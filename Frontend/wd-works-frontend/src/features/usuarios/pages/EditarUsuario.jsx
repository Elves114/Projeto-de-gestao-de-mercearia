
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
    buscarUsuario,
    atualizarUsuario
} from "../services/usuarioService";
import "../style/Usuario.css";


function EditarUsuario() {

    const { id } = useParams();

    const navigate = useNavigate();

    const [nome, setNome] = useState("");
    const [email, setEmail] = useState("");

    const [carregando, setCarregando] = useState(true);
    const [salvando, setSalvando] = useState(false);

    const [erro, setErro] = useState("");


    useEffect(() => {

        let ativo = true;

        async function carregarUsuario() {

            try {

                const usuario = await buscarUsuario(id);

                if (ativo) {

                    setNome(usuario.nome);
                    setEmail(usuario.email);

                }

            } catch (error) {

                console.error(error);

                if (ativo) {

                    setErro(
                        "Não foi possível carregar o usuário."
                    );

                }

            } finally {

                if (ativo) {

                    setCarregando(false);

                }

            }
        }

        carregarUsuario();

        return () => {
            ativo = false;
        };

    }, [id]);


    async function handleSubmit(event) {

        event.preventDefault();

        setErro("");
        setSalvando(true);

        try {

            await atualizarUsuario(id, {
                nome,
                email
            });

            navigate("/usuarios");

        } catch (error) {

            console.error(error);

            setErro(
                "Não foi possível atualizar o usuário."
            );

        } finally {

            setSalvando(false);

        }
    }


    if (carregando) {

        return (
            <p>
                Carregando usuário...
            </p>
        );

    }


    return (
        <div>

            <h1>
                Editar usuário
            </h1>

            <p>
                Altere os dados do usuário.
            </p>


            {erro && (

                <div className="message message-error">
                    {erro}
                </div>

            )}


            <form onSubmit={handleSubmit}>

                <div>

                    <label>
                        Nome
                    </label>

                    <input
                        type="text"
                        value={nome}
                        onChange={(event) =>
                            setNome(event.target.value)
                        }
                        required
                    />

                </div>


                <div>

                    <label>
                        Email
                    </label>

                    <input
                        type="email"
                        value={email}
                        onChange={(event) =>
                            setEmail(event.target.value)
                        }
                        required
                    />

                </div>


                <button
                    type="submit"
                    disabled={salvando}
                >
                    {salvando
                        ? "Salvando..."
                        : "Salvar alterações"
                    }
                </button>


                <button
                    type="button"
                    onClick={() => navigate("/usuarios")}
                    disabled={salvando}
                >
                    Cancelar
                </button>

            </form>

        </div>
    );
}


export default EditarUsuario;

