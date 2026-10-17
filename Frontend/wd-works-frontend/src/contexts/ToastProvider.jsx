import { useCallback, useMemo, useState } from "react";

import { ToastContext } from "./toastContext";
import ToastContainer from "../components/ui/ToastContainer";


export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([]);

    const removeToast = useCallback((id) => {
        setToasts((prev) => prev.filter((t) => t.id !== id));
    }, []);

    const addToast = useCallback((message, options = {}) => {
        const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
        const duration = options.duration ?? 4000;

        const toast = {
            id,
            message,
            type: options.type || "info",
            duration,
            action: options.action || null,
        };

        setToasts((prev) => [...prev, toast]);

        // Auto-dismiss (0 = não auto-fechar)
        if (duration > 0) {
            setTimeout(() => {
                setToasts((prev) => prev.filter((t) => t.id !== id));
            }, duration);
        }

        return id;
    }, []);

    const success = useCallback(
        (msg, opts) => addToast(msg, { ...opts, type: "success" }),
        [addToast]
    );
    const error = useCallback(
        (msg, opts) => addToast(msg, { ...opts, type: "error" }),
        [addToast]
    );
    const warning = useCallback(
        (msg, opts) => addToast(msg, { ...opts, type: "warning" }),
        [addToast]
    );
    const info = useCallback(
        (msg, opts) => addToast(msg, { ...opts, type: "info" }),
        [addToast]
    );

    const value = useMemo(
        () => ({ toasts, addToast, removeToast, success, error, warning, info }),
        [toasts, addToast, removeToast, success, error, warning, info]
    );

    return (
        <ToastContext.Provider value={value}>
            {children}
            <ToastContainer toasts={toasts} onDismiss={removeToast} />
        </ToastContext.Provider>
    );
}