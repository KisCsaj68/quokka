import {Link} from "react-router-dom";

const Button = ({text, action, btnType, className}) => {
    return (
        <button className={className} type={btnType} >{text}</button>
    )
}

export default Button;