import { BrowserRouter, Routes, Route } from "react-router-dom";
import ReactDom from 'react-dom/client';
import AdminLogin from "./admin-login.js"
import AdminPanel from "./admin-panel.js"
import UserLogin from "./user-login.js"
import UserRegister from "./user-register.js"
import UserPanel from "./user-panel.js"
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/admin" element={<AdminLogin/>} />
        <Route path="/admin/panel" element={<AdminPanel/>} />
        <Route path="/" element={<UserLogin/>} />
        <Route path="/register" element={<UserRegister/>} />
        <Route path="/panel" element={<UserPanel/>} />
      </Routes>
    </BrowserRouter>
  );
}

const root = ReactDom.createRoot(document.getElementById('root'));
root.render(<App/>);
