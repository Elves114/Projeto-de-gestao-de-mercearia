import api from "../../../services/api";

export async function listarProdutos(page = 0, size = 10) {
    const response = await api.get("/api/produtos", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

export async function buscarProduto(id) {
    const response = await api.get(
        `/api/produtos/${id}`
    );

    return response.data;
}

export async function pesquisarProdutos(
    nome,
    page = 0,
    size = 10
) {
    const response = await api.get(
        "/api/produtos/pesquisar",
        {
            params: {
                nome,
                page,
                size,
            },
        }
    );

    return response.data;
}

export async function criarProduto(produto) {
    const response = await api.post(
        "/api/produtos",
        produto
    );

    return response.data;
}

export async function atualizarProduto(
    id,
    produto
) {
    const response = await api.put(
        `/api/produtos/${id}`,
        produto
    );

    return response.data;
}

export async function eliminarProduto(id) {
    await api.delete(
        `/api/produtos/${id}`
    );
}