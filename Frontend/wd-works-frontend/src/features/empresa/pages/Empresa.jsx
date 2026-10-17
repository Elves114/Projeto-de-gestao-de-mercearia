import { useEffect, useState, useRef } from "react";
import {
    Building2, Hash, Mail, Phone, MapPin, Calendar,
    Pencil, Power, Save, X, Sparkles,
} from "lucide-react";
import { motion } from "framer-motion";

import {
    buscarMinhaEmpresa,
    atualizarMinhaEmpresa,
    ativarMinhaEmpresa,
    desativarMinhaEmpresa,
} from "../services/empresaService";

import "../style/Empresa.css";


/* ============================================================
   CONTADOR ANIMADO — sem biblioteca
   ============================================================ */

function useContadorAnimado(valorFinal, duracao = 1500) {
    const ref = useRef(null);

    useEffect(() => {
        const elemento = ref.current;
        if (!elemento || !valorFinal) return;

        const inicio = performance.now();
        let raf;

        function animar(tempoAtual) {
            const progresso = Math.min((tempoAtual - inicio) / duracao, 1);
            const suavizado = 1 - Math.pow(1 - progresso, 3);
            elemento.textContent = String(Math.round(valorFinal * suavizado));

            if (progresso < 1) raf = requestAnimationFrame(animar);
        }

        raf = requestAnimationFrame(animar);
        return () => cancelAnimationFrame(raf);
    }, [valorFinal, duracao]);

    return ref;
}


/* ============================================================
   COMPONENTE
   ============================================================ */

function Empresa() {
    const [empresa, setEmpresa] = useState(null);
    const [editando, setEditando] = useState(false);
    const [carregando, setCarregando] = useState(true);
    const [salvando, setSalvando] = useState(false);
    const [alterandoStatus, setAlterandoStatus] = useState(false);
    const [erro, setErro] = useState(null);

    const [formulario, setFormulario] = useState({
        nome: "", nuit: "", email: "", contacto: "", endereco: "",
    });

    /* Hook sempre no topo, mesmo que ainda sem empresa */
    const anoCriacao = empresa
        ? new Date(empresa.dataCriacao).getFullYear()
        : 0;
    const contadorRef = useContadorAnimado(anoCriacao);

    /* Carregar empresa */
    useEffect(() => {
        async function carregar() {
            try {
                const data = await buscarMinhaEmpresa();
                setEmpresa(data);
                preencherFormulario(data);
            } catch (e) {
                console.error(e);
                setErro("Não foi possível carregar os dados da empresa.");
            } finally {
                setCarregando(false);
            }
        }
        carregar();
    }, []);

    function preencherFormulario(data) {
        setFormulario({
            nome: data.nome || "",
            nuit: data.nuit || "",
            email: data.email || "",
            contacto: data.contacto || "",
            endereco: data.endereco || "",
        });
    }

    function alterarCampo(e) {
        const { name, value } = e.target;
        setFormulario((p) => ({ ...p, [name]: value }));
    }

    function cancelar() {
        preencherFormulario(empresa);
        setEditando(false);
        setErro(null);
    }

    async function salvar(e) {
        e.preventDefault();
        setSalvando(true);
        setErro(null);
        try {
            const data = await atualizarMinhaEmpresa(formulario);
            setEmpresa(data);
            preencherFormulario(data);
            setEditando(false);
        } catch (err) {
            console.error(err);
            setErro("Não foi possível atualizar a empresa.");
        } finally {
            setSalvando(false);
        }
    }

    async function alternarStatus() {
        setAlterandoStatus(true);
        setErro(null);
        try {
            const data = empresa.status === "ATIVO"
                ? await desativarMinhaEmpresa()
                : await ativarMinhaEmpresa();
            setEmpresa(data);
        } catch (err) {
            console.error(err);
            setErro("Não foi possível alterar o status da empresa.");
        } finally {
            setAlterandoStatus(false);
        }
    }

    /* Estados de carregamento */
    if (carregando) {
        return (
            <div className="empresa-page">
                <div className="empresa-skeleton-hero" />
                <div className="empresa-skeleton-grid">
                    {Array.from({ length: 4 }).map((_, i) => (
                        <div key={i} className="empresa-skeleton-card" />
                    ))}
                </div>
            </div>
        );
    }

    if (erro && !empresa) {
        return (
            <div className="empresa-page">
                <div className="empresa-message empresa-message-error">{erro}</div>
            </div>
        );
    }

    if (!empresa) {
        return <div className="empresa-page"><p>Empresa não encontrada.</p></div>;
    }

    const ativo = empresa.status === "ATIVO";

    return (
        <div className="empresa-page">

            {/* HEADER */}
            <header className="empresa-header">
                <span className="empresa-overline">Perfil da Empresa</span>
                <h1>
                    {empresa.nome}
                    <Sparkles size={22} className="empresa-header-sparkle" aria-hidden />
                </h1>
                <p>
                    A identidade digital do seu negócio no WD Works.
                    Gerir os dados e o estado da conta da empresa.
                </p>
            </header>

            {erro && <div className="empresa-message empresa-message-error">{erro}</div>}

            <div className="empresa-layout">

                {/* COLUNA ESQUERDA — HERO */}
                <motion.aside
                    className="empresa-hero"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
                >
                    <div className="empresa-hero-conteudo">
                        <div className="empresa-hero-logo">
                            {empresa.nome.charAt(0).toUpperCase()}
                        </div>

                        <div className="empresa-hero-info">
                            <h2>{empresa.nome}</h2>
                            <span className="empresa-hero-nuit">
                                NUIT {empresa.nuit}
                            </span>
                        </div>

                        <span className={`empresa-status ${ativo ? "ativo" : "inativo"}`}>
                            {empresa.status}
                        </span>

                        <div className="empresa-hero-contadores">
                            <div className="empresa-hero-counter">
                                <span className="empresa-hero-counter-label">
                                    No WD Works desde
                                </span>
                                <strong ref={contadorRef}>{anoCriacao}</strong>
                            </div>
                            <div className="empresa-hero-counter">
                                <span className="empresa-hero-counter-label">
                                    Estado atual
                                </span>
                                <strong className={ativo ? "text-ativo" : "text-inativo"}>
                                    {ativo ? "Operacional" : "Suspensa"}
                                </strong>
                            </div>
                        </div>
                    </div>
                </motion.aside>

                {/* COLUNA DIREITA — PAINEL */}
                <motion.section
                    className="empresa-panel"
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.1, ease: [0.22, 1, 0.36, 1] }}
                >
                    {!editando ? (
                        <div>
                            <header className="empresa-panel-header">
                                <div>
                                    <h2>Informações</h2>
                                    <p>Dados cadastrais da empresa</p>
                                </div>
                                <button
                                    type="button"
                                    className="empresa-panel-action"
                                    onClick={() => setEditando(true)}
                                >
                                    <Pencil size={14} />
                                    Editar
                                </button>
                            </header>

                            <div className="empresa-info-grid">
                                <InfoItem icon={Building2} label="Nome" value={empresa.nome} full />
                                <InfoItem icon={Hash} label="NUIT" value={empresa.nuit} />
                                <InfoItem icon={Mail} label="Email" value={empresa.email} />
                                <InfoItem icon={Phone} label="Contacto" value={empresa.contacto} />
                                <InfoItem icon={MapPin} label="Endereço" value={empresa.endereco} full />
                                <InfoItem
                                    icon={Calendar}
                                    label="Data de criação"
                                    value={new Date(empresa.dataCriacao).toLocaleString("pt-MZ")}
                                    full
                                />
                            </div>

                            <footer className="empresa-panel-footer">
                                <button
                                    type="button"
                                    className={`empresa-action empresa-action-${ativo ? "danger" : "success"}`}
                                    onClick={alternarStatus}
                                    disabled={alterandoStatus}
                                >
                                    <Power size={14} />
                                    {alterandoStatus
                                        ? "A alterar..."
                                        : ativo ? "Desativar empresa" : "Ativar empresa"}
                                </button>
                            </footer>
                        </div>
                    ) : (
                        <form onSubmit={salvar}>
                            <header className="empresa-panel-header">
                                <div>
                                    <h2>Editar</h2>
                                    <p>Atualize os dados da empresa</p>
                                </div>
                            </header>

                            <div className="empresa-form-grid">
                                <Campo nome="nome" label="Nome" valor={formulario.nome} onChange={alterarCampo} full />
                                <Campo nome="nuit" label="NUIT" valor={formulario.nuit} onChange={alterarCampo} />
                                <Campo nome="email" label="Email" valor={formulario.email} onChange={alterarCampo} type="email" />
                                <Campo nome="contacto" label="Contacto" valor={formulario.contacto} onChange={alterarCampo} />
                                <Campo nome="endereco" label="Endereço" valor={formulario.endereco} onChange={alterarCampo} full />
                            </div>

                            <footer className="empresa-panel-footer">
                                <button type="button" className="empresa-action empresa-action-secondary" onClick={cancelar} disabled={salvando}>
                                    <X size={14} />
                                    Cancelar
                                </button>
                                <button type="submit" className="empresa-action empresa-action-primary" disabled={salvando}>
                                    <Save size={14} />
                                    {salvando ? "A guardar..." : "Guardar alterações"}
                                </button>
                            </footer>
                        </form>
                    )}
                </motion.section>
            </div>
        </div>
    );
}


/* ============================================================
   COMPONENTES AUXILIARES
   ============================================================ */

function InfoItem({ icon: Icon, label, value, full }) {
    return (
        <div className={`empresa-info ${full ? "empresa-info-full" : ""}`}>
            <span className="empresa-info-label">
                <Icon size={11} strokeWidth={2.4} />
                {label}
            </span>
            <strong>{value}</strong>
        </div>
    );
}

function Campo({ nome, label, valor, onChange, type = "text", full }) {
    return (
        <div className={`empresa-field ${full ? "empresa-field-full" : ""}`}>
            <label htmlFor={nome}>{label}</label>
            <input
                id={nome}
                name={nome}
                type={type}
                value={valor}
                onChange={onChange}
                autoComplete="off"
            />
        </div>
    );
}

export default Empresa;