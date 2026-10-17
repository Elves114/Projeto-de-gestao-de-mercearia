import { useCallback, useEffect, useMemo, useState } from "react";

import {
    THEME_STORAGE_KEY,
    ThemeContext,
} from "./themeContext";


function temaPreferidoDoSistema() {
    if (typeof window === "undefined") {
        return "light";
    }

    return window.matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light";
}


function lerTemaInicial() {
    if (typeof window === "undefined") {
        return "light";
    }

    const noHtml = document.documentElement.getAttribute("data-theme");
    if (noHtml === "light" || noHtml === "dark") {
        return noHtml;
    }

    try {
        const gravado = localStorage.getItem(THEME_STORAGE_KEY);
        if (gravado === "light" || gravado === "dark") {
            return gravado;
        }
    } catch {
        /* localStorage indisponível */
    }

    return temaPreferidoDoSistema();
}


function aplicarTema(tema) {
    document.documentElement.setAttribute("data-theme", tema);
}


export function ThemeProvider({ children }) {
    const [tema, setTemaState] = useState(lerTemaInicial);

    useEffect(() => {
        aplicarTema(tema);

        try {
            localStorage.setItem(THEME_STORAGE_KEY, tema);
        } catch {
            /* persistência opcional */
        }
    }, [tema]);

    const setTema = useCallback((proximo) => {
        if (proximo !== "light" && proximo !== "dark") {
            return;
        }
        setTemaState(proximo);
    }, []);

    const alternarTema = useCallback(() => {
        setTemaState((atual) => (atual === "dark" ? "light" : "dark"));
    }, []);

    const valor = useMemo(
        () => ({ tema, alternarTema, setTema }),
        [tema, alternarTema, setTema]
    );

    return (
        <ThemeContext.Provider value={valor}>
            {children}
        </ThemeContext.Provider>
    );
}