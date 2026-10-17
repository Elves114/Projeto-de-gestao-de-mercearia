
import { Moon, Sun } from "lucide-react";
import { useTheme } from "../../contexts/useTheme";

function ThemeToggle() {
    const { tema, alternarTema } = useTheme();
    const escuro = tema === "dark";

    return (
        <button
            type="button"
            className="theme-toggle"
            onClick={alternarTema}
            aria-label={escuro ? "Ativar modo claro" : "Ativar modo escuro"}
            title={escuro ? "Modo claro" : "Modo escuro"}
        >
            {escuro ? <Sun size={18} /> : <Moon size={18} />}
        </button>
    );
}

export default ThemeToggle;