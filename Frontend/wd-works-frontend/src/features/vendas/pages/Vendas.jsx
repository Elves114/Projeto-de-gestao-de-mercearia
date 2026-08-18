import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { listarVendas } from "../services/vendaService";
import VendaTabela from "../components/VendaTabela";

function Vendas() {
    const [vendas, setVendas] = useState([]);

    const [pagina, setPagina] = useState(0);
    const [totalPaginas, setTotalPaginas] = useState(0);

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    const tamanhoPagina = 10;

    useEffect(() => {
        async function carregar() {
            try {
                setCarregando(true);
                setErro(null);

                const data = await listarVendas(
                    pagina,
                    tamanhoPagina
                );

                setVendas(data.content);
                setTotalPaginas(data.totalPages);

            } catch (error) {
                console.error(error);

                setErro(
                    "Não foi possível carregar as vendas."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregar();
    }, [pagina]);

    function paginaAnterior() {
        if (pagina > 0) {
            setPagina((atual) => atual - 1);
        }
    }

    function proximaPagina() {
        if (pagina < totalPaginas - 1) {
            setPagina((atual) => atual + 1);
        }
    }

    if (carregando) {
        return (
            <div className="page-container">
                <p>Carregando vendas...</p>
            </div>
        );
    }

    if (erro) {
        return (
            <div className="page-container">
                <p>{erro}</p>
            </div>
        );
    }

    return (
        <div className="page-container">

            <div className="page-header">
                <div>
                    <h1>Vendas</h1>

                    <p>
                        Histórico das vendas realizadas pela empresa.
                    </p>
                </div>

                <Link
                    to="/vendas/nova"
                    className="button-primary"
                >
                    + Nova venda
                </Link>
            </div>

            <section className="content-card">

                <div className="content-card-header">
                    <div>
                        <h2>Histórico de vendas</h2>

                        <p>
                            Consulte as vendas realizadas e os seus detalhes.
                        </p>
                    </div>
                </div>

                <VendaTabela vendas={vendas} />

                {totalPaginas > 1 && (
                    <div className="pagination">

                        <button
                            type="button"
                            onClick={paginaAnterior}
                            disabled={pagina === 0}
                        >
                            ← Anterior
                        </button>

                        <span>
                            Página {pagina + 1} de{" "}
                            {totalPaginas}
                        </span>

                        <button
                            type="button"
                            onClick={proximaPagina}
                            disabled={
                                pagina === totalPaginas - 1
                            }
                        >
                            Próxima →
                        </button>

                    </div>
                )}

            </section>

        </div>
    );
}

export default Vendas;