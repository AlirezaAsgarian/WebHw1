import { useState, useEffect } from 'react';
import { Redirect, Outlet } from 'react-router-dom'
import ReactDom from 'react-dom/client';
import Cookies from 'universal-cookie';

const BACKEND_BASE_URL = "http://localhost:8080";
const cookies = new Cookies(null, { path: '/' });

export default function Panel() {

  const [userEntries, setUserEntries] = useState([]);
  const token = cookies.get("admin-token");

  useEffect(() => {
    fetch(`${BACKEND_BASE_URL}/admin/users`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
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
      const users = json["users"];
      users.sort()
      console.log(users);
      setUserEntries(users);
    })
    .catch((err) => console.log(err.message));
  }, [])

  return (
    <table>
      <tr>
        <th>Username</th>
        <th>Date Joined</th>
        <th>Active</th>
      </tr>
      {userEntries
      .filter((user) => user.username != "admin")
      .map((user) => <UserEntry user={user} />)}
    </table>
  );
}

function UserEntry({user}) {

  const [isActive, setIsActive] = useState(user.active);
  const token = cookies.get("admin-token");

  const onActiveChange = (e) => {
    console.log(user.active)
    fetch(`${BACKEND_BASE_URL}/admin/users?username=${user.username}&active=${!isActive}`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
    .then((response) => {
      console.log(response);
      if (response.status != 200) {
        alert(response.status);
      }
      setIsActive(!isActive)
    })
    .catch((err) => console.log(err.message));
  }

  return (
    <tr>
      <td>{user.username}</td>
      <td>{user.dateJoined}</td>
      <td class="che">
          <input type="checkbox" onChange={onActiveChange} checked={isActive} />
      </td>
    </tr>
  )
}
