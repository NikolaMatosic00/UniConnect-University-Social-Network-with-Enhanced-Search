import React, { useState, useEffect } from "react";
import axios from "axios";
import Modal from "react-modal";

// Inline CSS in JS
const styles = {
  container: {
    display: "flex",
    height: "100vh",
  },
  sidebar: {
    width: "200px",
    backgroundColor: "#333",
    color: "white",
    padding: "20px",
  },
  sidebarTitle: {
    marginTop: "0",
    fontSize: "24px",
    color: "#fff",
  },
  sidebarList: {
    listStyle: "none",
    padding: "0",
  },
  sidebarItem: {
    padding: "10px",
    backgroundColor: "#444",
    marginBottom: "10px",
    borderRadius: "8px",
    cursor: "pointer",
    textAlign: "center",
  },
  sidebarItemHover: {
    backgroundColor: "#555",
  },
  sidebarItemAdmin: {
    backgroundColor: "#429bf5",
  },
  sidebarItemMember: {
    backgroundColor: "#4254f5",
  },
  sidebarLink: {
    color: "white",
    textDecoration: "none",
    fontSize: "18px",
    display: "block",
    padding: "10px",
    borderRadius: "5px",
    transition: "background-color 0.3s ease",
  },
  profilePage: {
    flexGrow: 1,
    padding: "20px",
    backgroundColor: "white",
  },
  profileDetails: {
    marginBottom: "20px",
  },
  profileDetail: {
    fontSize: "16px",
    margin: "10px 0",
  },
  changePassword: {
    marginTop: "20px",
  },
  input: {
    display: "block",
    marginBottom: "10px",
    padding: "10px",
    width: "100%",
    border: "1px solid #ccc",
    borderRadius: "8px",
  },
  button: {
    padding: "10px",
    backgroundColor: "#429bf5",
    border: "none",
    borderRadius: "8px",
    color: "white",
    cursor: "pointer",
    marginTop: "10px",
  },
  buttonHover: {
    backgroundColor: "#3678e6",
  },
  error: {
    color: "red",
    marginTop: "10px",
  },
  modal: {
    overlay: {
      backgroundColor: "rgba(0, 0, 0, 0.5)",
    },
    content: {
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      transform: "translate(-50%, -50%)",
      padding: "20px",
      borderRadius: "8px",
    },
  },
  profileGroups: {
    marginTop: "20px",
  },
  groupItem: {
    fontSize: "16px",
    margin: "5px 0",
  },
};

const ProfilePage = () => {
  const [user, setUser] = useState({});
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [description, setDescription] = useState("");

  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/users/get-user/${userId}`
        );
        setUser(response.data);
        setDescription(response.data.description);
      } catch (error) {
        console.error("Error fetching user details:", error);
      }
    };

    fetchUser();
  }, [userId]);

  const handleChangePassword = async () => {
    if (newPassword !== confirmPassword) {
      setPasswordError("New password and confirmation do not match.");
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/api/users/change-password",
        {
          username: user.username,
          oldPassword,
          newPassword,
        }
      );
      alert(response.data);
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (error) {
      setPasswordError(
        "Failed to change password. Please check your credentials."
      );
    }
  };

  const handleUpdateDescription = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8080/api/users/update-description",
        {
          username: user.username,
          description,
        }
      );
      alert(response.data);
    } catch (error) {
      alert("Failed to update description.");
    }
  };

  const handleOpenModal = () => {
    setModalIsOpen(true);
  };

  const handleCloseModal = () => {
    setModalIsOpen(false);
  };

  return (
    <div style={styles.container}>
      {/* Sidebar */}
      <div style={styles.sidebar}>
        <h3 style={styles.sidebarTitle}>Connect</h3>
        <ul style={styles.sidebarList}>
          <li style={styles.sidebarItem}>
            <a href="/feed" style={styles.sidebarLink}>
              Feed
            </a>
          </li>
          <li style={styles.sidebarItem}>
            <a href="/groups" style={styles.sidebarLink}>
              Groups
            </a>
          </li>
          <li style={styles.sidebarItem}>
            <a href="/friends" style={styles.sidebarLink}>
              Friends
            </a>
          </li>
          <li style={styles.sidebarItem}>
            <a href="/profile" style={styles.sidebarLink}>
              Profile
            </a>
          </li>
          <li>
            <button
              className="button"
              onClick={() => {
                localStorage.removeItem("userId");
                window.location.href = "/login";
              }}
            >
              Logout
            </button>
          </li>
        </ul>
      </div>

      {/* Main content */}
      <div style={styles.profilePage}>
        <h1>Profile</h1>
        <div style={styles.profileDetails}>
          <p style={styles.profileDetail}>
            <strong>Username:</strong> {user.username}
          </p>
          <p style={styles.profileDetail}>
            <strong>Email:</strong> {user.email}
          </p>
          <p style={styles.profileDetail}>
            <strong>First Name:</strong> {user.firstName}
          </p>
          <p style={styles.profileDetail}>
            <strong>Last Name:</strong> {user.lastName}
          </p>
          <p style={styles.profileDetail}>
            <strong>Display Name:</strong> {user.displayName}
          </p>
          <p style={styles.profileDetail}>
            <strong>Description:</strong> {user.description}
          </p>
        </div>

        <div style={styles.profileGroups}>
          <h2>Groups</h2>
          {user.groups &&
            user.groups.map((group, index) => (
              <p key={index} style={styles.groupItem}>
                {group}
              </p>
            ))}
        </div>

        <div style={styles.changePassword}>
          <h2>Change Password</h2>
          <input
            type="password"
            placeholder="Old Password"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            style={styles.input}
          />
          <input
            type="password"
            placeholder="New Password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            style={styles.input}
          />
          <input
            type="password"
            placeholder="Confirm New Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            style={styles.input}
          />
          <button
            onClick={handleChangePassword}
            style={styles.button}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor =
                styles.buttonHover.backgroundColor)
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor =
                styles.button.backgroundColor)
            }
          >
            Change Password
          </button>
          {passwordError && <p style={styles.error}>{passwordError}</p>}
        </div>

        <div style={styles.changePassword}>
          <h2>Update Description</h2>
          <textarea
            placeholder="Enter new description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            style={{ ...styles.input, height: "100px" }}
          />
          <button
            onClick={handleUpdateDescription}
            style={styles.button}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor =
                styles.buttonHover.backgroundColor)
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor =
                styles.button.backgroundColor)
            }
          >
            Update Description
          </button>
        </div>
      </div>

      {/* Modal for profile details */}
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={handleCloseModal}
        style={styles.modal}
        contentLabel="Profile Details"
      >
        <h2>Profile Details</h2>
        <p>
          <strong>Username:</strong> {user.username}
        </p>
        <p>
          <strong>Email:</strong> {user.email}
        </p>
        <p>
          <strong>First Name:</strong> {user.firstName}
        </p>
        <p>
          <strong>Last Name:</strong> {user.lastName}
        </p>
        <p>
          <strong>Display Name:</strong> {user.displayName}
        </p>
        <p>
          <strong>Description:</strong> {user.description}
        </p>
        <button
          onClick={handleCloseModal}
          style={{ ...styles.button, backgroundColor: "#f44336" }}
        >
          Close
        </button>
      </Modal>
    </div>
  );
};

export default ProfilePage;
