import { Navigate, Outlet, useLocation } from "react-router-dom";

import { useAuth } from "../contexts/useAuth";
import { PERMISSOES_ROTAS } from "../config/Permissions";

function RotaPorPerfil() {

    const { usuario } = useAuth();
    const location = useLocation();

    const perfil = usuario?.perfil;

    if (!perfil) {
        return <Navigate to="/dashboard" replace />;
    }

    const permissoes = obterPermissoes(location.pathname);

    // Rota sem configuração
    if (!permissoes) {
        return <Navigate to="/dashboard" replace />;
    }

    // Perfil sem permissão
    if (!permissoes.includes(perfil)) {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
}


function obterPermissoes(pathname) {

    // Primeiro tenta encontrar a rota exatamente
    if (PERMISSOES_ROTAS[pathname]) {
        return PERMISSOES_ROTAS[pathname];
    }

    // Depois verifica rotas dinâmicas
    const rota = Object.keys(PERMISSOES_ROTAS).find((rotaConfigurada) => {

        if (!rotaConfigurada.includes(":")) {
            return false;
        }

        const partesRota = rotaConfigurada.split("/");
        const partesPath = pathname.split("/");

        if (partesRota.length !== partesPath.length) {
            return false;
        }

        return partesRota.every((parte, index) => {

            if (parte.startsWith(":")) {
                return true;
            }

            return parte === partesPath[index];
        });
    });

    return rota
        ? PERMISSOES_ROTAS[rota]
        : null;
}

export default RotaPorPerfil;