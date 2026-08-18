import { useEffect, useState } from "react";
import {
    buscarMinhaEmpresa,
    atualizarMinhaEmpresa,
    ativarMinhaEmpresa,
    desativarMinhaEmpresa,
} from "../services/empresaService";


function Empresa() {
    const [empresa, setEmpresa] = useState(null);

    const [formulario, setFormulario] = useState({
        nome: "",
        nuit: "",
        email: "",
        contacto: "",
        endereco: "",
    });

    const [editando, setEditando] = useState(false);
    const [carregando, setCarregando] = useState(true);
    const [salvando, setSalvando] = useState(false);
    const [alterandoStatus, setAlterandoStatus] = useState(false);

    const [erro, setErro] = useState(null);
    const [sucesso, setSucesso] = useState(null);

    useEffect(() => {
        async function carregarEmpresa() {
            try {
                const data = await buscarMinhaEmpresa();

                setEmpresa(data);

                setFormulario({
                    nome: data.nome || "",
                    nuit: data.nuit || "",
                    email: data.email || "",
                    contacto: data.contacto || "",
                    endereco: data.endereco || "",
                });
            } catch (error) {
                console.error(error);

                setErro(
                    "Não foi possível carregar os dados da empresa."
                );
            } finally {
                setCarregando(false);
            }
        }

        carregarEmpresa();
    }, []);

    function alterarCampo(event) {
        const { name, value } = event.target;

        setFormulario((anterior) => ({
            ...anterior,
            [name]: value,
        }));
    }

    function cancelarEdicao() {
        setFormulario({
            nome: empresa.nome || "",
            nuit: empresa.nuit || "",
            email: empresa.email || "",
            contacto: empresa.contacto || "",
            endereco: empresa.endereco || "",
        });

        setEditando(false);
        setErro(null);
        setSucesso(null);
    }

    async function salvarAlteracoes(event) {
        event.preventDefault();

        setSalvando(true);
        setErro(null);
        setSucesso(null);

        try {
            const data = await atualizarMinhaEmpresa(formulario);

            setEmpresa(data);

            setFormulario({
                nome: data.nome || "",
                nuit: data.nuit || "",
                email: data.email || "",
                contacto: data.contacto || "",
                endereco: data.endereco || "",
            });

            setEditando(false);

            setSucesso(
                "Empresa atualizada com sucesso."
            );
        } catch (error) {
            console.error(error);

            setErro(
                "Não foi possível atualizar a empresa."
            );
        } finally {
            setSalvando(false);
        }
    }

    async function alterarStatus() {
        setAlterandoStatus(true);
        setErro(null);
        setSucesso(null);

        try {
            const data =
                empresa.status === "ATIVO"
                    ? await desativarMinhaEmpresa()
                    : await ativarMinhaEmpresa();

            setEmpresa(data);

            setSucesso(
                data.status === "ATIVO"
                    ? "Empresa ativada com sucesso."
                    : "Empresa desativada com sucesso."
            );
        } catch (error) {
            console.error(error);

            setErro(
                "Não foi possível alterar o status da empresa."
            );
        } finally {
            setAlterandoStatus(false);
        }
    }

    if (carregando) {
        return <p>Carregando empresa...</p>;
    }

    if (erro && !empresa) {
        return <p>{erro}</p>;
    }

    if (!empresa) {
        return <p>Empresa não encontrada.</p>;
    }

    return (
    <div className="empresa-page">

        <div className="empresa-header">
            <div>
                <h1>Minha Empresa</h1>

                <p>
                    Informações e configurações da sua empresa.
                </p>
            </div>
        </div>

        {erro && (
            <div className="empresa-message empresa-message-error">
                {erro}
            </div>
        )}

        {sucesso && (
            <div className="empresa-message empresa-message-success">
                {sucesso}
            </div>
        )}

        {!editando ? (
            <section className="empresa-card">

                <div className="empresa-card-header">
                    <div>
                        <h2>Informações da empresa</h2>

                        <span>
                            Dados cadastrais
                        </span>
                    </div>

                    <span
                        className={
                            empresa.status === "ATIVO"
                                ? "empresa-status ativo"
                                : "empresa-status inativo"
                        }
                    >
                        {empresa.status}
                    </span>
                </div>

                <div className="empresa-info-grid">

                    <div className="empresa-info">
                        <span>Nome</span>
                        <strong>{empresa.nome}</strong>
                    </div>

                    <div className="empresa-info">
                        <span>NUIT</span>
                        <strong>{empresa.nuit}</strong>
                    </div>

                    <div className="empresa-info">
                        <span>Email</span>
                        <strong>{empresa.email}</strong>
                    </div>

                    <div className="empresa-info">
                        <span>Contacto</span>
                        <strong>{empresa.contacto}</strong>
                    </div>

                    <div className="empresa-info empresa-info-full">
                        <span>Endereço</span>
                        <strong>{empresa.endereco}</strong>
                    </div>

                    <div className="empresa-info">
                        <span>Data de criação</span>

                        <strong>
                            {new Date(
                                empresa.dataCriacao
                            ).toLocaleString()}
                        </strong>
                    </div>

                </div>

                <div className="empresa-actions">

                    <button
                        className="button button-primary"
                        onClick={() => {
                            setEditando(true);
                            setSucesso(null);
                            setErro(null);
                        }}
                    >
                        Editar
                    </button>

                    <button
                        className={
                            empresa.status === "ATIVO"
                                ? "button button-danger"
                                : "button button-success"
                        }
                        onClick={alterarStatus}
                        disabled={alterandoStatus}
                    >
                        {alterandoStatus
                            ? "Alterando..."
                            : empresa.status === "ATIVO"
                                ? "Desativar"
                                : "Ativar"}
                    </button>

                </div>

            </section>
        ) : (
            <section className="empresa-card">

                <div className="empresa-card-header">
                    <div>
                        <h2>Editar empresa</h2>

                        <span>
                            Atualize os dados cadastrais
                        </span>
                    </div>
                </div>

                <form
                    className="empresa-form"
                    onSubmit={salvarAlteracoes}
                >

                    <div className="empresa-form-grid">

                        <div className="form-group">
                            <label htmlFor="nome">
                                Nome
                            </label>

                            <input
                                id="nome"
                                type="text"
                                name="nome"
                                value={formulario.nome}
                                onChange={alterarCampo}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="nuit">
                                NUIT
                            </label>

                            <input
                                id="nuit"
                                type="text"
                                name="nuit"
                                value={formulario.nuit}
                                onChange={alterarCampo}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="email">
                                Email
                            </label>

                            <input
                                id="email"
                                type="email"
                                name="email"
                                value={formulario.email}
                                onChange={alterarCampo}
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="contacto">
                                Contacto
                            </label>

                            <input
                                id="contacto"
                                type="text"
                                name="contacto"
                                value={formulario.contacto}
                                onChange={alterarCampo}
                            />
                        </div>

                        <div className="form-group form-group-full">
                            <label htmlFor="endereco">
                                Endereço
                            </label>

                            <input
                                id="endereco"
                                type="text"
                                name="endereco"
                                value={formulario.endereco}
                                onChange={alterarCampo}
                            />
                        </div>

                    </div>

                    <div className="empresa-actions">

                        <button
                            className="button button-primary"
                            type="submit"
                            disabled={salvando}
                        >
                            {salvando
                                ? "Salvando..."
                                : "Salvar alterações"}
                        </button>

                        <button
                            className="button button-secondary"
                            type="button"
                            onClick={cancelarEdicao}
                            disabled={salvando}
                        >
                            Cancelar
                        </button>

                    </div>

                </form>

            </section>
        )}

    </div>
);
}

export default Empresa;