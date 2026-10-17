import "./Button.css";

function Button({
    children,
    type = "button",
    variant = "primary",
    size = "md",
    disabled = false,
    fullWidth = false,
    icon: Icon,
    iconPosition = "left",
    onClick,
    className = "",
    ...rest
}) {
    const classes = [
        "ui-button",
        `ui-button-${variant}`,
        `ui-button-${size}`,
        fullWidth ? "ui-button-full" : "",
    ].filter(Boolean).join(" ");

    return (
        <button
            type={type}
            className={`${classes} ${className}`}
            disabled={disabled}
            onClick={onClick}
            {...rest}
        >
            {Icon && iconPosition === "left" && (
                <span className="ui-button-icon ui-button-icon-left" aria-hidden="true">
                    <Icon size={16} />
                </span>
            )}
            <span className="ui-button-label">{children}</span>
            {Icon && iconPosition === "right" && (
                <span className="ui-button-icon ui-button-icon-right" aria-hidden="true">
                    <Icon size={16} />
                </span>
            )}
        </button>
    );
}

export default Button;
