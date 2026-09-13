import { useEffect, useState } from "react";

import { useAuth } from "../../../contexts/useAuth";

import {
    buscarMinhaEmpresa,
    buscarMinhasAcoes,
} from "../services/perfilService";

import PerfilIdentidade from "../components/PerfilIdentidade";
import PerfilFormulario from "../components/PerfilFormulario";
import PerfilSenha      from "../components/PerfilSenha";
import PerfilEmpresa    from "../components/PerfilEmpresa";
import PerfilAtividade  from "../components/PerfilAtividade";
import PerfilSessao     from "../components/PerfilSessao";

import "../style/Perfil.css";


function Perfil() {

    const { usuario } = useAuth();

    const [empresa, setEmpresa] = useState(null);
    const [acoes, setAcoes] = useState([]);

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState("");


    /* ============================================================
       CARREGAMENTO INICIAL
       ============================================================ */

    useEffect(() => {

        let ativo = true;

        async function carregar() {

            try {

                const [dadosEmpresa, dadosAcoes] =
                    await Promise.all([
                        buscarMinhaEmpresa(),
                        buscarMinhasAcoes(0, 5),
                    ]);

                if (!ativo) {
                    return;
                }

                setEmpresa(dadosEmpresa);
                setAcoes(dadosAcoes.content || []);

            } catch (error) {

                console.error(
                    "Erro ao carregar perfil:",
                    error
                );

                if (ativo) {
                    setErro(
                        "Não foi possível carregar alguns dados do perfil."
                    );
                }

            } finally {

                if (ativo) {
                    setCarregando(false);
                }
            }
        }

        carregar();

        return () => {
            ativo = false;
        };

    }, []);


    /* ============================================================
       RENDER
       ============================================================ */

    return (

        <div className="perfil-page">

            <header className="perfil-header">

                <span className="perfil-overline">
                    Conta pessoal
                </span>

                <h1>
                    Os meus dados
                </h1>

                <p>
                    Consulte e actualize as informações
                    da sua conta no WD Works.
                </p>

            </header>


            {erro && (
                <div className="perfil-aviso perfil-aviso-erro">
                    {erro}
                </div>
            )}


            <div className="perfil-layout">

                {/* =================================================
                    COLUNA ESQUERDA — IDENTIDADE
                   ================================================= */}

                <aside className="perfil-coluna-esquerda">

                    <PerfilIdentidade
                        usuario={usuario}
                        empresa={empresa}
                        carregando={carregando}
                    />

                </aside>


                {/* =================================================
                    COLUNA DIREITA — SECÇÕES
                   ================================================= */}

                <main className="perfil-coluna-direita">

                    <PerfilFormulario usuario={usuario} />

                    <PerfilSenha />

                    <PerfilEmpresa
                        empresa={empresa}
                        carregando={carregando}
                    />

                    <PerfilAtividade
                        acoes={acoes}
                        carregando={carregando}
                    />

                    <PerfilSessao />

                </main>

            </div>

        </div>
    );
}


export default Perfil;