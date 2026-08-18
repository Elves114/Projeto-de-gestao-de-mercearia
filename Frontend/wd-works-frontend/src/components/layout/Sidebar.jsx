import { NavLink } from "react-router-dom";

function Sidebar() {

    const links = [
        { to: "/dashboard", label: "Dashboard" },
        { to: "/empresa", label: "Empresa" },
        { to: "/categorias", label: "Categorias" },
        { to: "/produtos", label: "Produtos" },
        { to: "/estoque", label: "Stock" },
        { to: "/vendas", label: "Vendas" },
        { to: "/usuarios", label: "Usuários" },
        { to: "/auditoria", label: "Auditoria" },
    ];

    return (
        <aside className="sidebar">

            <div className="sidebar-brand">
                <h2>WD Works</h2>
                <span>Gestão empresarial</span>
            </div>

            <nav className="sidebar-nav">

                {links.map((link) => (
                    <NavLink
                        key={link.to}
                        to={link.to}
                        className={({ isActive }) =>
                            isActive
                                ? "sidebar-link active"
                                : "sidebar-link"
                        }
                    >
                        {link.label}
                    </NavLink>
                ))}

            </nav>

        </aside>
    );
}

export default Sidebar;