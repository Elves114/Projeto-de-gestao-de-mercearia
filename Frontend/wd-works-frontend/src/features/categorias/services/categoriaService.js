import api from "../../../services/api";

export async function listarCategorias(
    page = 0,
    size = 10,
    nome = ""
) {
    const response = await api.get("/api/categorias", {
        params: {
            page,
            size,
            ...(nome.trim() && { nome: nome.trim() }),
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


/* ============================================================
   CONTAGEM DE PRODUTOS POR CATEGORIA
   ============================================================
 *
 * O endpoint /api/categorias não devolve a quantidade de
 * produtos por categoria. Para obter essa contagem sem
 * alterar o backend, usamos /api/produtos com filtro
 * categoriaId e size=1 — só lemos o page.totalElements.
 *
 * Faz N pedidos (um por categoria) — aceitável até ~20.
 * Se um dia crescer muito, vale a pena criar um endpoint
 * dedicado no backend que devolva já a contagem agregada.
 */

export async function contarProdutosPorCategoria(categoriaId) {
    const response = await api.get("/api/produtos", {
        params: {
            categoriaId,
            page: 0,
            size: 1,
        },
    });

    return response.data.page?.totalElements ?? 0;
}