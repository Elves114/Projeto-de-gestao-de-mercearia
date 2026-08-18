import { useEffect, useState } from "react";
import { listarCategorias } from "../services/categoriaService";
import CategoriaForm from "../components/CategoriaForm";

function Categorias() {
    const [estado, setEstado] = useState({
        carregando: true,
        categorias: [],
        erro: "",
    });

    useEffect(() => {
        let ativo = true;

        async function carregar() {
            try {
                const response = await listarCategorias();

                if (!ativo) {
                    return;
                }

                setEstado({
                    carregando: false,
                    categorias: response.content,
                    erro: "",
                });

            } catch (error) {
                console.error(
                    "Erro ao carregar categorias:",
                    error
                );

                if (!ativo) {
                    return;
                }

                setEstado({
                    carregando: false,
                    categorias: [],
                    erro: "Não foi possível carregar as categorias.",
                });
            }
        }

        carregar();

        return () => {
            ativo = false;
        };
    }, []);

    if (estado.carregando) {
        return <p>Carregando categorias...</p>;
    }

    if (estado.erro) {
        return <p>{estado.erro}</p>;
    }

   return (
    <div className="categorias-page">

        <div className="page-header">
            <div>
                <h1>Categorias</h1>
                <p>
                    Gerencie as categorias dos produtos da sua empresa.
                </p>
            </div>
        </div>

        <section className="categoria-form-card">
            <div className="section-header">
                <div>
                    <h2>Nova categoria</h2>
                    <p>
                        Adicione uma nova categoria para organizar os seus produtos.
                    </p>
                </div>
            </div>

            <CategoriaForm
                onCriada={(categoria) => {
                    setEstado((atual) => ({
                        ...atual,
                        categorias: [
                            categoria,
                            ...atual.categorias,
                        ],
                    }));
                }}
            />
        </section>

        <section className="categorias-list-card">

            <div className="section-header">
                <div>
                    <h2>Categorias cadastradas</h2>
                    <p>
                        Lista de categorias disponíveis.
                    </p>
                </div>

                <span className="categoria-count">
                    {estado.categorias.length}
                </span>
            </div>

            {estado.categorias.length === 0 ? (
                <div className="empty-state">
                    <p>Nenhuma categoria encontrada.</p>
                </div>
            ) : (
                <div className="table-container">
                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome</th>
                            </tr>
                        </thead>

                        <tbody>
                            {estado.categorias.map((categoria) => (
                                <tr key={categoria.id}>
                                    <td>{categoria.id}</td>
                                    <td>{categoria.nome}</td>
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

export default Categorias;