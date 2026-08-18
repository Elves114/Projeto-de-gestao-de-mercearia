import api from "../../../services/api";

export async function listarCategorias(page = 0, size = 10) {
    const response = await api.get("/api/categorias", {
        params: {
            page,
            size,
        },
    });

    return response.data;
}

export async function buscarCategoria(id) {
    const response = await api.get(`/api/categorias/${id}`);

    return response.data;
}

export async function criarCategoria(nome) {
    const response = await api.post("/api/categorias", {
        nome,
    });

    return response.data;
}

export async function atualizarCategoria(id, nome) {
    const response = await api.put(`/api/categorias/${id}`, {
        nome,
    });

    return response.data;
}

export async function eliminarCategoria(id) {
    await api.delete(`/api/categorias/${id}`);
}