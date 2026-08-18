import { Link } from "react-router-dom";
import VendaForm from "../components/VendaForm";

function NovaVenda() {
    return (
        <div className="page nova-venda-page">

            <div className="page-header">
                <div>
                    <h1>Nova Venda</h1>

                    <p>
                        Registe uma nova venda para a sua empresa.
                    </p>
                </div>

                <Link
                    to="/vendas"
                    className="button button-secondary"
                >
                    ← Voltar
                </Link>
            </div>

            <section className="page-section">
                <VendaForm />
            </section>

        </div>
    );
}

export default NovaVenda;