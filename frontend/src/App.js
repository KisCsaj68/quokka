import NavigationBar from "./NavigationBar"
import SideBar from "./SideBar"
import {Link, Route, Routes, useNavigate} from "react-router-dom";
import Home from "./Home";
import Registration from "./Registration"
import Stock from "./Stock";
import LogIn from "./LogIn";
import Crypto from "./Crypto";
import {useState} from "react";
import {regApi} from "./apiRequest";

function App() {
    const [fullName, setFullName] = useState('');
    const [email_address, setEmail_address] = useState('');
    const [userName, setUserName] = useState('');
    const [passWrd, setPassWrd] = useState('');
    const [error, setError] = useState('');
    const history = useNavigate();

    const handleRegistration = async (e) => {
        e.preventDefault();
        const newUser = {fullName, email_address: email_address, userName, password: passWrd}
        try {
            const response = await regApi.post('/api/v1/user', newUser);
            if(response.status < 300) {
                setEmail_address("");
                setPassWrd("");
                setFullName("");
                setEmail_address("");
                setUserName("");
                setError("")
                history('/login');
            }


        } catch (err) {
            const error = err.response.request.response
            if(error === "Email error") {
                setError("Please correct e-mail address!")
                setEmail_address("")
            }
            else if (error === "User error") {
                setError("Please choose another user name!")
                setUserName("")
            }
            else if( error === "Email occupied"){
                setError("You are already registered with this email. Please continue with log in!")
                setTimeout(history("/login"), 5000)
                setEmail_address("");
                setPassWrd("");
                setFullName("");
                setEmail_address("");
                setUserName("");
            }
            console.log(`Error: ${err.message}`);
        }
    }

    return (
        <div className="App">
            <NavigationBar/>
            <SideBar/>
            <div className="content">
                <Routes>
                    <Route path="/" element={<Home setError={setError}/>}/>
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
                               error={error}
                           />}
                    />
                    <Route path="/login" element={<LogIn
                        userName={userName}
                        setUserName={setUserName}
                        passWrd={[passWrd]}
                        setPassWrd={setPassWrd}
                        error={error}
                        setError={setError}
                    />}/>
                    <Route path="/stock" element={<Stock/>}/>
                    <Route path="/crypto" element={<Crypto/>}/>
                </Routes>
            </div>

        </div>
    );
}

export default App;
