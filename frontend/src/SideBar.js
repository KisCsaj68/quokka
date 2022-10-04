import {Link} from "react-router-dom";

const SideBar = () => {
    return (
        <div className="area">
            <nav className="main-menu">
                <ul>
                    <li>
                        <Link to="#">
                            <i className="fa fa-bar-chart-o fa-2x"></i>
                            <span className="nav-text">
                            Positions
                        </span>
                        </Link>
                    </li>

                    <li>
                        <Link to="#">
                            <to className="fa fa-table fa-2x"></to>
                            <span className="nav-text">
                            Orders
                        </span>
                        </Link>
                    </li>

                    <li>
                        <Link to="#">
                            <i className="fa fa-info fa-2x"></i>
                            <span className="nav-text">
                            User Information
                        </span>
                        </Link>
                    </li>
                </ul>

                <ul className="logout">
                    <li>
                        <Link to="#">
                            <i className="fa fa-power-off fa-2x"></i>
                            <span className="nav-text">
                            Logout
                        </span>
                        </Link>
                    </li>
                </ul>
            </nav>
        </div>


    )
}

export default SideBar;