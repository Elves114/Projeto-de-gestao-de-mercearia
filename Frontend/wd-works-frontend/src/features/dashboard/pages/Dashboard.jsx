function Dashboard() {
    return (
        <div className="dashboard">

            <div className="dashboard-header">
                <p>
                    Visão geral da sua empresa.
                </p>
            </div>

            <section className="dashboard-stats">

                <div className="dashboard-card">
                    <span className="dashboard-card-label">
                        Produtos
                    </span>

                    <strong className="dashboard-card-value">
                        —
                    </strong>

                    <span className="dashboard-card-description">
                        Produtos cadastrados
                    </span>
                </div>

                <div className="dashboard-card">
                    <span className="dashboard-card-label">
                        Stock
                    </span>

                    <strong className="dashboard-card-value">
                        —
                    </strong>

                    <span className="dashboard-card-description">
                        Itens em stock
                    </span>
                </div>

                <div className="dashboard-card">
                    <span className="dashboard-card-label">
                        Vendas
                    </span>

                    <strong className="dashboard-card-value">
                        —
                    </strong>

                    <span className="dashboard-card-description">
                        Vendas realizadas
                    </span>
                </div>

            </section>

            <section className="dashboard-section">

                <div className="dashboard-section-header">
                    <h2>Atividade recente</h2>
                </div>

                <div className="dashboard-empty">
                    <p>
                        Nenhuma atividade disponível.
                    </p>
                </div>

            </section>

        </div>
    );
}

export default Dashboard;