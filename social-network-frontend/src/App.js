import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Register from "./components/Register";
import Login from "./components/Login";
import WelcomePage from "./components/WelcomePage";
import FeedPage from "./components/FeedPage";
import GroupsPage from "./components/GroupsPage";
// import UserProfile from "./components/UserProfile";
import "./App.css";
import ProfilePage from "./components/ProfilePage";
import Friends from "./components/Friends";

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          {/* <Route path="/user/:id" component={UserProfile} /> */}
          <Route path="/" element={<WelcomePage />} />
          <Route path="/feed" element={<FeedPage />} />
          <Route path="/groups" element={<GroupsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/friends" element={<Friends />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
