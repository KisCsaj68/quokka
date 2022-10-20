import { Link } from "react-router-dom";
import Button from "basicComponents/Button";

const Registration = ({
    handleReg,
    fullName,
    email,
    userName,
    passWrd,
    setFullName,
    setEmail,
    setUserName,
    setPassWrd,
    error,
}) => {
    return (
        <form onSubmit={handleReg} method={"POST"}>
            <div className="regContainer">
                <h1>Register</h1>
                {error ? (
                    <p className={"inputError"}>{error}</p>
                ) : (
                    <p>Please fill in this form to create an account.</p>
                )}
                <label htmlFor="fullName">
                    <b>Full name</b>
                </label>
                <input
                    type="text"
                    placeholder="Enter Full name"
                    name="fullName"
                    id="fullName"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                ></input>

                <label htmlFor="emailAddress">
                    <b>Email</b>
                </label>
                <input
                    type="text"
                    placeholder="Enter Email"
                    name="emailAddress"
                    id="emailAddress"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                ></input>

                <label htmlFor="userName">
                    <b>User name</b>
                </label>
                <input
                    type="text"
                    placeholder="Enter User name"
                    name="userName"
                    id="userName"
                    required
                    value={userName}
                    onChange={(e) => setUserName(e.target.value)}
                ></input>

                <label htmlFor="psw">
                    <b>Password</b>
                </label>
                <input
                    type="password"
                    placeholder="Enter Password"
                    name="psw"
                    id="psw"
                    required
                    value={passWrd}
                    onChange={(e) => setPassWrd(e.target.value)}
                ></input>
                {/*<button type={"submit"}> reg</button>*/}
                <Button
                    className={"regButton"}
                    text={"Registration"}
                    btnType="submit"
                ></Button>
            </div>
            <div className="regAfterContainer signin">
                <p>
                    Already have an account? <Link to="/login">Sign in</Link>.
                </p>
            </div>
        </form>
    );
};

export default Registration;
