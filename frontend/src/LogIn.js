import Button from "./Button";
import {api} from "./apiRequest";
import {useContext} from "react";
import AuthContext from "./context/AuthProvider";

const LogIn = ({userName, passWrd, setPassWrd, setUserName,error}) => {
    const { setToken } = useContext(AuthContext)

    const handleLogin = async (e) => {
        e.preventDefault();
        const data = {user_name: userName, password:passWrd};
        try {
            const response = await api.post('/api/v1/login', data);
            setToken(response.headers['authorization']);

        } catch (err) {
            console.log(err);
        }
    }

    return (
        <form onSubmit={handleLogin} method={"POST"}>
            <div className="logInContainer">
                <h1>Log in</h1>
                {error ? <p className={"inputError"}>You already registered. Please log into Your account!</p> : <p>Please fill in this form to log into your Account.</p>}
                <label htmlFor="userName"><b>User name</b></label>
                <input
                    type="text"
                    placeholder="Enter User name"
                    name="userName" id="userName"
                    required
                    value={userName}
                    onChange={(e) => setUserName(e.target.value)}>
                </input>

                <label htmlFor="psw"><b>Password</b></label>
                <input
                    type="password"
                    placeholder="Enter Password"
                    name="psw"
                    id="psw"
                    required
                    value={passWrd}
                    onChange={(e) => setPassWrd(e.target.value)}>
                </input>
                <Button className={"logInButton"} text={"Log in"} btnType={"submit"}></Button>
            </div>
        </form>
    )
}

export default LogIn;