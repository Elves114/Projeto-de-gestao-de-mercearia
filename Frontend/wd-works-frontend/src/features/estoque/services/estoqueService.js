import api from "../../../services/api";

// Listar estoque da empresa do usuário autenticado
export async function listarEstoque(page = 0, size = 10) {
    const response = await api.get("/api/estoques", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

// Buscar estoque por ID
export async function buscarEstoquePorId(id) {
    const response = await api.get(`/api/estoques/${id}`);

    return response.data;
}

// Buscar estoque de um produto
export async function buscarEstoquePorProduto(produtoId) {
    const response = await api.get(
        `/api/estoques/produto/${produtoId}`
    );

    return response.data;
}