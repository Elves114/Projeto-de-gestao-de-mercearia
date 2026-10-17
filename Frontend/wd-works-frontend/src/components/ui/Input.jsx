import "./Input.css";

function Input({
    type = "text",
    id,
    name,
    label,
    value,
    onChange,
    placeholder,
    disabled = false,
    required = false,
    error,
    className = "",
    ...rest
}) {
    const fieldId = id || name;

    return (
        <div className={`input-group ${className}`}>
            {label && (
                <label htmlFor={fieldId} className="input-label">
                    {label}
                    {required && <span className="input-required">*</span>}
                </label>
            )}
            <input
                type={type}
                id={fieldId}
                name={name}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                disabled={disabled}
                required={required}
                aria-invalid={!!error}
                aria-describedby={error ? `${fieldId}-error` : undefined}
                className="input-field"
                {...rest}
            />
            {error && (
                <p id={`${fieldId}-error`} className="input-error" role="alert">
                    {error}
                </p>
            )}
        </div>
    );
}

export default Input;
