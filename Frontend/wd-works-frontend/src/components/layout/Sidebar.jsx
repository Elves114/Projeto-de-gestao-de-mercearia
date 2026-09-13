import { NavLink } from "react-router-dom";
import {
    LayoutDashboard,
    ShoppingCart,
    Package,
    Tags,
    Warehouse,
    ArrowLeftRight,
    AlertTriangle,
    Users,
    Building2,
    ScrollText,
} from "lucide-react";
import { useAuth } from "../../contexts/useAuth";
import { PERMISSOES_ROTAS } from "../../config/Permissions";
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
                    icon: LayoutDashboard,
                },
            ],
        },

        {
            titulo: "Gestão",
            links: [
                {
                    to: "/vendas",
                    label: "Vendas",
                    icon: ShoppingCart,
                },
                {
                    to: "/produtos",
                    label: "Produtos",
                    icon: Package,
                },
                {
                    to: "/categorias",
                    label: "Categorias",
                    icon: Tags,
                },
                {
                    to: "/estoque",
                    label: "Stock",
                    icon: Warehouse,
                },
                {
                    to: "/estoque/movimentos",
                    label: "Movimentos",
                    icon: ArrowLeftRight,
                },
                {
                    to: "/alertas-stock",
                    label: "Alertas de Stock",
                    icon: AlertTriangle,
                },
            ],
        },

        {
            titulo: "Administração",
            links: [
                {
                    to: "/usuarios",
                    label: "Usuários",
                    icon: Users,
                },
                {
                    to: "/empresa",
                    label: "Empresa",
                    icon: Building2,
                },
                {
                    to: "/auditoria",
                    label: "Auditoria",
                    icon: ScrollText,
                },
            ],
        },
    ];

    const gruposPermitidos = grupos
        .map((grupo) => ({
            ...grupo,
            links: grupo.links.filter((link) => {
                const permissoes = PERMISSOES_ROTAS[link.to];

                if (!permissoes) {
                    return false;
                }

                return permissoes.includes(usuario?.perfil);
            }),
        }))
        .filter((grupo) => grupo.links.length > 0);

    return (
        <aside
            className={`sidebar ${aberto ? "open" : ""}`}
        >

            {/* ==================================================
                MARCA / BOTÃO DO SIDEBAR
               ================================================== */}

            <button
                type="button"
                className="sidebar-brand"
                onClick={() => setAberto(!aberto)}
                aria-label={
                    aberto
                        ? "Fechar menu"
                        : "Abrir menu"
                }
                aria-expanded={aberto}
            >

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

            </button>


            {/* ==================================================
                NAVEGAÇÃO
               ================================================== */}

            <nav className="sidebar-nav">

                {gruposPermitidos.map((grupo) => (

                    <div
                        className="sidebar-group"
                        key={grupo.titulo}
                    >

                        <span className="sidebar-group-title">
                            {grupo.titulo}
                        </span>


                        <div className="sidebar-group-links">

                            {grupo.links.map((link) => {

                                const Icone = link.icon;

                                return (
                                    <NavLink
                                        key={link.to}
                                        to={link.to}
                                        title={
                                            !aberto
                                                ? link.label
                                                : undefined
                                        }
                                        className={({ isActive }) =>
                                            isActive
                                                ? "sidebar-link active"
                                                : "sidebar-link"
                                        }
                                    >

                                        <span className="sidebar-link-icon">
                                            <Icone
                                                size={18}
                                                strokeWidth={2}
                                            />
                                        </span>

                                        <span className="sidebar-link-label">
                                            {link.label}
                                        </span>

                                    </NavLink>
                                );
                            })}

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