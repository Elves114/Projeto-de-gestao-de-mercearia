import {
    Plus,
    Pencil,
    X,
    LogIn,
    LogOut,
    ShoppingCart,
} from "lucide-react";


const ICONES = {
    CRIACAO:   Plus,
    ALTERACAO: Pencil,
    EXCLUSAO:  X,
    LOGIN:     LogIn,
    LOGOUT:    LogOut,
    VENDA:     ShoppingCart,
};


const ROTULOS = {
    CRIACAO:   "Criação",
    ALTERACAO: "Alteração",
    EXCLUSAO:  "Exclusão",
    LOGIN:     "Login",
    LOGOUT:    "Logout",
    VENDA:     "Venda",
};


/*
 * Função pura — recebe "agora" como argumento.
 * Nunca lê o relógio internamente.
 */
function tempoRelativo(dataISO, agora) {

    if (!dataISO) {
        return "—";
    }

    const diff = Math.floor(
        (agora - new Date(dataISO).getTime()) / 1000
    );

    if (diff < 60)     return "agora mesmo";
    if (diff < 3600)   return `há ${Math.floor(diff / 60)} min`;
    if (diff < 86400)  return `há ${Math.floor(diff / 3600)} h`;
    if (diff < 604800) return `há ${Math.floor(diff / 86400)} d`;

    return new Date(dataISO).toLocaleDateString(
        "pt-MZ",
        { day: "2-digit", month: "short" }
    );
}


function PerfilAtividade({ acoes, carregando, agora }) {

    return (

        <section className="perfil-painel">

            <header className="perfil-painel-header">

                <div>
                    <h3>Actividade recente</h3>
                    <p>As suas últimas acções no WD Works.</p>
                </div>

            </header>


            {carregando ? (

                <p className="perfil-nota">
                    A carregar histórico...
                </p>

            ) : acoes.length === 0 ? (

                <p className="perfil-nota">
                    Ainda não existem acções registadas.
                </p>

            ) : (

                <ul className="perfil-atividade">

                    {acoes.map((acao, index) => {

                        const Icone =
                            ICONES[acao.tipo] || Pencil;

                        return (
                            <li
                                key={index}
                                className="perfil-atividade-item"
                                data-gravidade={acao.gravidade}
                            >

                                <span className="perfil-atividade-icon">
                                    <Icone size={14} strokeWidth={2.2} />
                                </span>


                                <div className="perfil-atividade-info">

                                    <strong>{acao.descricao}</strong>

                                    <span>
                                        {ROTULOS[acao.tipo] || acao.tipo}
                                    </span>

                                </div>


                                <span className="perfil-atividade-tempo">
                                    {tempoRelativo(acao.data, agora)}
                                </span>

                            </li>
                        );
                    })}

                </ul>
            )}

        </section>
    );
}


export default PerfilAtividade;