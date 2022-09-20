import Button from "./Button";
import {Link} from "react-router-dom";

const LogIn = () => {
    return (
        <form >
            <div className="logInContainer">
                <h1>Log in</h1>
                <p>Please fill in this form to log into your Account.</p>


                <label htmlFor="userName"><b>User name</b></label>
                <input type="text" placeholder="Enter User name" name="userName" id="userName" required></input>

                <label htmlFor="psw"><b>Password</b></label>
                <input type="password" placeholder="Enter Password" name="psw" id="psw" required></input>

                {/*<label htmlFor="psw-repeat"><b>Repeat Password</b></label>*/}
                {/*<input type="password" placeholder="Repeat Password" name="psw-repeat" id="psw-repeat" required></input>*/}
                <Button className={"logInButton"} text={"Log in"} btnType={"submit"}></Button>
            </div>
            {/*<div className="regAfterContainer signin">*/}
            {/*    <p>Already have an account? <Link to="/login">Sign in</Link>.</p>*/}
            {/*</div>*/}
        </form>
    )
}

export default LogIn;