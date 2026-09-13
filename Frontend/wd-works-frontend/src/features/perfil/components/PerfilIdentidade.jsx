import { Building2, Mail, Shield } from "lucide-react";


function formatarPerfil(perfil) {

    switch (perfil) {
        case "ADMIN":      return "Administrador";
        case "GERENTE":    return "Gerente";
        case "FUNCIONARIO":return "Funcionário";
        default:           return perfil || "Utilizador";
    }
}


function PerfilIdentidade({ usuario, empresa, carregando }) {

    const nome = usuario?.nome || "Utilizador";
    const inicial = nome.charAt(0).toUpperCase();

    const perfil =
        formatarPerfil(usuario?.perfil);

    const nomeEmpresa =
        carregando
            ? "A carregar..."
            : empresa?.nome || "—";


    return (

        <article className="perfil-identidade">

            <div className="perfil-avatar">
                {inicial}
            </div>


            <h2 className="perfil-identidade-nome">
                {nome}
            </h2>


            <span className="perfil-identidade-badge">
                <Shield size={12} strokeWidth={2.4} />
                {perfil}
            </span>


            <div className="perfil-identidade-info">

                <div className="perfil-identidade-linha">
                    <Mail size={14} strokeWidth={2} />
                    <span>{usuario?.email || "—"}</span>
                </div>

                <div className="perfil-identidade-linha">
                    <Building2 size={14} strokeWidth={2} />
                    <span>{nomeEmpresa}</span>
                </div>

            </div>

        </article>
    );
}


export default PerfilIdentidade;