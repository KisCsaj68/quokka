import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import {Link} from "react-router-dom";

function NavigationBar() {
    return (
        <Navbar bg="dark" variant="dark">
            <Container>
                <Navbar.Brand href="#home">
                    Quokka Trading Platfo®m
                </Navbar.Brand>
                <Nav className="me-auto">
                    <Link className="link-light" to="/">Home </Link>
                    <Link className="link-light" to="/registration">Registration</Link>
                    <Link className="link-light" to="/login">LogIn</Link>
                    <Link className="link-light" to="/stock">Stock</Link>
                    <Link className="link-light" to="/crypto">Crypto</Link>
                </Nav>
            </Container>
        </Navbar>

    );
}

export default NavigationBar;