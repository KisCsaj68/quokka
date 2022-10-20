const Button = (props) => {
    const text = props.text;
    const className = props.className;
    const btnType = props.btnType;
    const action = props.action;
    return (
        <button className={className} type={btnType}>
            {text}
        </button>
    );
};

export default Button;
