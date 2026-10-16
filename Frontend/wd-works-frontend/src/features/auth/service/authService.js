import api from "../../../services/api";

/* ============================================================
   TOKENS — armazenamento
   ============================================================ */

export function guardarTokens(token, refreshToken) {
    localStorage.setItem("token", token);
    if (refreshToken) {
        localStorage.setItem("refreshToken", refreshToken);
    }
}

export function limparTokens() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
}

export function getAccessToken() {
    return localStorage.getItem("token");
}

export function getRefreshToken() {
    return localStorage.getItem("refreshToken");
}

/* ============================================================
   API
   ============================================================ */

export async function login(email, senha) {
    const response = await api.post("/auth/login", { email, senha });
    const { token, refreshToken } = response.data;
    guardarTokens(token, refreshToken);
    return response.data;
}

export async function refresh() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) throw new Error("Sem refresh token");

    const response = await api.post("/auth/refresh", { refreshToken });
    const { token, refreshToken: novoRefresh } = response.data;
    guardarTokens(token, novoRefresh);
    return response.data;
}

export async function logout() {
    const refreshToken = getRefreshToken();
    try {
        if (refreshToken) {
            await api.post("/auth/logout", { refreshToken });
        }
    } catch (e) {
        console.warn("Erro ao terminar sessão no servidor.", e);
    } finally {
        limparTokens();
    }
}

export async function usuarioAutenticado() {
    const response = await api.get("/auth/me");
    return response.data;
}

export async function cadastrar(dados) {
    const response = await api.post("/auth/cadastro", dados);
    const { token, refreshToken } = response.data;
    guardarTokens(token, refreshToken);
    return response.data;
}

export async function recuperarSenha(email) {
    const response = await api.post("/auth/recuperar-senha", { email });
    return response.data;
}

export async function redefinirSenha(token, novaSenha) {
    const response = await api.post("/auth/redefinir-senha", {
        token,
        novaSenha,
    });
    return response.data;
}