import button from "./Button";
import {Link} from "react-router-dom";

const Home = ({setError}) => {
    setError("")
    return (
        <div>
            <img
                src="/eating_quokka.gif"
                width="170"
                height="170"
                alt="quokka-logo"
            />
            <h1 className="intro">Welcome to Quokka Trading Platfo®m</h1>
            <div className="button">
                <Link to={"/registration"}>
                    <button  type={"button"} className={"buttonHome"}> Sing up for free </button>
                </Link>
            </div>
        </div>


    )
}

export default Home;