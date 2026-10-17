import { useState } from "react";
import { LogOut } from "lucide-react";

import { useAuth } from "../../../contexts/useAuth";
import { useNavigate } from "react-router-dom";
import ConfirmDialog from "../../../components/ui/ConfirmDialog";


function PerfilSessao() {

    const { logout } = useAuth();
    const navigate = useNavigate();

    const [confirmOpen, setConfirmOpen] = useState(false);


    function confirmarLogout() {
        logout();
        navigate("/login", { replace: true });
    }


    return (

        <>
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
                onClick={() => setConfirmOpen(true)}
            >
                <LogOut size={14} strokeWidth={2.4} />
                Terminar sessão
            </button>

        </section>

        <ConfirmDialog
            isOpen={confirmOpen}
            title="Terminar sessão"
            message="Tem a certeza que deseja terminar a sessão?"
            confirmLabel="Terminar"
            cancelLabel="Cancelar"
            confirmVariant="danger"
            onConfirm={confirmarLogout}
            onCancel={() => setConfirmOpen(false)}
        />
        </>
    );
}


export default PerfilSessao;