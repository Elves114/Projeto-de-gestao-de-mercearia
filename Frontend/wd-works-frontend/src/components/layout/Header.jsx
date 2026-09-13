
import { useAuth } from "../../contexts/useAuth";
import { Link, useLocation } from "react-router-dom";
import "../style/Header.css";

function Header() {

    const location = useLocation();

    const { usuario } = useAuth();

    const paginaAtual = obterNomePagina(
        location.pathname
    );


    const nomeUsuario =
        usuario?.nome || "Utilizador";

    const perfilUsuario =
        formatarPerfil(
            usuario?.perfil
        );

    const inicial =
        nomeUsuario
            .charAt(0)
            .toUpperCase();


    return (
        <header className="header">

            <div className="header-left">

                <div className="header-page-info">

                    <span className="header-section">
                        WD Works
                    </span>

                    <h1>
                        {paginaAtual}
                    </h1>

                </div>

            </div>


            <div className="header-right">

                <div className="header-search">

                    <span className="header-search-icon">
                        ⌕
                    </span>

                    <input
                        type="text"
                        placeholder="Pesquisar..."
                        aria-label="Pesquisar"
                    />

                </div>


                <button
                    type="button"
                    className="header-notification"
                    aria-label="Notificações"
                >

                    <span className="notification-icon">
                        ♢
                    </span>

                </button>


                <div className="header-divider" />


                                <Link
                    to="/perfil"
                    className="header-user"
                    aria-label="Ir para o meu perfil"
                >

                    <div className="header-user-avatar">
                        {inicial}
                    </div>

                    <div className="header-user-info">

                        <strong>
                            {nomeUsuario}
                        </strong>

                        <span>
                            {perfilUsuario}
                        </span>

                    </div>

                    <span className="header-user-arrow">
                        ▾
                    </span>

                </Link>

            </div>

        </header>
    );
}


function formatarPerfil(perfil) {

    if (!perfil) {
        return "Utilizador";
    }

    switch (perfil) {

        case "ADMIN":
            return "Administrador";

        case "GERENTE":
            return "Gerente";

        case "FUNCIONARIO":
            return "Funcionário";

        default:
            return perfil;
    }
}


function obterNomePagina(pathname) {

    if (pathname === "/dashboard") {
        return "Dashboard";
    }

    if (pathname === "/empresa") {
        return "Empresa";
    }

    if (pathname === "/categorias") {
        return "Categorias";
    }

    if (pathname === "/produtos") {
        return "Produtos";
    }

    if (pathname === "/estoque") {
        return "Stock";
    }

    if (pathname === "/estoque/entrada") {
        return "Entrada de Stock";
    }

    if (pathname === "/estoque/movimentos") {
        return "Movimentos de Stock";
    }

    if (pathname === "/alertas-stock") {
        return "Alertas de Stock";
    }

    if (pathname === "/vendas") {
        return "Vendas";
    }

    if (pathname === "/vendas/nova") {
        return "Nova Venda";
    }

    if (pathname === "/usuarios") {
        return "Usuários";
    }

    if (pathname === "/auditoria") {
        return "Auditoria";
    }

    return "Painel de gestão";
}


export default Header;
