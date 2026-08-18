import { useState } from "react";
import { criarCategoria } from "../services/categoriaService";

function CategoriaForm({ onCriada }) {
    const [nome, setNome] = useState("");
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState("");

    async function handleSubmit(event) {
        event.preventDefault();

        if (!nome.trim()) {
            setErro("O nome da categoria é obrigatório.");
            return;
        }

        try {
            setCarregando(true);
            setErro("");

            const categoria = await criarCategoria(nome.trim());

            setNome("");

            onCriada(categoria);

        } catch (error) {
            console.error(
                "Erro ao criar categoria:",
                error
            );

            if (error.response?.status === 403) {
                setErro(
                    "Você não possui permissão para criar categorias."
                );
            } else if (error.response?.status === 400) {
                setErro(
                    error.response.data?.message ||
                    "Dados da categoria inválidos."
                );
            } else {
                setErro(
                    "Não foi possível criar a categoria."
                );
            }
        } finally {
            setCarregando(false);
        }
    }

    return (
    <form className="categoria-form" onSubmit={handleSubmit}>

        <div className="form-group">
            <label htmlFor="nome">
                Nome da categoria
            </label>

            <input
                id="nome"
                type="text"
                value={nome}
                onChange={(event) =>
                    setNome(event.target.value)
                }
                placeholder="Ex: Bebidas"
                disabled={carregando}
            />
        </div>

        {erro && (
            <p className="form-error">
                {erro}
            </p>
        )}

        <button
            className="button-primary"
            type="submit"
            disabled={carregando}
        >
            {carregando
                ? "Criando..."
                : "Criar categoria"}
        </button>

    </form>
);
}

export default CategoriaForm;