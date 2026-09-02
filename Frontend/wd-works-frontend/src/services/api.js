import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
});


// ===============================
// TOKEN
// ===============================

api.interceptors.request.use(
    (config) => {

        const token = localStorage.getItem("token");

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },

    (error) => {
        return Promise.reject(error);
    }
);


// ===============================
// RESPOSTA / ERROS
// ===============================

api.interceptors.response.use(

    (response) => {
        return response;
    },

    (error) => {

        const status = error.response?.status;

        // Sessão expirada
        if (
            status === 401 &&
            localStorage.getItem("token")
        ) {

            localStorage.removeItem("token");

            window.dispatchEvent(
                new Event("sessao-expirada")
            );
        }

        return Promise.reject(error);
    }
);


// ===============================
// MENSAGENS DE ERRO
// ===============================
export function obterMensagemErro(error) {

    const status = error.response?.status;

    const mensagemBackend =
        error.response?.data?.message;

    if (mensagemBackend) {
        return mensagemBackend;
    }

    switch (status) {

        case 400:
            return "Os dados enviados são inválidos.";

        case 401:
            return "A sua sessão expirou. Faça login novamente.";

        case 403:
            return "Você não tem permissão para realizar esta operação.";

        case 404:
            return "O recurso solicitado não foi encontrado.";

        case 409:
            return "Não foi possível concluir a operação porque existe um conflito.";

        case 422:
            return "Os dados enviados não puderam ser processados.";

        case 500:
            return "Ocorreu um erro interno no servidor.";

        default:
            return "Ocorreu um erro inesperado. Tente novamente.";
    }
}

export default api;