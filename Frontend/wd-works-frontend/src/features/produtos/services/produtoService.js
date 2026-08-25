import api from "../../../services/api";

export async function listarProdutos(
    page = 0,
    size = 10,
    nome = "",
    filtros = {}
) {
    const response = await api.get("/api/produtos", {
        params: {
            page,
            size,

            ...(nome.trim() && {
                nome: nome.trim(),
            }),

            ...(filtros.status && {
                status: filtros.status,
            }),

            ...(filtros.categoriaId && {
                categoriaId: filtros.categoriaId,
            }),

            ...(filtros.precoMin && {
                precoMin: filtros.precoMin,
            }),

            ...(filtros.precoMax && {
                precoMax: filtros.precoMax,
            }),

            ...(filtros.quantidadeMin && {
                quantidadeMin: filtros.quantidadeMin,
            }),

            ...(filtros.quantidadeMax && {
                quantidadeMax: filtros.quantidadeMax,
            }),
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

export async function desativarProduto(id) {
    const response = await api.patch(
        `/api/produtos/${id}/desativar`
    );

    return response.data;
}
export async function ativarProduto(id) {
    const response = await api.patch(
        `/api/produtos/${id}/ativar`
    );

    return response.data;
}
export async function pesquisarProdutos(
    nome,
    page = 0,
    size = 10
) {
    return listarProdutos(
        page,
        size,
        nome
    );
}