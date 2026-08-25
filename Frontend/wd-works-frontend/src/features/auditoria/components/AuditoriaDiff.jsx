function parseJson(valor) {
    if (!valor) {
        return {};
    }

    try {
        return typeof valor === "string"
            ? JSON.parse(valor)
            : valor;
    } catch {
        return {};
    }
}

function formatarValor(valor) {
    if (valor === null || valor === undefined) {
        return "null";
    }

    if (typeof valor === "object") {
        return JSON.stringify(valor);
    }

    return String(valor);
}

function AuditoriaDiff({
    dadosAntigos,
    dadosNovos
}) {

    const antigo = parseJson(dadosAntigos);
    const novo = parseJson(dadosNovos);

    const chaves = Array.from(
        new Set([
            ...Object.keys(antigo),
            ...Object.keys(novo)
        ])
    );

    return (
        <div className="auditoria-diff">

            {/* DADOS ANTIGOS */}

            <div className="auditoria-diff-column">

                <div className="auditoria-diff-column-title">
                    Dados antigos
                </div>

                {chaves.map(chave => {

                    const existeAntigo =
                        Object.prototype.hasOwnProperty.call(
                            antigo,
                            chave
                        );

                    const valorAntigo =
                        formatarValor(
                            antigo[chave]
                        );

                    const valorNovo =
                        formatarValor(
                            novo[chave]
                        );

                    const alterado =
                        valorAntigo !== valorNovo;

                    return (
                        <div
                            key={`antigo-${chave}`}
                            className={`
                                auditoria-diff-line
                                ${alterado ? "alterado" : ""}
                            `}
                        >

                            <span className="auditoria-diff-key">
                                {chave}
                            </span>

                            <span
                                className={`
                                    auditoria-diff-value
                                    ${
                                        alterado && existeAntigo
                                            ? "valor-alterado"
                                            : ""
                                    }
                                `}
                            >
                                {existeAntigo
                                    ? valorAntigo
                                    : "—"}
                            </span>

                        </div>
                    );
                })}

            </div>


            {/* DADOS NOVOS */}

            <div className="auditoria-diff-column">

                <div className="auditoria-diff-column-title">
                    Dados novos
                </div>

                {chaves.map(chave => {

                    const existeNovo =
                        Object.prototype.hasOwnProperty.call(
                            novo,
                            chave
                        );

                    const valorAntigo =
                        formatarValor(
                            antigo[chave]
                        );

                    const valorNovo =
                        formatarValor(
                            novo[chave]
                        );

                    const alterado =
                        valorAntigo !== valorNovo;

                    return (
                        <div
                            key={`novo-${chave}`}
                            className={`
                                auditoria-diff-line
                                ${alterado ? "alterado" : ""}
                            `}
                        >

                            <span className="auditoria-diff-key">
                                {chave}
                            </span>

                            <span
                                className={`
                                    auditoria-diff-value
                                    ${
                                        alterado && existeNovo
                                            ? "valor-alterado"
                                            : ""
                                    }
                                `}
                            >
                                {existeNovo
                                    ? valorNovo
                                    : "—"}
                            </span>

                        </div>
                    );
                })}

            </div>

        </div>
    );
}

export default AuditoriaDiff;