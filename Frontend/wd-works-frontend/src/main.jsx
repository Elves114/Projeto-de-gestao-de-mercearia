
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import App from "./App.jsx";
import { ThemeProvider } from "./contexts/ThemeProvider.jsx";
import { ToastProvider } from "./contexts/ToastProvider.jsx";
import { AuthProvider } from "./contexts/AuthProvider.jsx";

import "./styles/tokens.css";
import "./index.css";

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <ThemeProvider>
            <ToastProvider>          {/* ← FALTAVA */}
                <AuthProvider>
                    <App />
                </AuthProvider>
            </ToastProvider>
        </ThemeProvider>
    </StrictMode>,
);