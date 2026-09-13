import api from "../../../services/api";

export async function buscarDashboard(periodo = 7) {

    const response = await api.get("/api/dashboard", {
        params: {
            periodo,
        },
    });

    return response.data;
}