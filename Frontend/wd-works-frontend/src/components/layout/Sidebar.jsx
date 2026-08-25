import { NavLink } from "react-router-dom";
import { useAuth } from "../../contexts/useAuth";
import "../style/Sidebar.css";

function Sidebar({ aberto, setAberto }) {

    const { usuario } = useAuth();

    const nomeUsuario =
        usuario?.nome || "Utilizador";

    const perfilUsuario =
        formatarPerfil(usuario?.perfil);

    const inicial =
        nomeUsuario
            .charAt(0)
            .toUpperCase();


    const grupos = [
        {
            titulo: "Principal",
            links: [
                {
                    to: "/dashboard",
                    label: "Dashboard",
                    icon: "⌂"
                }
            ]
        },

        {
            titulo: "Gestão",
            links: [
                {
                    to: "/vendas",
                    label: "Vendas",
                    icon: "▣"
                },
                {
                    to: "/produtos",
                    label: "Produtos",
                    icon: "□"
                },
                {
                    to: "/categorias",
                    label: "Categorias",
                    icon: "▦"
                },
                {
                    to: "/estoque",
                    label: "Stock",
                    icon: "▤"
                },
                {
                    to: "/estoque/movimentos",
                    label: "Movimentos",
                    icon: "↕"
                },
                {
                    to: "/alertas-stock",
                    label: "Alertas de Stock",
                    icon: "!"
                }
            ]
        },

        {
            titulo: "Administração",
            links: [
                {
                    to: "/usuarios",
                    label: "Usuários",
                    icon: "♙"
                },
                {
                    to: "/empresa",
                    label: "Empresa",
                    icon: "▥"
                },
                {
                    to: "/auditoria",
                    label: "Auditoria",
                    icon: "◷"
                }
            ]
        }
    ];


    return (
        <aside
            className={`sidebar ${aberto ? "open" : ""}`}
            onClick={() => {
                if (!aberto) {
                    setAberto(true);
                }
            }}
        >

            {/* ==================================================
                MARCA
               ================================================== */}

            <button
                className="sidebar-toggle"
                onClick={(e) => {
                    e.stopPropagation();
                    setAberto(!aberto);
                }}
                aria-label="Abrir ou fechar menu"
            >
                ☰
            </button>


            <div className="sidebar-brand">

                <div className="sidebar-logo">
                    W
                </div>

                <div className="sidebar-brand-text">

                    <strong>
                        WD Works
                    </strong>

                    <span>
                        Gestão empresarial
                    </span>

                </div>

            </div>


            {/* ==================================================
                NAVEGAÇÃO
               ================================================== */}

            <nav className="sidebar-nav">

                {grupos.map((grupo) => (

                    <div
                        className="sidebar-group"
                        key={grupo.titulo}
                    >

                        <span className="sidebar-group-title">
                            {grupo.titulo}
                        </span>


                        <div className="sidebar-group-links">

                            {grupo.links.map((link) => (

                                <NavLink
                                    key={link.to}
                                    to={link.to}
                                    className={({ isActive }) =>
                                        isActive
                                            ? "sidebar-link active"
                                            : "sidebar-link"
                                    }
                                >

                                    <span className="sidebar-link-icon">
                                        {link.icon}
                                    </span>

                                    <span>
                                        {link.label}
                                    </span>

                                </NavLink>

                            ))}

                        </div>

                    </div>

                ))}

            </nav>


            {/* ==================================================
                UTILIZADOR
               ================================================== */}

            <div className="sidebar-user">

                <div className="sidebar-user-avatar">
                    {inicial}
                </div>

                <div className="sidebar-user-info">

                    <strong>
                        {nomeUsuario}
                    </strong>

                    <span>
                        {perfilUsuario}
                    </span>

                </div>

            </div>

        </aside>
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


export default Sidebar;