import { useEffect, useState } from "react";
import { listarProdutos, criarProduto } from "../services/produtoService";
import { listarCategorias } from "../../categorias/services/categoriaService";
import ProdutoTable from "../components/ProdutoTable";
import ProdutoForm from "../components/ProdutoForm";

function Produtos() {
    const [produtos, setProdutos] = useState([]);
    const [categorias, setCategorias] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");
    const [mensagem, setMensagem] = useState("");

    useEffect(() => {
        let ativo = true;

        async function carregarDados() {
            try {
                const [produtosData, categoriasData] =
                    await Promise.all([
                        listarProdutos(),
                        listarCategorias(),
                    ]);

                if (ativo) {
                    setProdutos(produtosData.content);
                    setCategorias(categoriasData.content);
                }
            } catch {
                if (ativo) {
                    setErro("Não foi possível carregar os dados.");
                }
            } finally {
                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        carregarDados();

        return () => {
            ativo = false;
        };
    }, []);

    async function handleCriarProduto(event) {
        event.preventDefault();

        const formData = new FormData(event.target);

        const produto = {
            nome: formData.get("nome"),
            precoCompra: Number(formData.get("precoCompra")),
            precoVenda: Number(formData.get("precoVenda")),
            categoriaId: Number(formData.get("categoriaId")),
        };

        try {
            const novoProduto = await criarProduto(produto);

            setProdutos((produtosAtuais) => [
                ...produtosAtuais,
                novoProduto,
            ]);

            setMensagem("Produto criado com sucesso.");
            event.target.reset();
        } catch {
            setErro("Não foi possível criar o produto.");
        }
    }

    if (carregando) {
        return <p>A carregar produtos...</p>;
    }

    if (erro) {
        return <p>{erro}</p>;
    }

    return (
    <div className="produtos-page">

        <div className="page-header">
            <div>
                <h1>Produtos</h1>
                <p>
                    Gerencie os produtos da sua empresa.
                </p>
            </div>
        </div>

        {mensagem && (
            <div className="alert-success">
                {mensagem}
            </div>
        )}

        <section className="produto-form-card">

            <div className="section-header">
                <div>
                    <h2>Novo produto</h2>
                    <p>
                        Cadastre um novo produto no sistema.
                    </p>
                </div>
            </div>

            <ProdutoForm
                categorias={categorias}
                onSubmit={handleCriarProduto}
            />

        </section>

        <section className="produtos-list-card">

            <div className="section-header">
                <div>
                    <h2>Produtos cadastrados</h2>
                    <p>
                        Lista de produtos disponíveis.
                    </p>
                </div>

                <span className="categoria-count">
                    {produtos.length}
                </span>
            </div>

            {produtos.length === 0 ? (
                <div className="empty-state">
                    <p>
                        Nenhum produto encontrado.
                    </p>
                </div>
            ) : (
                <ProdutoTable produtos={produtos} />
            )}

        </section>

    </div>
);
}

export default Produtos;