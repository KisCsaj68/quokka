const PurchaseField = (props) => {
    const name = props.name;
    const text = props.text;
    const type = props.type;
    const onChangeHandler = props.onChange;
    const onBuyHandler = props.onBuy;
    const min = props.min;
    return (
        <div>
            <label htmlFor={name}>
                <b>{text}</b>
            </label>
            <input
                type="number"
                placeholder={`Enter ${text}`}
                name={name}
                id={name}
                value="1"
                min="1"
                onChange={onChangeHandler}
            />
            <button></button>
        </div>
    );
};

export default PurchaseField;
