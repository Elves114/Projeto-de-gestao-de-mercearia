
import { useState } from "react";
import { CheckCircle2, XCircle, AlertTriangle, Info, X } from "lucide-react";

import "./ToastContainer.css";

const ICONES = {
    success: CheckCircle2,
    error: XCircle,
    warning: AlertTriangle,
    info: Info,
};

function ToastContainer({ toasts, onDismiss }) {
    // ⚠️ NÃO chama useToast. Recebe tudo por props.

    if (!toasts || toasts.length === 0) {
        return null;
    }

    return (
        <div
            className="toast-container"
            role="region"
            aria-live="polite"
            aria-label="Notificações"
        >
            {toasts.map((toast) => (
                <ToastItem
                    key={toast.id}
                    toast={toast}
                    onDismiss={onDismiss}
                />
            ))}
        </div>
    );
}

function ToastItem({ toast, onDismiss }) {
    const Icone = ICONES[toast.type] || Info;
    const [saindo, setSaindo] = useState(false);

    function fechar() {
        setSaindo(true);
        setTimeout(() => onDismiss(toast.id), 180);
    }

    function executarAcao() {
        try {
            toast.action.onClick?.();
        } finally {
            fechar();
        }
    }

    return (
        <div
            className={`toast toast-${toast.type} ${saindo ? "toast-exit" : ""}`}
            role="status"
        >
            <span className="toast-icon">
                <Icone size={18} strokeWidth={2.2} />
            </span>

            <p className="toast-message">{toast.message}</p>

            {toast.action && (
                <button
                    type="button"
                    className="toast-action"
                    onClick={executarAcao}
                >
                    {toast.action.label}
                </button>
            )}

            <button
                type="button"
                className="toast-close"
                onClick={fechar}
                aria-label="Fechar notificação"
            >
                <X size={14} strokeWidth={2.4} />
            </button>
        </div>
    );
}

export default ToastContainer;