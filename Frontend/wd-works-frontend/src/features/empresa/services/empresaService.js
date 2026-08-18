import api from "../../../services/api";

export async function buscarMinhaEmpresa() {
    const response = await api.get("/api/empresas/minha");

    return response.data;
}

export async function atualizarMinhaEmpresa(empresa) {
    const response = await api.put(
        "/api/empresas/minha",
        empresa
    );

    return response.data;
}

export async function ativarMinhaEmpresa() {
    const response = await api.patch(
        "/api/empresas/minha/ativar"
    );

    return response.data;
}

export async function desativarMinhaEmpresa() {
    const response = await api.patch(
        "/api/empresas/minha/desativar"
    );

    return response.data;
}