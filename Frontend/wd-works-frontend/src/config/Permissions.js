export const PERMISSOES_ROTAS = {
    "/dashboard": [
        "ADMIN",
        "GERENTE"
    ],

    "/vendas": [
        "ADMIN",
        "GERENTE",
        "FUNCIONARIO"
    ],

    "/vendas/nova": [
        "ADMIN",
        "GERENTE",
        "FUNCIONARIO"
    ],

    "/produtos": [
        "ADMIN",
        "GERENTE",
        "FUNCIONARIO"
    ],

    "/categorias": [
        "ADMIN",
        "GERENTE"
    ],

    "/estoque": [
        "ADMIN",
        "GERENTE",
        "FUNCIONARIO"
    ],

    "/estoque/movimentos": [
        "ADMIN",
        "GERENTE"
    ],

    "/estoque/entrada": [
        "ADMIN",
        "GERENTE"
    ],

    "/alertas-stock": [
        "ADMIN",
        "GERENTE"
    ],

    "/usuarios": [
        "ADMIN"
    ],

    "/empresa": [
        "ADMIN"
    ],

    "/auditoria": [
        "ADMIN"
    ],
    "/vendas/:id": [
        "ADMIN",
        "GERENTE",
        "FUNCIONARIO"
    ],

    "/usuarios/novo": [
        "ADMIN"
    ],

    "/usuarios/:id/editar": [
        "ADMIN"
    ],

        "/perfil": [
        "ADMIN",
        "GERENTE",
        "FUNCIONARIO"
    ],
};