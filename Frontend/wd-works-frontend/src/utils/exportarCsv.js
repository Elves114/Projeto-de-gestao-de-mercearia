/**
 * Utilitário para descarregar ficheiros CSV da API.
 *
 * Como os endpoints exigem JWT, não podemos simplesmente
 * usar window.open(). Temos de fazer fetch com o header
 * Authorization e depois criar um Blob para download.
 */

const BASE_URL = import.meta.env.VITE_API_URL;

export async function exportarCsv(path, params = {}, nomeFicheiro = "dados.csv") {

    const token = localStorage.getItem("token");

    if (!token) {
        alert("Sessão expirada. Faça login novamente.");
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
                alert("Não tem permissão para exportar este relatório.");
            } else if (response.status === 401) {
                alert("Sessão expirada. Faça login novamente.");
            } else {
                alert("Não foi possível gerar o ficheiro.");
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
        alert("Erro de ligação ao servidor.");
    }
}