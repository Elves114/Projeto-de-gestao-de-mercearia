import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 10 },
        { duration: '20s', target: 25 },
        { duration: '20s', target: 50 },
        { duration: '20s', target: 50 },
        { duration: '10s', target: 0 },
    ],

    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000'],
    },
};

const BASE_URL = 'http://localhost:8080';

export default function () {

    const email = "Arnold@wdworks.com"
    const senha = "1234qwer"

    // =========================
    // LOGIN
    // =========================

    const loginPayload = JSON.stringify({
        email: email,
        senha: senha,
    });

    const loginParams = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const loginResponse = http.post(
        `${BASE_URL}/auth/login`,
        loginPayload,
        loginParams
    );

    const loginOk = check(loginResponse, {
        'login status 200': (r) => r.status === 200,
        'login retornou token': (r) => {
            try {
                return r.json('token') !== undefined;
            } catch (e) {
                return false;
            }
        },
    });

    if (!loginOk) {
        console.log(`Login falhou: ${loginResponse.status}`);
        return;
    }

    const token = loginResponse.json('token');

    // =========================
    // /AUTH/ME
    // =========================

    const meResponse = http.get(
        `${BASE_URL}/auth/me`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    check(meResponse, {
        '/auth/me status 200': (r) => r.status === 200,
    });

    sleep(1);
}