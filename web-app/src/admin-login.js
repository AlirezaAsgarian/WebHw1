import { useState } from 'react';
import { useNavigate, Outlet, Link } from 'react-router-dom'
import ReactDom from 'react-dom/client';
import Cookies from 'universal-cookie';

const BACKEND_BASE_URL = "http://localhost:8080";
const cookies = new Cookies(null, { path: '/' });

export default function AdminLogin() {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const sendLogin = (e) => {
    e.preventDefault();
    fetch(`${BACKEND_BASE_URL}/users/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
              "username": username,
              "password": password
            })
    })
    .then((response) => {
      console.log(response);
      if (response.status != 200) {
        alert(response.status);
      } else {
        return response.json()
      }
    })
    .then((json) => {
      cookies.set('admin-token', json["token"]);
      console.log(json["token"])
      navigate("/admin/panel"); 
    })
    .catch((err) => console.log(err.message));
  }

  return (
    <div>
      <form onSubmit={sendLogin}>
        <h1>Admin Login</h1>
        <label>username: 
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </label>
        <br/>
        <label>password: 
          <input
            type="text"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        <br/>
        <input type="submit" value="Login"/>
      </form>
    </div>
  );
}
