
import { useEffect, useState } from "react";
import {
    listarMinhasAuditorias,
    listarAuditoriasPorTipo,
} from "../services/auditoriaService";
import AuditoriaTabela from "../components/AuditoriaTabela";

function Auditoria() {
    const [auditorias, setAuditorias] = useState([]);
    const [tipo, setTipo] = useState("");
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    useEffect(() => {
        async function carregarAuditorias() {
            setCarregando(true);
            setErro(null);

            try {
                const data = tipo
                    ? await listarAuditoriasPorTipo(tipo)
                    : await listarMinhasAuditorias();

                setAuditorias(data.content);
            } catch (error) {
                console.error(error);

                setErro(
                    "Não foi possível carregar as auditorias."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarAuditorias();
    }, [tipo]);

    function handleTipoChange(event) {
        setTipo(event.target.value);
    }

    if (carregando) {
        return <p>Carregando auditorias...</p>;
    }

    if (erro) {
        return <p>{erro}</p>;
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1>Auditoria</h1>
                    <p>
                        Histórico das atividades realizadas no sistema.
                    </p>
                </div>
            </div>

            <div className="content-card">
                <div className="content-card-header">
                    <h2>Histórico de auditoria</h2>
                    <p>
                        Consulte e filtre as atividades registadas.
                    </p>
                </div>

                <div className="form-group" style={{ padding: "20px 24px" }}>
                    <label htmlFor="tipo-auditoria">
                        Filtrar por tipo
                    </label>

                    <select
                        id="tipo-auditoria"
                        value={tipo}
                        onChange={handleTipoChange}
                    >
                        <option value="">
                            Minhas auditorias
                        </option>

                        <option value="CRIACAO">
                            Criação
                        </option>

                        <option value="ALTERACAO">
                            Alteração
                        </option>

                        <option value="EXCLUSAO">
                            Exclusão
                        </option>

                        <option value="LOGIN">
                            Login
                        </option>

                        <option value="LOGOUT">
                            Logout
                        </option>

                        <option value="VENDA">
                            Venda
                        </option>
                    </select>
                </div>

                <AuditoriaTabela auditorias={auditorias} />
            </div>
        </div>
    );
}

export default Auditoria;
