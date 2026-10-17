/**
 * Utilitário para descarregar ficheiros CSV da API.
 *
 * Como os endpoints exigem JWT, não podemos simplesmente
 * usar window.open(). Temos de fazer fetch com o header
 * Authorization e depois criar um Blob para download.
 *
 * Aceita uma função showToast opcional para notificações.
 * Se não fornecida, usa console.warn como fallback.
 */

const BASE_URL = import.meta.env.VITE_API_URL;

function notificar(msg, tipo = "error") {
    // Tenta usar o ToastContext se disponível via window
    if (window.__wdToast && typeof window.__wdToast.error === "function") {
        if (tipo === "error") {
            window.__wdToast.error(msg);
        } else if (tipo === "warning") {
            window.__wdToast.warning(msg);
        }
    } else {
        console.warn(`[exportarCsv] ${msg}`);
    }
}

export async function exportarCsv(path, params = {}, nomeFicheiro = "dados.csv") {

    const token = localStorage.getItem("token");

    if (!token) {
        notificar("Sessão expirada. Faça login novamente.");
        return;
    }

    /* Construir query string */
    const query = new URLSearchParams();

    Object.entries(params).forEach(([chave, valor]) => {
        if (valor !== undefined && valor !== null && valor !== "") {
            query.append(chave, valor);
        }
    });

    const url = `${BASE_URL}${path}${query.toString() ? "?" + query.toString() : ""}`;

    try {

        const response = await fetch(url, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        if (!response.ok) {

            if (response.status === 403) {
                notificar("Não tem permissão para exportar este relatório.");
            } else if (response.status === 401) {
                notificar("Sessão expirada. Faça login novamente.");
            } else {
                notificar("Não foi possível gerar o ficheiro.");
            }

            return;
        }

        const blob = await response.blob();

        /* Criar download */
        const blobUrl = URL.createObjectURL(blob);

        const link = document.createElement("a");
        link.href = blobUrl;
        link.download = nomeFicheiro;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        URL.revokeObjectURL(blobUrl);

    } catch (error) {
        console.error("Erro ao exportar CSV:", error);
        notificar("Erro de ligação ao servidor.");
    }
}