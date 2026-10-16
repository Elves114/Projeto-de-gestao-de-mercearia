import api from "../../../services/api";

export async function buscarFechoCaixa(data) {
    const response = await api.get("/api/caixa/fecho", {
        params: data ? { data } : {},
    });
    return response.data;
}