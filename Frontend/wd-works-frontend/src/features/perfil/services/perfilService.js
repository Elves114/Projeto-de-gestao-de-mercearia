import api from "../../../services/api";


/* ============================================================
   ALTERAR A MINHA SENHA
   ============================================================ */

export async function alterarMinhaSenha(
    senhaAtual,
    novaSenha
) {
    await api.patch("/api/usuarios/me/senha", {
        senhaAtual,
        novaSenha,
    });
}


/* ============================================================
   DADOS DA MINHA EMPRESA
   ============================================================ */

export async function buscarMinhaEmpresa() {
    const response = await api.get("/api/empresas/minha");
    return response.data;
}


/* ============================================================
   ÚLTIMAS AÇÕES (auditoria do próprio)
   ============================================================ */

export async function buscarMinhasAcoes(
    page = 0,
    size = 5
) {
    const response = await api.get(
        "/api/auditorias/minhas",
        { params: { page, size } }
    );

    return response.data;
}