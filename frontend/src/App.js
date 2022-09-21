import NavigationBar from "./NavigationBar"
import SideBar from "./SideBar"
import {Link, Route, Routes, useNavigate} from "react-router-dom";
import Home from "./Home";
import Registration from "./Registration"
import Stock from "./Stock";
import LogIn from "./LogIn";
import Crypto from "./Crypto";
import {useState} from "react";
import apiRequest from "./apiRequest";

function App() {
    const [fullName, setFullName] = useState('');
    const [email_address, setEmail_address] = useState('');
    const [userName, setUserName] = useState('');
    const [passWrd, setPassWrd] = useState('');
    const history = useNavigate();

    const handleRegistration = async (e) => {
        e.preventDefault();
        const newUser = {fullName, email_address: email_address, userName, password: passWrd}
        console.log(newUser)
        try {
            const response = await apiRequest.post('/api/v1/user', newUser);
            setEmail_address("");
            setPassWrd("");
            setFullName("");
            setEmail_address("");
            // setUserName("");
            history('/login');
        } catch (err) {
            console.log(`Error: ${err.message}`);
        }

    }

    const addNewUser = async (fullName, email, userName, passWrd) => {
        const newUser = {fullName, email, userName, passWrd}
        const optionPost = {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(newUser)
        };
        const response = await apiRequest("/api/v1/user", optionPost, null);


    }
    return (
        <div className="App">
            <NavigationBar/>
            <SideBar/>
            <div className="content">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/registration"
                           element={<Registration
                                        handleReg={handleRegistration}
                                        fullName={fullName}
                                        setFullName={setFullName}
                                        userName={userName}
                                        setUserName={setUserName}
                                        email={email_address}
                                        setEmail={setEmail_address}
                                        passWrd={[passWrd]}
                                        setPassWrd={setPassWrd}
                                    />}
                    />
                    <Route path="/login" element={<LogIn />} />
                    <Route path="/stock" element={<Stock />} />
                    <Route path="/crypto" element={<Crypto />} />
                </Routes>
            </div>

        </div>
    );
}

export default App;
