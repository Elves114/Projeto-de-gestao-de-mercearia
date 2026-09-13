import api from "../../../services/api";

export async function login(email, senha) {
    const response = await api.post("/auth/login", {
        email,
        senha,
    });

    return response.data;
}

export async function usuarioAutenticado() {
    const response = await api.get("/auth/me");

    return response.data;
}

export async function cadastrar(dados) {
    const response = await api.post(
        "/auth/cadastro",
        dados
    );

    return response.data;
}
export async function recuperarSenha(email) {

const response = await api.post(
    "/auth/recuperar-senha",
    {
        email
    }
);

return response.data;


}

export async function redefinirSenha(token, novaSenha) {

const response = await api.post(
    "/auth/redefinir-senha",
    {
        token,
        novaSenha
    }
);

return response.data;


}
