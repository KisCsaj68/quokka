import { useState } from "react";
import { postNewUser } from "utils/apiRequest.js";
import { FormField } from "basicComponents/FormField";

const NewRegistartion = () => {
    const [regFormVals, setRegFormVals] = useState({
        fullName: "",
        emailAddress: "",
        userName: "",
        password: "",
    });
    const [err, setErr] = useState("");
    const changeHandler = (e) => {
        setRegFormVals((prevVals) => {
            return { ...prevVals, [e.target.name]: e.taget.value };
        });
    };

    const handleRegistration = async () => {
        await postNewUser();
    };
    return (
        <div className="regContainer">
            <h1>Register</h1>
            <FormField
                name="fullName"
                text="Full name"
                type="text"
                onChangeHandler={changeHandler}
            ></FormField>
            <FormField
                name="emailAddress"
                text="Email"
                type="email"
                onChangeHandler={changeHandler}
            ></FormField>
            <FormField
                name="userName"
                text="Username"
                type="text"
                onChangeHandler={changeHandler}
            ></FormField>
            <FormField
                name="password"
                text="Password"
                type="password"
                onChangeHandler={changeHandler}
            ></FormField>
        </div>
    );
};
export default NewRegistartion;
