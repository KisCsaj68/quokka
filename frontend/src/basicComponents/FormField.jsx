const FormField = (props) => {
    const name = props.name;
    const text = props.text;
    const type = props.type;
    const onChangeHandler = props.onChange;

    return (
        <div>
            <label htmlFor={name}>
                <b>{text}</b>
            </label>
            <input
                type={type}
                placeholder={`Enter ${text}`}
                name={name}
                id={name}
                required
                value=""
                onChange={onChangeHandler}
            ></input>
        </div>
    );
};
export default FormField;
