import http from 'k6/http';

export default function () {

    const payload = JSON.stringify({
        email: 'Arnold@wdworks.com',
        senha: '1234qwer',
    });

    const response = http.post(
        'http://localhost:8080/auth/login',
        payload,
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    console.log(`STATUS: ${response.status}`);
    console.log(`BODY: ${response.body}`);
}