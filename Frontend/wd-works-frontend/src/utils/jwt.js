export function decodificarToken(token) {
    if (!token) {
        return null;
    }

    try {
        const payload = token.split(".")[1];

        const base64 = payload
            .replace(/-/g, "+")
            .replace(/_/g, "/");

        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map(
                    (char) =>
                        "%" +
                        ("00" + char.charCodeAt(0).toString(16)).slice(-2)
                )
                .join("")
        );

        return JSON.parse(jsonPayload);
    } catch {
        console.error("Não foi possível decodificar o token.");
        return null;
    }
}