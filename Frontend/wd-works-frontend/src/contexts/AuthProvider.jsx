import { useEffect, useState } from "react";

import { AuthContext } from "./AuthContext";
import { usuarioAutenticado } from "../features/auth/service/authService";


export function AuthProvider({ children }) {

    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const [usuario, setUsuario] = useState(null);

    const [carregando, setCarregando] = useState(true);


    useEffect(() => {

        function tratarSessaoExpirada() {

            setToken(null);
            setUsuario(null);
        }

        window.addEventListener(
            "sessao-expirada",
            tratarSessaoExpirada
        );

        return () => {

            window.removeEventListener(
                "sessao-expirada",
                tratarSessaoExpirada
            );

        };

    }, []);


    useEffect(() => {

        async function carregarUsuario() {

            if (!token) {
                setUsuario(null);
                setCarregando(false);
                return;
            }

            try {

                const data = await usuarioAutenticado();

                setUsuario(data);

            } catch (error) {

                console.error(
                    "Não foi possível obter o usuário autenticado.",
                    error
                );

                localStorage.removeItem("token");
                setToken(null);
                setUsuario(null);

            } finally {

                setCarregando(false);

            }
        }

        carregarUsuario();

    }, [token]);


    function login(novoToken) {

        localStorage.setItem(
            "token",
            novoToken
        );

        setToken(novoToken);
    }


    function logout() {

        localStorage.removeItem("token");

        setToken(null);
        setUsuario(null);
    }


    const isAuthenticated = token !== null;


    return (
        <AuthContext.Provider
            value={{
                token,
                usuario,
                isAuthenticated,
                carregando,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}