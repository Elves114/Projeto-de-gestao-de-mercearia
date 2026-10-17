import "./Badge.css";

function Badge({
    children,
    variant = "default",
    size = "md",
    className = "",
    ...rest
}) {
    return (
        <span
            className={`badge badge-${variant} badge-${size} ${className}`}
            {...rest}
        >
            {children}
        </span>
    );
}

export default Badge;
