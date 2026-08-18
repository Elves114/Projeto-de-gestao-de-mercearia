import api from "../../../services/api";

export async function criarMovimentoStock(movimento) {
    const response = await api.post(
        "/api/movimentos-stock",
        movimento
    );

    return response.data;
}