import { Building2, MapPin, Phone, Mail, Hash } from "lucide-react";


function PerfilEmpresa({ empresa, carregando }) {

    if (carregando) {
        return (
            <section className="perfil-painel">
                <header className="perfil-painel-header">
                    <div>
                        <h3>Empresa</h3>
                        <p>A carregar dados da empresa...</p>
                    </div>
                </header>
            </section>
        );
    }

    if (!empresa) {
        return (
            <section className="perfil-painel">
                <header className="perfil-painel-header">
                    <div>
                        <h3>Empresa</h3>
                        <p>Não foi possível carregar.</p>
                    </div>
                </header>
            </section>
        );
    }


    return (

        <section className="perfil-painel">

            <header className="perfil-painel-header">

                <div>
                    <h3>Empresa</h3>
                    <p>A empresa à qual a sua conta pertence.</p>
                </div>

            </header>


            <div className="perfil-campos">

                <div className="perfil-campo perfil-campo-largo">
                    <span>
                        <Building2 size={11} strokeWidth={2.4} />
                        Nome
                    </span>
                    <strong>{empresa.nome}</strong>
                </div>

                <div className="perfil-campo">
                    <span>
                        <Hash size={11} strokeWidth={2.4} />
                        NUIT
                    </span>
                    <strong>{empresa.nuit}</strong>
                </div>

                <div className="perfil-campo">
                    <span>
                        <Mail size={11} strokeWidth={2.4} />
                        Email
                    </span>
                    <strong>{empresa.email}</strong>
                </div>

                <div className="perfil-campo">
                    <span>
                        <Phone size={11} strokeWidth={2.4} />
                        Contacto
                    </span>
                    <strong>{empresa.contacto}</strong>
                </div>

                <div className="perfil-campo perfil-campo-largo">
                    <span>
                        <MapPin size={11} strokeWidth={2.4} />
                        Endereço
                    </span>
                    <strong>{empresa.endereco || "—"}</strong>
                </div>

            </div>

        </section>
    );
}


export default PerfilEmpresa;