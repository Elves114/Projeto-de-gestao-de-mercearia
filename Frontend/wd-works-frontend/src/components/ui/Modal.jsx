import { useEffect, useRef } from "react";
import { X } from "lucide-react";
import "./Modal.css";

function Modal({
    isOpen,
    title,
    children,
    onClose,
    footer,
    size = "md",
    closeOnOverlay = true,
}) {
    const contentRef = useRef(null);
    const prevActiveRef = useRef(null);

    useEffect(() => {
        if (!isOpen) return;

        prevActiveRef.current = document.activeElement;

        const timer = setTimeout(() => {
            contentRef.current?.focus();
        }, 50);

        return () => clearTimeout(timer);
    }, [isOpen]);

    useEffect(() => {
        if (!isOpen && prevActiveRef.current) {
            prevActiveRef.current.focus();
        }
    }, [isOpen]);

    useEffect(() => {
        if (!isOpen) return;

        function onKey(e) {
            if (e.key === "Escape") {
                onClose?.();
            }
        }

        function onKeydown(e) {
            if (e.key !== "Tab") return;
            const container = contentRef.current;
            if (!container) return;

            const focusable = container.querySelectorAll(
                'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
            );
            const arr = Array.from(focusable);
            if (!arr.length) return;

            if (e.shiftKey) {
                if (document.activeElement === arr[0]) {
                    e.preventDefault();
                    arr[arr.length - 1].focus();
                }
            } else {
                if (document.activeElement === arr[arr.length - 1]) {
                    e.preventDefault();
                    arr[0].focus();
                }
            }
        }

        window.addEventListener("keydown", onKey);
        window.addEventListener("keydown", onKeydown);
        return () => {
            window.removeEventListener("keydown", onKey);
            window.removeEventListener("keydown", onKeydown);
        };
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    return (
        <div
            className="modal-overlay"
            onClick={(e) => {
                if (closeOnOverlay && e.target === e.currentTarget) {
                    onClose?.();
                }
            }}
            role="dialog"
            aria-modal="true"
            aria-labelledby={title ? "modal-title" : undefined}
        >
            <div
                className={`modal modal-${size}`}
                ref={contentRef}
                tabIndex={-1}
            >
                {title && (
                    <header className="modal-header">
                        <h3 id="modal-title">{title}</h3>
                        <button
                            type="button"
                            className="modal-close"
                            onClick={onClose}
                            aria-label="Fechar"
                        >
                            <X size={18} aria-hidden="true" />
                        </button>
                    </header>
                )}
                <div className="modal-body">{children}</div>
                {footer && <div className="modal-footer">{footer}</div>}
            </div>
        </div>
    );
}

export default Modal;
