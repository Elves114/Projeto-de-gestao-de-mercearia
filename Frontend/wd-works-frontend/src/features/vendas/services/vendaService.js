
import api from "../../../services/api";

export async function listarVendas(
    page = 0,
    size = 10,
    filtros = {}
) {
    const params = {
        page,
        size,
    };

    /*
     * ID da venda
     */
    if (filtros.vendaId) {
        params.vendaId = filtros.vendaId;
    }

    /*
     * Data/hora inicial
     */
    if (filtros.inicio) {
        params.inicio = filtros.inicio;
    }

    /*
     * Data/hora final
     */
    if (filtros.fim) {
        params.fim = filtros.fim;
    }

    /*
     * Vendedor
     */
    if (filtros.usuarioId) {
        params.usuarioId = filtros.usuarioId;
    }

    const response = await api.get("/api/vendas", {
        params,
    });

    return response.data;
}


export async function buscarVenda(id) {
    const response = await api.get(
        `/api/vendas/${id}`
    );

    return response.data;
}


export async function criarVenda(venda) {
    const response = await api.post(
        "/api/vendas",
        venda
    );

    return response.data;
}

