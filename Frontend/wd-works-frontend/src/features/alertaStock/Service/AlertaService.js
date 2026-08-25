import api from "../../../services/api";

// Listar todos os alertas da empresa
export async function listarAlertasStock(
    page = 0,
    size = 10
) {
    const response = await api.get(
        "/api/alertas-stock",
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
}

// Listar apenas alertas ativos
export async function listarAlertasStockAtivos(
    page = 0,
    size = 10
) {
    const response = await api.get(
        "/api/alertas-stock/ativos",
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
}

// Buscar alerta por ID
export async function buscarAlertaStockPorId(id) {
    const response = await api.get(
        `/api/alertas-stock/${id}`
    );

    return response.data;
}