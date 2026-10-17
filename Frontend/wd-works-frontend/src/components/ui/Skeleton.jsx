import "./Skeleton.css";

function Skeleton({ type = "rect", className = "", style = {}, ...rest }) {
    const classList = ["skeleton"];

    if (type === "circle") classList.push("skeleton-circle");
    if (type === "text") classList.push("skeleton-text");
    if (type === "line") classList.push("skeleton-line");
    if (type === "card") classList.push("skeleton-card");

    if (className) classList.push(className);

    return (
        <div className={classList.join(" ")} style={style} {...rest} />
    );
}

export default Skeleton;
