import { useState } from "react";
import AuditoriaDiff from "./AuditoriaDiff";

function formatarJson(valor) {
    if (!valor) {
        return null;
    }

    try {
        const objeto =
            typeof valor === "string"
                ? JSON.parse(valor)
                : valor;

        return JSON.stringify(objeto, null, 2);
    } catch {
        return valor;
    }
}

function obterClasseGravidade(gravidade) {
    switch (gravidade) {
        case "CRITICAL":
            return "auditoria-severity-critical";

        case "WARNING":
            return "auditoria-severity-warning";

        case "INFO":
        default:
            return "auditoria-severity-info";
    }
}

function obterIconeGravidade(gravidade) {
    switch (gravidade) {
        case "CRITICAL":
            return "⚠";

        case "WARNING":
            return "●";

        case "INFO":
        default:
            return "●";
    }
}

function AuditoriaTabela({
    auditorias,
    mudandoFiltro
}) {
    const [auditoriaAberta, setAuditoriaAberta] =
        useState(null);

    function alternarAuditoria(id) {
        setAuditoriaAberta(
            atual => atual === id ? null : id
        );
    }

    return (
        <div
            className={`
        table-container
        auditoria-table-container
        ${mudandoFiltro ? "auditoria-filtro-mudando" : ""}
    `}
        >

            <table className="data-table auditoria-table">

                <thead>
                    <tr>
                        <th>Data</th>
                        <th>Usuário</th>
                        <th>Tipo</th>
                        <th>Gravidade</th>
                        <th>Tabela</th>
                        <th>Registro</th>
                        <th>Descrição</th>
                    </tr>
                </thead>

                <tbody>

                    {auditorias.length === 0 ? (

                        <tr>
                            <td colSpan="7">
                                Nenhuma auditoria encontrada.
                            </td>
                        </tr>

                    ) : (

                        auditorias.map(auditoria => {

                            const aberta =
                                auditoriaAberta === auditoria.id;

                            return (
                                <AuditoriaLinha
                                    key={auditoria.id}
                                    auditoria={auditoria}
                                    aberta={aberta}
                                    onClick={() =>
                                        alternarAuditoria(
                                            auditoria.id
                                        )
                                    }
                                />
                            );
                        })
                    )}

                </tbody>

            </table>

        </div>
    );
}

function AuditoriaLinha({
    auditoria,
    aberta,
    onClick
}) {

    const dadosAntigos =
        formatarJson(auditoria.dadosAntigos);

    const dadosNovos =
        formatarJson(auditoria.dadosNovos);

    const payload =
        formatarJson(auditoria.payload);

    const classeGravidade =
        obterClasseGravidade(
            auditoria.gravidade
        );

    return (
        <>
            <tr
                className={`
        auditoria-row
        ${aberta ? "auditoria-row-aberta" : ""}
    `}
                onClick={onClick}
            >

                <td className="auditoria-data">
                    {new Date(
                        auditoria.data
                    ).toLocaleString()}
                </td>

                <td className="auditoria-usuario">
                    {auditoria.usuarioNome}
                </td>

                <td>
                    <span className="auditoria-tipo">
                        {auditoria.tipo}
                    </span>
                </td>

                <td>
                    <span
                        className={`
                auditoria-gravidade
                ${classeGravidade}
            `}
                    >
                        <span className="auditoria-gravidade-icon">
                            {obterIconeGravidade(
                                auditoria.gravidade
                            )}
                        </span>

                        {auditoria.gravidade}
                    </span>
                </td>

                <td>
                    {auditoria.tabela}
                </td>

                <td>
                    {auditoria.registo}
                </td>

                <td>
                    <div className="auditoria-descricao">

                        <span>
                            {auditoria.descricao}
                        </span>

                        <span
                            className={`
                    auditoria-expand-icon
                    ${aberta ? "aberto" : ""}
                `}
                        >
                            ›
                        </span>

                    </div>
                </td>

            </tr>
            <tr
                className={`
                    auditoria-detail-row
                    ${aberta ? "aberta" : ""}
                `}
            >

                <td colSpan="7">

                    <div className="auditoria-detail-wrapper">

                        <div className="auditoria-detail">

                            <div className="auditoria-detail-header">

                                <div>
                                    <span className="auditoria-detail-label">
                                        INSPEÇÃO DE AUDITORIA
                                    </span>

                                    <h3>
                                        {auditoria.descricao}
                                    </h3>
                                </div>

                                <span className="auditoria-detail-id">
                                    #{auditoria.id}
                                </span>

                            </div>


                            <div className="auditoria-context-grid">

                                <div className="auditoria-context-item">
                                    <span>IP</span>
                                    <strong>
                                        {auditoria.ip || "—"}
                                    </strong>
                                </div>

                                <div className="auditoria-context-item">
                                    <span>Método</span>
                                    <strong>
                                        {auditoria.metodo || "—"}
                                    </strong>
                                </div>

                                <div className="auditoria-context-item">
                                    <span>Endpoint</span>
                                    <strong>
                                        {auditoria.endpoint || "—"}
                                    </strong>
                                </div>

                                <div className="auditoria-context-item">
                                    <span>Registro</span>
                                    <strong>
                                        {auditoria.tabela}
                                        {" #"}
                                        {auditoria.registo}
                                    </strong>
                                </div>

                            </div>


                            {(dadosAntigos || dadosNovos) && (

                                <div className="auditoria-diff-section">

                                    <div className="auditoria-diff-title">

                                        <div>
                                            <span className="auditoria-json-dot" />

                                            Alterações detectadas
                                        </div>

                                        <span>
                                            DIFF
                                        </span>

                                    </div>

                                    <AuditoriaDiff
                                        dadosAntigos={dadosAntigos}
                                        dadosNovos={dadosNovos}
                                    />

                                </div>

                            )}


                            {payload && (

                                <div className="auditoria-payload">

                                    <div className="auditoria-json-header">

                                        <div>
                                            <span className="auditoria-json-dot" />
                                            Payload da requisição
                                        </div>

                                        <span>
                                            REQUEST
                                        </span>

                                    </div>

                                    <pre>
                                        {payload}
                                    </pre>

                                </div>

                            )}

                        </div>

                    </div>

                </td>

            </tr>
        </>
    );
}

export default AuditoriaTabela;