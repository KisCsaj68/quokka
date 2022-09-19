import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';

function NavigationBar() {
    return (
        <Navbar bg="dark" variant="dark">
            <Container>
                <Navbar.Brand href="#home">
                    Quokka Trading Platfo®m
                </Navbar.Brand>
                <Nav className="me-auto">
                    <Nav.Link href="/">Home</Nav.Link>
                    <Nav.Link href="/registration">Registration</Nav.Link>
                    <Nav.Link href="/login">LogIn</Nav.Link>
                    <Nav.Link href="/stock">Stock</Nav.Link>
                    <Nav.Link href="/crypto">Crypto</Nav.Link>
                </Nav>
            </Container>
        </Navbar>

    );
}

export default NavigationBar;