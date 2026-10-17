import { useEffect, useRef, useState } from "react";
import "./ConfirmDialog.css";

function ConfirmDialog({
    isOpen,
    title,
    message,
    confirmLabel = "Confirmar",
    cancelLabel = "Cancelar",
    confirmVariant = "danger",
    onConfirm,
    onCancel,
    carregando = false,
}) {
    const dialogRef = useRef(null);
    const firstFocusableRef = useRef(null);
    const [prevActiveElement, setPrevActiveElement] = useState(null);

    /* Focus trap */
    useEffect(() => {
        if (!isOpen) return;

        setPrevActiveElement(document.activeElement);

        // Focus no primeiro elemento focável
        const timer = setTimeout(() => {
            if (firstFocusableRef.current) {
                firstFocusableRef.current.focus();
            } else if (dialogRef.current) {
                dialogRef.current.focus();
            }
        }, 50);

        return () => clearTimeout(timer);
    }, [isOpen]);

    /* Restaurar focus ao fechar */
    useEffect(() => {
        if (!isOpen && prevActiveElement) {
            prevActiveElement.focus();
        }
    }, [isOpen, prevActiveElement]);

    /* Fechar com Escape */
    useEffect(() => {
        if (!isOpen) return;

        function onKey(e) {
            if (e.key === "Escape" && !carregando) {
                e.preventDefault();
                onCancel?.();
            }
        }
        window.addEventListener("keydown", onKey);
        return () => window.removeEventListener("keydown", onKey);
    }, [isOpen, onCancel, carregando]);

    /* Focus trap — Tab cycling */
    useEffect(() => {
        if (!isOpen) return;

        function onKeydown(e) {
            if (e.key !== "Tab") return;
            if (carregando) {
                e.preventDefault();
                return;
            }

            const container = dialogRef.current;
            if (!container) return;

            const focusable = container.querySelectorAll(
                'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
            );
            const focusableArray = Array.from(focusable);

            if (focusableArray.length === 0) return;

            const first = focusableArray[0];
            const last = focusableArray[focusableArray.length - 1];

            if (e.shiftKey) {
                if (document.activeElement === first) {
                    e.preventDefault();
                    last.focus();
                }
            } else {
                if (document.activeElement === last) {
                    e.preventDefault();
                    first.focus();
                }
            }
        }

        window.addEventListener("keydown", onKeydown);
        return () => window.removeEventListener("keydown", onKeydown);
    }, [isOpen, carregando]);

    if (!isOpen) return null;

    return (
        <div
            className="confirm-overlay"
            onClick={(e) => {
                if (e.target === e.currentTarget && !carregando) {
                    onCancel?.();
                }
            }}
            role="dialog"
            aria-modal="true"
            aria-labelledby="confirm-title"
            aria-describedby="confirm-message"
        >
            <div
                className="confirm-dialog"
                ref={dialogRef}
                tabIndex={-1}
            >
                <h3 id="confirm-title">{title}</h3>
                <p id="confirm-message">{message}</p>
                <div className="confirm-actions">
                    <button
                        type="button"
                        className="button-secondary"
                        onClick={onCancel}
                        disabled={carregando}
                        ref={firstFocusableRef}
                    >
                        {cancelLabel}
                    </button>
                    <button
                        type="button"
                        className={`btn-${confirmVariant}`}
                        onClick={() => onConfirm?.()}
                        disabled={carregando}
                    >
                        {carregando ? "A processar..." : confirmLabel}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ConfirmDialog;
