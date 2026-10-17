import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
});

/* ============================================================
   URLS QUE NÃO DEVEM TENTAR REFRESH
   ============================================================ */

const URLS_SEM_REFRESH = [
    "/auth/login",
    "/auth/refresh",
    "/auth/cadastro",
    "/auth/me",       
];

/* ============================================================
   ESTADO DO REFRESH (promise compartilhada)
   ============================================================ */

let aRefrescar = null;

/* ============================================================
   REQUEST — adiciona o access token
   ============================================================ */

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

/* ============================================================
   RESPONSE — trata 401 com renovação automática
   ============================================================ */

api.interceptors.response.use(

    (response) => response,

    async (error) => {

        const originalRequest = error.config;
        const status = error.response?.status;

        /*
         * Não tenta renovar se:
         *   - Não é 401
         *   - Já tentou renovar este pedido
         *   - É um endpoint de auth
         */
        if (
            status !== 401 ||
            originalRequest._retry ||
            URLS_SEM_REFRESH.some((u) => originalRequest.url?.includes(u))
        ) {
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        try {

            /* Se já está a renovar, espera pela mesma Promise */
            if (!aRefrescar) {

                const refreshToken = localStorage.getItem("refreshToken");

                if (!refreshToken) {
                    throw new Error("Sem refresh token");
                }

                aRefrescar = axios.post(
                    `${import.meta.env.VITE_API_URL}/auth/refresh`,
                    { refreshToken }
                ).then((response) => {

                    const { token, refreshToken: novoRefresh } = response.data;

                    localStorage.setItem("token", token);
                    localStorage.setItem("refreshToken", novoRefresh);

                    return token;

                }).finally(() => {
                    aRefrescar = null;
                });
            }

            const novoToken = await aRefrescar;

            /* Reenvia o pedido original com o novo token */
            originalRequest.headers.Authorization = `Bearer ${novoToken}`;

            return api(originalRequest);

        } catch (refreshError) {

            /* Falhou: limpa tudo e avisa */
            localStorage.removeItem("token");
            localStorage.removeItem("refreshToken");

            window.dispatchEvent(new Event("sessao-expirada"));

            return Promise.reject(refreshError);
        }
    }
);

/* ============================================================
   MENSAGENS DE ERRO
   ============================================================ */

export function obterMensagemErro(error) {

    const status = error.response?.status;
    const mensagemBackend = error.response?.data?.mensagem;

    if (mensagemBackend) return mensagemBackend;

    switch (status) {
        case 400: return "Os dados enviados são inválidos.";
        case 401: return "A sua sessão expirou. Faça login novamente.";
        case 403: return "Não tem permissão para esta operação.";
        case 404: return "Recurso não encontrado.";
        case 409: return "Conflito. Tente novamente.";
        case 422: return "Dados não processáveis.";
        case 429: return "Demasiados pedidos. Tente novamente.";
        case 500: return "Erro interno do servidor.";
        default: return "Erro inesperado. Tente novamente.";
    }
}

export default api;