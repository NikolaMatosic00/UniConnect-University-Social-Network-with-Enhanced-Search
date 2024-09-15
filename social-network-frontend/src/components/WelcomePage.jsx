import React from "react";
import { Link } from "react-router-dom";
import "./css/WelcomePage.css";

const WelcomePage = () => {
  return (
    <div className="container">
      <h1>Welcome to Fakebook</h1>
      <p>A place to connect with friends and the world around you.</p>
      <div className="buttons">
        <Link to="/register" className="button">
          Register
        </Link>
        <Link to="/login" className="button">
          Login
        </Link>
      </div>
    </div>
  );
};

export default WelcomePage;
