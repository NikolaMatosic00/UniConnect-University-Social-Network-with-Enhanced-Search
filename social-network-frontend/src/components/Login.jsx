import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/api";

const Login = () => {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await loginUser(formData);
      localStorage.setItem("userId", response.data.id);

      navigate("/feed");
      // Dodaj logiku za preusmeravanje ili prikaz poruke o uspehu
    } catch (error) {
      console.error("Login failed:", error);
      alert("pogresni kredencijali");
    }
  };

  return (
    <form className="formica" onSubmit={handleSubmit}>
      <input
        type="text"
        name="username"
        placeholder="Username"
        value={formData.username}
        onChange={handleChange}
        required
      />
      <input
        type="password"
        name="password"
        placeholder="Password"
        value={formData.password}
        onChange={handleChange}
        required
      />
      <button className="batoncic" type="submit">
        Login
      </button>
    </form>
  );
};

export default Login;
