function AuditoriaTabela({ auditorias }) {
    return (
        <div className="table-container">
            <table className="data-table">
                <thead>
                    <tr>
                        <th>Data</th>
                        <th>Usuário</th>
                        <th>Tipo</th>
                        <th>Tabela</th>
                        <th>Registro</th>
                        <th>Descrição</th>
                    </tr>
                </thead>

                <tbody>
                    {auditorias.length === 0 ? (
                        <tr>
                            <td colSpan="6">
                                Nenhuma auditoria encontrada.
                            </td>
                        </tr>
                    ) : (
                        auditorias.map((auditoria) => (
                            <tr key={auditoria.id}>
                                <td>
                                    {new Date(
                                        auditoria.data
                                    ).toLocaleString()}
                                </td>

                                <td>
                                    {auditoria.usuarioNome}
                                </td>

                                <td>
                                    {auditoria.tipo}
                                </td>

                                <td>
                                    {auditoria.tabela}
                                </td>

                                <td>
                                    {auditoria.registo}
                                </td>

                                <td>
                                    {auditoria.descricao}
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default AuditoriaTabela;