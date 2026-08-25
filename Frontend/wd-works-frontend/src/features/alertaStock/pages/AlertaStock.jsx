import { useEffect, useState } from "react";

import {
    listarAlertasStockAtivos
} from "../Service/AlertaService";

import AlertaStockTable from "../components/AlertaStockTable";
import "../style/AlertaStock.css";


function AlertasStock() {

    const [alertas, setAlertas] = useState([]);

    const [pagina, setPagina] = useState(0);

    const [totalPaginas, setTotalPaginas] =
        useState(0);

    const [totalElementos, setTotalElementos] =
        useState(0);

    const [carregando, setCarregando] =
        useState(true);

    const [erro, setErro] =
        useState("");

    const tamanhoPagina = 10;


    useEffect(() => {

        let ativo = true;

        async function carregarAlertas() {

            try {

                setCarregando(true);
                setErro("");

                const data =
                    await listarAlertasStockAtivos(
                        pagina,
                        tamanhoPagina
                    );

                if (!ativo) {
                    return;
                }

                setAlertas(
                    data.content || []
                );

                setTotalPaginas(
                    data.totalPages || 0
                );

                setTotalElementos(
                    data.totalElements || 0
                );

            } catch (error) {

                console.error(
                    "Erro ao carregar alertas:",
                    error
                );

                if (!ativo) {
                    return;
                }

                setErro(
                    "Não foi possível carregar os alertas de stock."
                );

            } finally {

                if (ativo) {
                    setCarregando(false);
                }

            }
        }

        carregarAlertas();

        return () => {
            ativo = false;
        };

    }, [pagina]);


    function paginaAnterior() {

        if (pagina > 0) {

            setPagina(
                pagina - 1
            );

        }
    }


    function proximaPagina() {

        if (pagina < totalPaginas - 1) {

            setPagina(
                pagina + 1
            );

        }
    }


    if (carregando) {

        return (
            <div className="alertas-stock-page">

                <p>
                    A carregar alertas de stock...
                </p>

            </div>
        );

    }


    if (erro) {

        return (
            <div className="alertas-stock-page">

                <p className="form-error">
                    {erro}
                </p>

            </div>
        );

    }


    return (

        <div className="alertas-stock-page">

            <div className="page-header">

                <div>

                    <h1>
                        Alertas de Stock
                    </h1>

                    <p>
                        Produtos que precisam de reposição
                        de stock.
                    </p>

                </div>

            </div>


            <section className="alertas-stock-list-card">

                <div className="section-header">

                    <div>

                        <h2>
                            Alertas ativos
                        </h2>

                        <p>
                            Produtos cujo stock está
                            abaixo ou igual ao mínimo definido.
                        </p>

                    </div>

                    <span className="categoria-count">
                        {totalElementos}
                    </span>

                </div>


                {alertas.length === 0 ? (

                    <div className="empty-state">

                        <p>
                            Não existem alertas de stock ativos.
                        </p>

                    </div>

                ) : (

                    <AlertaStockTable
                        alertas={alertas}
                    />

                )}


                {totalPaginas > 1 && (

                    <div className="pagination">

                        <button
                            type="button"
                            className="button button-secondary"
                            onClick={paginaAnterior}
                            disabled={pagina === 0}
                        >
                            Anterior
                        </button>


                        <span className="pagination-info">

                            Página{" "}
                            {pagina + 1}
                            {" "}de{" "}
                            {totalPaginas}

                        </span>


                        <button
                            type="button"
                            className="button button-secondary"
                            onClick={proximaPagina}
                            disabled={
                                pagina >= totalPaginas - 1
                            }
                        >
                            Próxima
                        </button>

                    </div>

                )}

            </section>

        </div>
    );
}


export default AlertasStock;