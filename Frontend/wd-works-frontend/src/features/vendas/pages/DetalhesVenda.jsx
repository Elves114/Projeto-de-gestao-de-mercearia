import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { buscarVenda } from "../services/vendaService";

function formatarValor(valor) {
    return Number(valor).toLocaleString("pt-MZ", {
        style: "currency",
        currency: "MZN",
    });
}

function DetalhesVenda() {
    const { id } = useParams();

    const [venda, setVenda] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    useEffect(() => {
        async function carregarVenda() {
            try {
                const data = await buscarVenda(id);

                setVenda(data);
            } catch (error) {
                console.error(error);

                setErro(
                    "Não foi possível carregar os detalhes da venda."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarVenda();
    }, [id]);

    if (carregando) {
        return (
            <div className="page-container">
                <p>Carregando venda...</p>
            </div>
        );
    }

    if (erro) {
        return (
            <div className="page-container">
                <p>{erro}</p>

                <Link to="/vendas">
                    Voltar para vendas
                </Link>
            </div>
        );
    }

    if (!venda) {
        return (
            <div className="page-container">
                <p>Venda não encontrada.</p>

                <Link to="/vendas">
                    Voltar para vendas
                </Link>
            </div>
        );
    }

    return (
        <div className="page-container">

            <div className="page-header">
                <div>
                    <h1>Venda #{venda.id}</h1>

                    <p>
                        Realizada em{" "}
                        {new Date(
                            venda.dataVenda
                        ).toLocaleString("pt-MZ")}
                    </p>
                </div>

                <Link
                    to="/vendas"
                    className="button-secondary"
                >
                    ← Voltar para vendas
                </Link>
            </div>

            <section className="venda-info">

                <div className="info-card">
                    <span>Funcionário</span>

                    <strong>
                        {venda.usuarioNome}
                    </strong>
                </div>

                <div className="info-card">
                    <span>Total</span>

                    <strong>
                        {formatarValor(venda.total)}
                    </strong>
                </div>

                <div className="info-card">
                    <span>Lucro</span>

                    <strong>
                        {formatarValor(venda.lucroTotal)}
                    </strong>
                </div>

            </section>

            <section className="content-card">

                <div className="content-card-header">
                    <div>
                        <h2>Produtos vendidos</h2>

                        <p>
                            Produtos incluídos nesta venda.
                        </p>
                    </div>
                </div>

                {venda.itens.length === 0 ? (
                    <div className="empty-state">
                        <p>
                            Esta venda não possui produtos.
                        </p>
                    </div>
                ) : (
                    <div className="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Produto</th>
                                    <th>Quantidade</th>
                                    <th>Preço unitário</th>
                                    <th>Subtotal</th>
                                    <th>Lucro</th>
                                </tr>
                            </thead>

                            <tbody>
                                {venda.itens.map((item) => (
                                    <tr key={item.id}>
                                        <td>
                                            <strong>
                                                {item.produtoNome}
                                            </strong>
                                        </td>

                                        <td>
                                            {item.quantidade}
                                        </td>

                                        <td>
                                            {formatarValor(
                                                item.precoUnitario
                                            )}
                                        </td>

                                        <td>
                                            {formatarValor(
                                                item.subtotal
                                            )}
                                        </td>

                                        <td>
                                            {formatarValor(
                                                item.lucro
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

            </section>

        </div>
    );
}

export default DetalhesVenda;