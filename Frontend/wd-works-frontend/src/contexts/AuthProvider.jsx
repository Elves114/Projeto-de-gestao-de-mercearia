import { useEffect, useRef, useState } from "react";
import { AuthContext } from "./AuthContext";
import {
    usuarioAutenticado,
    logout as logoutApi,
    limparTokens,
} from "../features/auth/service/authService";


export function AuthProvider({ children }) {
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [usuario, setUsuario] = useState(null);
    const [carregando, setCarregando] = useState(true);

    // 🛡 Guard contra duplo pedido do StrictMode
    const pedidoEmCurso = useRef(null);

    /* Sessão expirada (evento global) */
    useEffect(() => {
        function tratarSessaoExpirada() {
            setToken(null);
            setUsuario(null);
        }
        window.addEventListener("sessao-expirada", tratarSessaoExpirada);
        return () => {
            window.removeEventListener("sessao-expirada", tratarSessaoExpirada);
        };
    }, []);

    /* Carregar utilizador */
    useEffect(() => {
        let ativo = true;

        async function carregarUsuario() {
            if (!token) {
                setUsuario(null);
                setCarregando(false);
                return;
            }

            // 🛡 Se já há um pedido em curso, não disparar outro
            if (pedidoEmCurso.current) {
                try {
                    const data = await pedidoEmCurso.current;
                    if (ativo) setUsuario(data);
                } catch {
                    // já tratado abaixo
                } finally {
                    if (ativo) setCarregando(false);
                }
                return;
            }

            try {
                setCarregando(true);

                const promessa = usuarioAutenticado();
                pedidoEmCurso.current = promessa;

                const data = await promessa;
                if (ativo) setUsuario(data);

            } catch (error) {
                console.error("Falha ao carregar usuário.", error);

                // 🧹 403 ou 401 → sessão inválida
                const status = error.response?.status;
                if (status === 401 || status === 403) {
                    limparTokens();
                    if (ativo) {
                        setToken(null);
                        setUsuario(null);
                    }
                }

            } finally {
                pedidoEmCurso.current = null;
                if (ativo) setCarregando(false);
            }
        }

        carregarUsuario();

        return () => {
            ativo = false;
        };
    }, [token]);

    function login(novoToken) {
        localStorage.setItem("token", novoToken);
        setToken(novoToken);
    }

    async function logout() {
        await logoutApi();
        setToken(null);
        setUsuario(null);
    }

    function atualizarUsuarioLocal(dados) {
        setUsuario((atual) => ({
            ...(atual || {}),
            ...dados,
        }));
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
                atualizarUsuarioLocal,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}