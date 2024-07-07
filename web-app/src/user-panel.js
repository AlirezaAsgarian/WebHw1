import { useState, useEffect } from 'react';
import { Redirect, Outlet, useNavigate } from 'react-router-dom'
import ReactDom from 'react-dom/client';
import Cookies from 'universal-cookie';

const BACKEND_BASE_URL = "http://localhost:8080";
const cookies = new Cookies(null, { path: '/' });

export default function Panel() {

  const [apiTokens, setApiTokens] = useState([]);
  const [tokenName, setTokenName] = useState("");
  const [tokenExpire, setTokenExpire] = useState("");
  const userToken = cookies.get("user-token");
  const username = cookies.get("user-name");
  const navigate = useNavigate();

  useEffect(() => {
    fetch(`${BACKEND_BASE_URL}/user/api-tokens?username=${username}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${userToken}`
      }
    })
    .then((response) => {
      // console.log(response);
      if (response.status != 200) {
        alert(response.status);
      }
      return response.json();
    })
    .then((json) => {
      console.log(json);
      const users = json["tokens"];
      users.sort()
      console.log(users);
      setApiTokens(users);
    })
    .catch((err) => console.log(err.message));
  }, [])

  const addToken = (e) => {
    e.preventDefault();
    fetch(`${BACKEND_BASE_URL}/user/api-tokens?username=${username}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${userToken}`
      },
      body: JSON.stringify({
              "name": tokenName,
              "expireDate": tokenExpire
            })
    })
    .then((response) => {
      console.log(response);
      if (response.status != 201) {
        alert(response.status);
      } else {
        window.location.reload()
      }
    })
    .catch((err) => console.log(err.message));
  }

  return (
    <>
      <h1>Welcome, {username}</h1>
      <table>
        <tr>
          <th>Username</th>
          <th>Expire Date</th>
          <th>Revoke</th>
        </tr>
        {apiTokens.map((token) => <TokenEntry token={token} />)}
      </table>
      <form onSubmit={addToken}>
        <h1>Add API Token</h1>
        <label>name: 
          <input
            type="text"
            value={tokenName}
            onChange={(e) => setTokenName(e.target.value)}
          />
        </label>
        <label>expire date: 
          <input
            type="text"
            value={tokenExpire}
            onChange={(e) => setTokenExpire(e.target.value)}
          />
        </label>
        <input type="submit"/>
      </form>
    </>
  );
}

function TokenEntry({token}) {

  const [isActive, setIsActive] = useState(token.active);
  const userToken = cookies.get("user-token");

  const revokeToken = (e) => {
    console.log(isActive)
    if (!isActive) return;
    fetch(`${BACKEND_BASE_URL}/user/api-tokens`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${userToken}`
      },
      body: JSON.stringify({
              "name": token.name,
              "expireDate": "fake"
            })
    })
    .then((response) => {
      console.log(response);
      if (response.status != 200) {
        alert(response.status);
      }
      setIsActive(false)
    })
    .catch((err) => console.log(err.message));
  }

  return (
    <tr>
      
      <td>{isActive ? token.name : <del>{token.name}</del>}</td>
      <td>{token.expireDate}</td>
      <td>
          <button onClick={revokeToken} >revoke</button>
      </td>

    </tr>
  )
}
