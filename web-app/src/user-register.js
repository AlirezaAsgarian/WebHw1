import { useState } from 'react';
import { useNavigate, Outlet, Link } from 'react-router-dom'
import ReactDom from 'react-dom/client';
import Cookies from 'universal-cookie';

const BACKEND_BASE_URL = "http://localhost:8080";
const cookies = new Cookies(null, { path: '/' });

export default function UserLogin() {

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const navigate = useNavigate();

  const sendLogin = (e) => {
    e.preventDefault();
    fetch(`${BACKEND_BASE_URL}/users/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
              "firstName": firstName,
              "lastName": lastName,
              "username": username,
              "password": password,
              "phoneNumber": phoneNumber,
              "address": address
            })
    })
    .then((response) => {
      console.log(response);
      if (response.status != 200) {
        alert(response.status);
      }
      navigate("/")
    })
    .catch((err) => console.log(err.message));
  }

  return (
    <div>
      <form onSubmit={sendLogin}>
        <label>first name: 
          <input
            type="text"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
          />
        </label>
        <label>last name: 
          <input
            type="text"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
          />
        </label>
        <label>username: 
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </label>
        <label>password: 
          <input
            type="text"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        <label>phone number: 
          <input
            type="text"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
          />
        </label>
        <label>address: 
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
          />
        </label>
        <input type="submit"/>
      </form>
    </div>
  );
}
