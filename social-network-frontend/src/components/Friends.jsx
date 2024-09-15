import React, { useEffect, useState } from "react";
import axios from "axios";

// Inline CSS in JS (including sidebar and popup styles)
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
  friendsContainer: {
    flexGrow: 1,
    padding: "20px",
    backgroundColor: "white",
  },
  searchBar: {
    marginBottom: "20px",
  },
  searchInput: {
    width: "100%",
    padding: "10px",
    fontSize: "16px",
    borderRadius: "5px",
    border: "1px solid #ccc",
  },
  friendItem: {
    padding: "10px",
    borderBottom: "1px solid #ddd",
  },
  existingFriendsTitle: {
    marginTop: "30px",
    fontSize: "20px",
    borderTop: "1px solid #ddd",
    paddingTop: "10px",
  },
  sendRequestButton: {
    backgroundColor: "#429bf5",
    color: "white",
    border: "none",
    padding: "10px 20px",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "16px",
    transition: "background-color 0.3s ease, transform 0.2s ease",
    marginTop: "10px",
  },
  sendRequestButtonHover: {
    backgroundColor: "#357abd",
  },
  sendRequestButtonActive: {
    transform: "scale(0.98)",
  },
  sendRequestButtonDisabled: {
    backgroundColor: "#ddd",
    color: "#aaa",
    cursor: "not-allowed",
  },
  modal: {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    backgroundColor: "rgba(0, 0, 0, 0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
  },
  modalContent: {
    backgroundColor: "white",
    padding: "20px",
    borderRadius: "8px",
    width: "400px",
    position: "relative",
  },
  close: {
    position: "absolute",
    top: "10px",
    right: "10px",
    cursor: "pointer",
    fontSize: "24px",
  },
  requestItem: {
    marginBottom: "10px",
  },
  acceptButton: {
    backgroundColor: "#429bf5",
    color: "white",
    border: "none",
    padding: "5px 10px",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "14px",
  },
};

const Friends = () => {
  const [friends, setFriends] = useState([]);
  const [filteredFriends, setFilteredFriends] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showPopup, setShowPopup] = useState(false);
  const [requests, setRequests] = useState([]);
  const [popupLoading, setPopupLoading] = useState(true);
  const [popupError, setPopupError] = useState(null);

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        const userId = localStorage.getItem("userId");
        const response = await axios.get(
          `http://localhost:8080/api/users/get-friends/${userId}`
        );
        setFriends(response.data);
        setFilteredFriends(response.data);
        setLoading(false);
      } catch (error) {
        setError("Error fetching friends data");
        setLoading(false);
      }
    };

    fetchFriends();
  }, []);

  useEffect(() => {
    const searchFriends = async () => {
      if (searchTerm.trim()) {
        try {
          const response = await axios.get(
            `http://localhost:8080/api/users/search-for-new-friends?keyword=${searchTerm}`
          );
          setFilteredFriends(response.data);
        } catch (error) {
          setError("Error searching friends");
        }
      } else {
        setFilteredFriends(friends); // Reset to the original list if search term is empty
      }
    };

    searchFriends();
  }, [searchTerm, friends]);

  const sendFriendRequest = async (toUserId) => {
    try {
      const fromUserId = localStorage.getItem("userId");
      await axios.post(`http://localhost:8080/api/friend-requests/send`, null, {
        params: { fromUserId, toUserId },
      });
      alert("Friend request sent!");
    } catch (error) {
      setError("Error sending friend request");
    }
  };

  const fetchFriendRequests = async () => {
    try {
      const userId = localStorage.getItem("userId");
      const response = await axios.get(
        `http://localhost:8080/api/friend-requests/get-users-friend-requests/${userId}`
      );
      setRequests(response.data);
      setPopupLoading(false);
    } catch (error) {
      setPopupError("Error fetching friend requests");
      setPopupLoading(false);
    }
  };

  const handleAcceptRequest = async (requestId) => {
    try {
      await axios.post(
        `http://localhost:8080/api/friend-requests/accept/${requestId}`
      );
      alert("Friend request accepted!");
      setRequests((prevRequests) =>
        prevRequests.filter((req) => req.id !== requestId)
      );
    } catch (error) {
      setPopupError("Error accepting friend request");
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

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
          <li style={styles.sidebarItem}>
            <button
              style={styles.sendRequestButton}
              onClick={() => {
                fetchFriendRequests();
                setShowPopup(true);
              }}
            >
              Friend Requests
            </button>
          </li>
        </ul>
      </div>

      {/* Main content */}
      <div style={styles.friendsContainer}>
        {/* Search Bar */}
        <div style={styles.searchBar}>
          <input
            type="text"
            placeholder="Search friends..."
            style={styles.searchInput}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Render search results */}
        <div>
          <h4>Search Results</h4>
          {filteredFriends.length > 0 ? (
            filteredFriends.map((friend) => (
              <div key={friend.id} style={styles.friendItem}>
                <h4>
                  {friend.firstName} {friend.lastName}
                </h4>
                <button
                  style={
                    friends.some((f) => f.id === friend.id)
                      ? {
                          ...styles.sendRequestButton,
                          ...styles.sendRequestButtonDisabled,
                        }
                      : styles.sendRequestButton
                  }
                  onMouseOver={(e) =>
                    (e.currentTarget.style.backgroundColor =
                      styles.sendRequestButtonHover.backgroundColor)
                  }
                  onMouseOut={(e) =>
                    (e.currentTarget.style.backgroundColor =
                      styles.sendRequestButton.backgroundColor)
                  }
                  onMouseDown={(e) =>
                    (e.currentTarget.style.transform =
                      styles.sendRequestButtonActive.transform)
                  }
                  onMouseUp={(e) =>
                    (e.currentTarget.style.transform = "scale(1)")
                  }
                  onClick={() =>
                    !friends.some((f) => f.id === friend.id) &&
                    sendFriendRequest(friend.id)
                  }
                  disabled={friends.some((f) => f.id === friend.id)}
                >
                  Send Friend Request
                </button>
              </div>
            ))
          ) : (
            <div>No friends found</div>
          )}
        </div>

        {/* Existing friends list */}
        <div style={styles.existingFriendsTitle}>
          <h4>Existing Friends</h4>
          {friends.length > 0 ? (
            friends.map((friend) => (
              <div key={friend.id} style={styles.friendItem}>
                <h4>
                  {friend.firstName} {friend.lastName}
                </h4>
                {/* Additional details about the friend */}
              </div>
            ))
          ) : (
            <div>No existing friends</div>
          )}
        </div>
      </div>

      {/* Popup for friend requests */}
      {showPopup && (
        <div style={styles.modal}>
          <div style={styles.modalContent}>
            <span style={styles.close} onClick={() => setShowPopup(false)}>
              &times;
            </span>
            <h3>Friend Requests</h3>
            {popupLoading ? (
              <div>Loading...</div>
            ) : popupError ? (
              <div>{popupError}</div>
            ) : (
              <div>
                {requests.length > 0 ? (
                  requests.map((request) => (
                    <div key={request.id} style={styles.requestItem}>
                      <h4>
                        {request.username} ({request.firstName}{" "}
                        {request.lastName})
                      </h4>
                      <button
                        style={styles.acceptButton}
                        onClick={() => handleAcceptRequest(request.id)}
                      >
                        Accept
                      </button>
                    </div>
                  ))
                ) : (
                  <div>No friend requests</div>
                )}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Friends;
