import { BrowserRouter, Routes, Route } from "react-router-dom";


import Login from "../features/auth/pages/Login";
import Categorias from "../features/categorias/pages/Categorias";
import Produtos from "../features/produtos/pages/Produtos";
import Estoque from "../features/estoque/pages/Estoque";
import Vendas from "../features/vendas/pages/Vendas";
import NovaVenda from "../features/vendas/pages/NovaVenda";
import EntradaStock from "../features/estoque/pages/EntradaStock";
import Usuarios from "../features/usuarios/pages/Usuarios";
import NovoUsuario from "../features/usuarios/pages/NovoUsuario";
import Empresa from "../features/empresa/pages/Empresa";
import RotaPrivada from "./RotaPrivada";
import RotaPorPerfil from "./RotaPorPerfil";
import Auditoria from "../features/auditoria/pages/Auditoria";
import Dashboard from "../features/dashboard/pages/Dashboard";
import Layout from "../components/layout/Layout";
import DetalhesVenda from "../features/vendas/pages/DetalhesVenda";
import EditarUsuario from "../features/usuarios/pages/EditarUsuario";
import Cadastro from "../features/auth/pages/Cadastro";
import MovimentosStock from "../features/estoque/pages/MovimentoStock";
import AlertasStock from "../features/alertaStock/pages/AlertaStock";
import RecuperarSenha from "../features/auth/pages/RecuperarSenha";
import RedefinirSenha from "../features/auth/pages/RedefinirSenha";
import Perfil from "../features/perfil/pages/Perfil";
import Inicio from "./Inicio";


function AppRoutes() {

    return (
        <BrowserRouter>

            <Routes>

                <Route
                    path="/"
                    element={<Inicio />}
                />

                <Route
                    path="/login"
                    element={<Login />}
                />

                <Route
                    path="/cadastro"
                    element={<Cadastro />}
                />
                <Route
                    path="/recuperar-senha"
                    element={<RecuperarSenha />}
                />

                <Route
                    path="/redefinir-senha"
                    element={<RedefinirSenha />}
                />


                <Route element={<RotaPrivada />}>

                    <Route element={<RotaPorPerfil />}>

                        <Route element={<Layout />}>

                            <Route
                                path="/dashboard"
                                element={<Dashboard />}
                            />

                            <Route
                                path="/alertas-stock"
                                element={<AlertasStock />}
                            />

                            <Route
                                path="/estoque/movimentos"
                                element={<MovimentosStock />}
                            />

                            <Route
                                path="/categorias"
                                element={<Categorias />}
                            />

                            <Route
                                path="/produtos"
                                element={<Produtos />}
                            />

                            <Route
                                path="/estoque"
                                element={<Estoque />}
                            />

                            <Route
                                path="/vendas"
                                element={<Vendas />}
                            />

                            <Route
                                path="/vendas/:id"
                                element={<DetalhesVenda />}
                            />

                            <Route
                                path="/vendas/nova"
                                element={<NovaVenda />}
                            />

                            <Route
                                path="/estoque/entrada"
                                element={<EntradaStock />}
                            />

                            <Route
                                path="/usuarios"
                                element={<Usuarios />}
                            />

                            <Route
                                path="/usuarios/novo"
                                element={<NovoUsuario />}
                            />

                            <Route
                                path="/usuarios/:id/editar"
                                element={<EditarUsuario />}
                            />

                            <Route
                                path="/empresa"
                                element={<Empresa />}
                            />

                            <Route
                                path="/auditoria"
                                element={<Auditoria />}
                            />

                            <Route
                                path="/perfil"
                                element={<Perfil />}
                            />

                        </Route>

                    </Route>

                </Route>

            </Routes>

        </BrowserRouter>
    );
}

export default AppRoutes;