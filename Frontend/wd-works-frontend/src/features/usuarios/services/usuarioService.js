import api from "../../../services/api";

export async function listarUsuarios(page = 0, size = 10) {
    const response = await api.get("/api/usuarios", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

export async function buscarUsuario(id) {
    const response = await api.get(`/api/usuarios/${id}`);

    return response.data;
}

export async function criarUsuario(usuario) {
    const response = await api.post(
        "/api/usuarios",
        usuario
    );

    return response.data;
}

export async function atualizarUsuario(id, usuario) {
    const response = await api.put(
        `/api/usuarios/${id}`,
        usuario
    );

    return response.data;
}

export async function ativarUsuario(id) {
    const response = await api.patch(
        `/api/usuarios/${id}/ativar`
    );

    return response.data;
}

export async function desativarUsuario(id) {
    const response = await api.patch(
        `/api/usuarios/${id}/desativar`
    );

    return response.data;
}

export async function alterarPerfil(id, perfil) {
    const response = await api.patch(
        `/api/usuarios/${id}/perfil`,
        null,
        {
            params: {
                perfil,
            },
        }
    );

    return response.data;
}

export async function pesquisarUsuarios(
    termo,
    page = 0,
    size = 10
) {

    const response = await api.get(
        "/api/usuarios/pesquisar",
        {
            params: {
                termo,
                page,
                size
            }
        }
    );

    return response.data;
}

