import { Link } from "react-router-dom";

const SideBarListItem = (props) => {
    const pathTo = props.pathTo;
    const itemCSS = `fa ${props.itemCSS} fa-2x`;
    const text = props.text;
    return (
        <li>
            <Link to={pathTo}>
                <i className={itemCSS}></i>
                <span className="nav-text">{text}</span>
            </Link>
        </li>
    );
};

export default SideBarListItem;
