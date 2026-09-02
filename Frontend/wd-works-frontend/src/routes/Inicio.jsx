import { Navigate } from "react-router-dom";
import { useAuth } from "../contexts/useAuth";

function Inicio() {

    const { usuario, carregando } = useAuth();

    if (carregando) {
        return <p>Carregando...</p>;
    }

    if (usuario?.perfil === "FUNCIONARIO") {
        return <Navigate to="/produtos" replace />;
    }

    return <Navigate to="/dashboard" replace />;
}

export default Inicio;