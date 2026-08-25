import MovimentoStockForm from "../components/MovimentoStockForm";
import "../style/EntradaStock.css"

function EntradaStock() {
    return (
        <div className="entrada-stock-page">

            <div className="page-header">
                <div>
                    <h1>Entrada de Stock</h1>

                    <p>
                        Registre a entrada de produtos no stock.
                    </p>
                </div>
            </div>

            <section className="entrada-stock-card">
                <MovimentoStockForm />
            </section>

        </div>
    );
}

export default EntradaStock;