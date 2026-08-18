import api from "../../../services/api";

export async function listarVendas(page = 0, size = 10) {
    const response = await api.get("/api/vendas", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

export async function buscarVenda(id) {
    const response = await api.get(`/api/vendas/${id}`);

    return response.data;
}

export async function criarVenda(venda) {
    const response = await api.post("/api/vendas", venda);

    return response.data;
}