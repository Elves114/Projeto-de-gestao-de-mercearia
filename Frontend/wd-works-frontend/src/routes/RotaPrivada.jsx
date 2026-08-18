import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../contexts/useAuth";

function RotaPrivada() {

    const {
        isAuthenticated,
        carregando
    } = useAuth();


    if (carregando) {
        return <p>Carregando...</p>;
    }


    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }


    return <Outlet />;
}

export default RotaPrivada;