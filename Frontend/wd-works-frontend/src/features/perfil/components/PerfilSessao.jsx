import { LogOut } from "lucide-react";

import { useAuth } from "../../../contexts/useAuth";
import { useNavigate } from "react-router-dom";


function PerfilSessao() {

    const { logout } = useAuth();
    const navigate = useNavigate();


    function terminar() {

        const confirmar = window.confirm(
            "Tem a certeza que deseja terminar a sessão?"
        );

        if (!confirmar) {
            return;
        }

        logout();
        navigate("/login", { replace: true });
    }


    return (

        <section className="perfil-painel perfil-painel-perigo">

            <header className="perfil-painel-header">

                <div>
                    <h3>Sessão</h3>
                    <p>
                        Termine a sua sessão neste dispositivo.
                    </p>
                </div>

            </header>


            <button
                type="button"
                className="perfil-btn perfil-btn-perigo"
                onClick={terminar}
            >
                <LogOut size={14} strokeWidth={2.4} />
                Terminar sessão
            </button>

        </section>
    );
}


export default PerfilSessao;