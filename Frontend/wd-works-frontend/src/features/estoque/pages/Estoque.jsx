import { useEffect, useState } from "react";
import { listarEstoque } from "../services/estoqueService";
import EstoqueTable from "../components/EstoqueTable";
import { Link } from "react-router-dom";
import "../style/Estoque.css"


function Estoque() {
    const [estoques, setEstoques] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");

    function atualizarEstoque(estoqueAtualizado) {

        setEstoques((estoquesAtuais) =>
            estoquesAtuais.map((estoque) =>
                estoque.id === estoqueAtualizado.id
                    ? estoqueAtualizado
                    : estoque
            )
        );
    }

    useEffect(() => {
        let ativo = true;

        async function carregar() {
            try {
                const data = await listarEstoque();

                if (ativo) {
                    setEstoques(data.content);
                }
            } catch {
                if (ativo) {
                    setErro("Não foi possível carregar o estoque.");
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        carregar();

        return () => {
            ativo = false;
        };
    }, []);

    if (carregando) {
        return <p>A carregar estoque...</p>;
    }

    if (erro) {
        return <p>{erro}</p>;
    }

    return (
        <div className="estoque-page">

            <div className="page-header">
                <div>
                    <h1>Stock</h1>
                    <p>
                        Consulte a quantidade de produtos disponível na sua empresa.
                    </p>
                </div>
                <Link
                    to="/estoque/entrada"
                    className="button button-primary"
                >
                    Adicionar Stock
                </Link>
            </div>

            <section className="estoque-list-card">

                <div className="section-header">
                    <div>
                        <h2>Stock atual</h2>
                        <p>
                            Produtos e respectivas quantidades disponíveis.
                        </p>
                    </div>

                    <span className="categoria-count">
                        {estoques.length}
                    </span>
                </div>

                {estoques.length === 0 ? (
                    <div className="empty-state">
                        <p>
                            Nenhum produto encontrado no stock.
                        </p>
                    </div>
                ) : (
                    <EstoqueTable
                        estoques={estoques}
                        onAtualizar={atualizarEstoque}
                    />
                )}

            </section>

        </div>
    );
}

export default Estoque;