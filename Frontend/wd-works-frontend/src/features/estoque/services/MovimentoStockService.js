import api from "../../../services/api";

// Criar movimento de stock
export async function criarMovimentoStock(movimento) {
    const response = await api.post(
        "/api/movimentos-stock",
        movimento
    );

    return response.data;
}

// Listar movimentos da empresa
export async function listarMovimentosStock(
    page = 0,
    size = 10,
    produto = ""
) {
    const response = await api.get(
        "/api/movimentos-stock",
        {
            params: {
                page,
                size,
                produto,
            },
        }
    );

    return response.data;
}

// Listar histórico de movimentos de um produto específico
export async function listarMovimentosPorProduto(
    produtoId,
    page = 0,
    size = 10
) {
    const response = await api.get(
        `/api/movimentos-stock/produto/${produtoId}`,
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
}