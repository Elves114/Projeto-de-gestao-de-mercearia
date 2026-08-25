function AuditoriaPaginacao({
    pagina,
    totalPaginas,
    totalElementos,
    onPaginaChange
}) {

    if (totalPaginas <= 1) {
        return null;
    }

    function paginaAnterior() {

        if (pagina > 0) {
            onPaginaChange(pagina - 1);
        }
    }

    function proximaPagina() {

        if (pagina < totalPaginas - 1) {
            onPaginaChange(pagina + 1);
        }
    }

    return (
        <div className="auditoria-paginacao">

            <div className="auditoria-paginacao-info">

                <span>
                    {totalElementos} eventos
                </span>

                <span>
                    Página {pagina + 1} de {totalPaginas}
                </span>

            </div>


            <div className="auditoria-paginacao-controls">

                <button
                    type="button"
                    onClick={paginaAnterior}
                    disabled={pagina === 0}
                >
                    ‹
                </button>

                <span>
                    {pagina + 1}
                </span>

                <button
                    type="button"
                    onClick={proximaPagina}
                    disabled={
                        pagina >= totalPaginas - 1
                    }
                >
                    ›
                </button>

            </div>

        </div>
    );
}

export default AuditoriaPaginacao;