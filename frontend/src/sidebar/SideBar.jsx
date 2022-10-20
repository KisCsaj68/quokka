import { SideBarListItem } from "./SideBarListItem.jsx";

const SideBar = () => {
    return (
        <div className="area">
            <nav className="main-menu">
                <ul>
                    <SideBarListItem
                        pathTo="#"
                        itemCSS="fa-bar-chart-o"
                        text="Positions"
                    />
                    <SideBarListItem
                        pathTo="#"
                        itemCSS="fa-table"
                        text="Orders"
                    />
                    <SideBarListItem
                        pathTo="#"
                        itemCSS="fa-info"
                        text="User Information"
                    />
                </ul>

                <ul className="logout">
                    <SideBarListItem
                        pathTo="#"
                        itemCSS="fa-power-off"
                        text="Logout"
                    />
                </ul>
            </nav>
        </div>
    );
};

export default SideBar;
