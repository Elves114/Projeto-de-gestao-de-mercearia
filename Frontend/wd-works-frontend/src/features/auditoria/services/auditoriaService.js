import api from "../../../services/api";

export async function listarAuditorias(page = 0, size = 10) {
    const response = await api.get("/api/auditorias", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

export async function listarMinhasAuditorias(page = 0, size = 10) {
    const response = await api.get("/api/auditorias/minhas", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

export async function listarAuditoriasPorUsuario(
    usuarioId,
    page = 0,
    size = 10
) {
    const response = await api.get(
        `/api/auditorias/usuario/${usuarioId}`,
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
}

export async function listarAuditoriasPorTipo(
    tipo,
    page = 0,
    size = 10
) {
    const response = await api.get(
        `/api/auditorias/tipo/${tipo}`,
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
}

export async function listarAuditoriasPorGravidade(
    gravidade,
    page = 0,
    size = 10
) {
    const response = await api.get(
        `/api/auditorias/gravidade/${gravidade}`,
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
}