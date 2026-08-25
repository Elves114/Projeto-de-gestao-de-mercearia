import { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import Header from "./Header";
import "../style/Layout.css";

function Layout() {

    const [sidebarAberto, setSidebarAberto] = useState(false);

    return (
        <div
            className={`app-layout ${
                sidebarAberto
                    ? "sidebar-aberto"
                    : "sidebar-fechado"
            }`}
        >

            <Sidebar
                aberto={sidebarAberto}
                setAberto={setSidebarAberto}
            />

            <div className="app-content">

                <Header />

                <main className="main-content">
                    <Outlet />
                </main>

            </div>

        </div>
    );
}

export default Layout;