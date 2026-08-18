import { useNavigate } from "react-router-dom";

function UsuarioTabela({
    usuarios,
    onAtivar,
    onDesativar
}) {

    const navigate = useNavigate();

    return (
        <div className="table-container">

            <table className="data-table">

                <thead>

                    <tr>

                        <th>
                            Nome
                        </th>

                        <th>
                            Email
                        </th>

                        <th>
                            Perfil
                        </th>

                        <th>
                            Status
                        </th>

                        <th>
                            Ações
                        </th>

                    </tr>

                </thead>

                <tbody>

                    {usuarios.map((usuario) => (

                        <tr key={usuario.id}>

                            <td>
                                <strong>
                                    {usuario.nome}
                                </strong>
                            </td>

                            <td>
                                {usuario.email}
                            </td>

                            <td>
                                {usuario.perfil}
                            </td>

                            <td>

                                {usuario.status === "ATIVO" ? (

                                    <span className="status-badge status-ativo">
                                        Ativo
                                    </span>

                                ) : (

                                    <span className="status-badge status-inativo">
                                        {usuario.status}
                                    </span>

                                )}

                            </td>

                            <td>

                                <button
                                    type="button"
                                    className="button button-primary"
                                    onClick={() =>
                                        navigate(
                                            `/usuarios/${usuario.id}/editar`
                                        )
                                    }
                                >
                                    Editar
                                </button>


                                {usuario.status === "ATIVO" ? (

                                    <button
                                        type="button"
                                        className="button button-secondary"
                                        onClick={() =>
                                            onDesativar(usuario.id)
                                        }
                                    >
                                        Desativar
                                    </button>

                                ) : (

                                    <button
                                        type="button"
                                        className="button button-primary"
                                        onClick={() =>
                                            onAtivar(usuario.id)
                                        }
                                    >
                                        Ativar
                                    </button>

                                )}

                            </td>

                        </tr>

                    ))}

                </tbody>

            </table>

        </div>
    );
}

export default UsuarioTabela;