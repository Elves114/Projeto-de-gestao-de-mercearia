
import api from "../../../services/api";

export async function buscarDashboard() {

    const response = await api.get("/api/dashboard");

    return response.data;
}

