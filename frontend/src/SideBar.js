const SideBar = () => {
    return (
        <div className="area">
            <nav className="main-menu">
                <ul>
                    <li>
                        {/*<a href="#">*/}
                        {/*    <i className="fa fa-home fa-2x"></i>*/}
                        {/*    <span className="nav-text">*/}
                        {/*    Dashboard*/}
                        {/*</span>*/}
                        {/*</a>*/}

                    </li>
                    <li className="has-subnav">
                        {/*<a href="#">*/}
                        {/*    <i className="fa fa-laptop fa-2x"></i>*/}
                        {/*    <span className="nav-text">*/}
                        {/*    Stars Components*/}
                        {/*</span>*/}
                        {/*</a>*/}

                    </li>
                    {/*<li className="has-subnav">*/}
                    {/*    <a href="#">*/}
                    {/*        <i className="fa fa-list fa-2x"></i>*/}
                    {/*        <span className="nav-text">*/}
                    {/*        Forms*/}
                    {/*    </span>*/}
                    {/*    </a>*/}

                    {/*</li>*/}
                    {/*<li className="has-subnav">*/}
                    {/*    <a href="#">*/}
                    {/*    /!*    <i className="fa fa-folder-open fa-2x"></i>*!/*/}
                    {/*    /!*    <span className="nav-text">*!/*/}
                    {/*    /!*    Pages*!/*/}
                    {/*    /!*</span>*!/*/}
                    {/*    </a>*/}

                    {/*</li>*/}
                    <li>
                        <a href="#">
                            <i className="fa fa-bar-chart-o fa-2x"></i>
                            <span className="nav-text">
                            Positions
                        </span>
                        </a>
                    </li>
                    <li>
                        {/*<a href="#">*/}
                        {/*    <i className="fa fa-font fa-2x"></i>*/}
                        {/*    <span className="nav-text">*/}
                        {/*   Quotes*/}
                        {/*</span>*/}
                        {/*</a>*/}
                    </li>
                    <li>
                        <a href="#">
                            <i className="fa fa-table fa-2x"></i>
                            <span className="nav-text">
                            Orders
                        </span>
                        </a>
                    </li>
                    <li>
                        {/*<a href="#">*/}
                        {/*    <i className="fa fa-map-marker fa-2x"></i>*/}
                        {/*    <span className="nav-text">*/}
                        {/*    Maps*/}
                        {/*</span>*/}
                        {/*</a>*/}
                    </li>
                    <li>
                        <a href="#">
                            <i className="fa fa-info fa-2x"></i>
                            <span className="nav-text">
                            User Information
                        </span>
                        </a>
                    </li>
                </ul>

                <ul className="logout">
                    <li>
                        <a href="#">
                            <i className="fa fa-power-off fa-2x"></i>
                            <span className="nav-text">
                            Logout
                        </span>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>


    )
}

export default SideBar;