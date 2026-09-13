import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 100 },
        { duration: '30s', target: 250 },
        { duration: '30s', target: 400 },
        { duration: '30s', target: 500 },
        { duration: '30s', target: 650 },
        { duration: '30s', target: 750 },
        { duration: '30s', target: 900 },
        { duration: '30s', target: 0 },
    ],

    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000'],
    },
};

const BASE_URL = 'http://localhost:8080';

const PRODUTO_ID = 3;
const CATEGORIA_ID = 3;

let token;

// ==========================================
// LOGIN — UMA VEZ POR VU
// ==========================================

export function setup() {

    const loginPayload = JSON.stringify({
        email: 'Arnold@wdworks.com',
        senha: '1234qwer',
    });

    const loginResponse = http.post(
        `${BASE_URL}/auth/login`,
        loginPayload,
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    check(loginResponse, {
        'LOGIN - status 200': (r) => r.status === 200,
        'LOGIN - retornou token': (r) => {
            try {
                return r.json('token') !== undefined;
            } catch (e) {
                return false;
            }
        },
    });

    if (loginResponse.status !== 200) {
        throw new Error(
            `Login falhou. Status: ${loginResponse.status}`
        );
    }

    return loginResponse.json('token');
}


// ==========================================
// TESTE PRINCIPAL
// ==========================================

export default function (data) {

    const authParams = {
        headers: {
            Authorization: `Bearer ${data}`,
        },
    };

    // ==========================================
    // 1. LISTAR PRODUTOS
    // ==========================================

    const produtosResponse = http.get(
        `${BASE_URL}/api/produtos?page=0&size=20`,
        authParams
    );

    check(produtosResponse, {
        'PRODUTOS - status 200': (r) => r.status === 200,
    });


    // ==========================================
    // 2. BUSCAR PRODUTO POR ID
    // ==========================================

    const produtoResponse = http.get(
        `${BASE_URL}/api/produtos/${PRODUTO_ID}`,
        authParams
    );

    check(produtoResponse, {
        'PRODUTO ID - status 200': (r) => r.status === 200,
    });


    // ==========================================
    // 3. PESQUISAR PRODUTOS POR NOME
    // ==========================================

    const pesquisaResponse = http.get(
        `${BASE_URL}/api/produtos?nome=a&page=0&size=20`,
        authParams
    );

    check(pesquisaResponse, {
        'PRODUTOS PESQUISA - status 200': (r) => r.status === 200,
    });


    // ==========================================
    // 4. FILTRAR PRODUTOS POR STATUS
    // ==========================================

    const statusResponse = http.get(
        `${BASE_URL}/api/produtos?status=ATIVO&page=0&size=20`,
        authParams
    );

    check(statusResponse, {
        'PRODUTOS STATUS - status 200': (r) => r.status === 200,
    });


    // ==========================================
    // 5. LISTAR CATEGORIAS
    // ==========================================

    const categoriasResponse = http.get(
        `${BASE_URL}/api/categorias?page=0&size=20`,
        authParams
    );

    check(categoriasResponse, {
        'CATEGORIAS - status 200': (r) => r.status === 200,
    });


    // ==========================================
    // 6. PESQUISAR CATEGORIAS
    // ==========================================

    const categoriaPesquisaResponse = http.get(
        `${BASE_URL}/api/categorias?nome=a&page=0&size=20`,
        authParams
    );

    check(categoriaPesquisaResponse, {
        'CATEGORIAS PESQUISA - status 200': (r) => r.status === 200,
    });


    // ==========================================
    // 7. BUSCAR CATEGORIA POR ID
    // ==========================================

    const categoriaResponse = http.get(
        `${BASE_URL}/api/categorias/${CATEGORIA_ID}`,
        authParams
    );

    check(categoriaResponse, {
        'CATEGORIA ID - status 200': (r) => r.status === 200,
    });


    sleep(1);
}