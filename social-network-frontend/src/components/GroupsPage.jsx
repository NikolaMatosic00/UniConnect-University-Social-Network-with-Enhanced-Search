import React, { useEffect, useState } from "react";
import axios from "axios";
import Modal from "react-modal";
import GroupPage from "./GroupPage";

// Inline CSS in JS

const suspendModalStyles = {
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
};

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
  groupsContainer: {
    flexGrow: 1,
    padding: "20px",
    backgroundColor: "white",
  },
  searchBar: {
    display: "flex",
    justifyContent: "center",
    marginBottom: "20px",
  },
  searchInput: {
    width: "80%",
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "8px",
  },
  groupCard: {
    display: "flex",
    alignItems: "center",
    backgroundColor: "white",
    border: "1px solid #ddd",
    borderRadius: "8px",
    padding: "20px",
    marginBottom: "20px",
    boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",
  },
  groupImage: {
    width: "80px",
    height: "80px",
    borderRadius: "8px",
    marginRight: "20px",
  },
  groupTitle: {
    margin: "0",
    fontSize: "18px",
  },
  groupDescription: {
    margin: "5px 0",
    fontSize: "14px",
    color: "#555",
  },
  joinButton: {
    marginLeft: "auto",
    display: "flex",
    alignItems: "center",
    padding: "10px",
    backgroundColor: "#eee",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
  },
  joinButtonHover: {
    backgroundColor: "#ddd",
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
  formGroup: {
    marginBottom: "15px",
  },
  formLabel: {
    display: "block",
    marginBottom: "5px",
  },
  formInput: {
    width: "100%",
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "8px",
  },
  formButton: {
    padding: "10px",
    backgroundColor: "#429bf5",
    border: "none",
    borderRadius: "8px",
    color: "white",
    cursor: "pointer",
  },
};

const GroupsPage = () => {
  const [suspendModalIsOpen, setSuspendModalIsOpen] = useState(false);
  const [suspendReason, setSuspendReason] = useState("");
  const [selectedGroupId, setSelectedGroupId] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null); // stanje za odabranu grupu
  const [isHeAdmin, setIsHeAdmin] = useState(null); // stanje za odabranu grupu
  const [groupsForSidebar, setGroupsForSidebar] = useState([]);
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams, setSearchParams] = useState({
    name: "",
    description: "",
    minPostCount: "",
    maxPostCount: "",
  });
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [newGroup, setNewGroup] = useState({
    name: "",
    userId: "",
    pdfFile: null,
  });

  const userId = localStorage.getItem("userId");

  // Open suspend modal
  const handleOpenSuspendModal = (groupId) => {
    setSelectedGroupId(groupId);
    setSuspendModalIsOpen(true);
  };

  // Close suspend modal
  const handleCloseSuspendModal = () => {
    setSuspendModalIsOpen(false);
    setSuspendReason("");
  };

  // Handle suspend group request
  const handleSuspendGroup = async () => {
    try {
      await axios.post(
        `http://localhost:8080/api/groups/suspend/${selectedGroupId}`,
        null,
        {
          params: {
            reason: suspendReason,
            userId: userId,
          },
        }
      );
      alert("Group suspended successfully!");
      handleCloseSuspendModal();
    } catch (error) {
      alert("Failed to suspend group.");
    }
  };

  const handleOpenModal = () => {
    setModalIsOpen(true);
  };

  const handleCloseModal = () => {
    setModalIsOpen(false);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewGroup((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e) => {
    setNewGroup((prev) => ({
      ...prev,
      pdfFile: e.target.files[0],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const formData = new FormData();
      formData.append("name", newGroup.name);
      formData.append("userId", userId);
      formData.append("pdfFile", newGroup.pdfFile);

      console.log(newGroup.name);
      console.log(newGroup.pdfFile);
      console.log(userId);

      await axios.post("http://localhost:8080/api/groups/create", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      alert("Group created successfully!");
      handleCloseModal();
    } catch (error) {
      alert("Failed to create group.");
    }
  };

  // Fetch groups data when component mounts
  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const userId = localStorage.getItem("userId");
        const response = await axios.get(
          `http://localhost:8080/api/groups/get-all-groups-of-user/${userId}`
        );
        setGroupsForSidebar(response.data);
        console.log(response.data);
      } catch (error) {
        setError("Error fetching groups data");
      } finally {
        setLoading(false);
      }
    };

    fetchGroups();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  // Handle search
  const handleSearch = async () => {
    try {
      // Filter out empty search parameters
      const filteredParams = Object.keys(searchParams).reduce((acc, key) => {
        if (searchParams[key]) {
          acc[key] = searchParams[key];
        }
        return acc;
      }, {});

      const response = await axios.get(
        "http://localhost:8080/api/groups/search",
        {
          params: filteredParams,
        }
      );
      setGroups(response.data);
      console.log(response.data);
    } catch (error) {
      setError("Error searching groups");
    }
  };

  // Handle join request
  const handleJoinRequest = async (groupId) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/api/groups/send-join-request",
        null,
        {
          params: {
            groupId,
            userId,
          },
        }
      );
      alert(response.data); // Show response message
    } catch (error) {
      alert("Failed to send join request.");
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  if (selectedGroup) {
    return (
      <GroupPage
        groupName={selectedGroup}
        groupId={selectedGroupId}
        isAdmin={isHeAdmin}
      />
    ); // prikazujemo GroupPage kada je grupa odabrana
  }

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
        </ul>
        <h3 style={styles.sidebarTitle}>Your Groups</h3>

        <ul style={styles.sidebarList}>
          {groupsForSidebar.map((group) => (
            <li
              key={group.groupId}
              style={
                group.admin
                  ? { ...styles.sidebarItem, ...styles.sidebarItemAdmin }
                  : { ...styles.sidebarItem, ...styles.sidebarItemMember }
              }
              onClick={() => {
                setSelectedGroup(group.groupName);
                setSelectedGroupId(group.groupId);
                setIsHeAdmin(group.admin);
              }} // ažuriramo stanje kada se klikne na grupu
            >
              <a href="#" style={styles.sidebarLink}>
                {group.groupName}
              </a>
            </li>
          ))}
        </ul>
        <button
          onClick={handleOpenModal}
          style={styles.joinButton}
          onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#ddd")}
          onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#eee")}
        >
          Create New Group
        </button>
      </div>

      {/* Main content */}
      <div style={styles.groupsContainer}>
        <div style={styles.searchBar}>
          <input
            type="text"
            placeholder="Group Name"
            value={searchParams.name}
            onChange={(e) =>
              setSearchParams({ ...searchParams, name: e.target.value })
            }
            style={styles.searchInput}
          />
          <input
            type="text"
            placeholder="Description"
            value={searchParams.description}
            onChange={(e) =>
              setSearchParams({ ...searchParams, description: e.target.value })
            }
            style={styles.searchInput}
          />
          <input
            type="number"
            placeholder="Min Post Count"
            value={searchParams.minPostCount}
            onChange={(e) =>
              setSearchParams({ ...searchParams, minPostCount: e.target.value })
            }
            style={styles.searchInput}
          />
          <input
            type="number"
            placeholder="Max Post Count"
            value={searchParams.maxPostCount}
            onChange={(e) =>
              setSearchParams({ ...searchParams, maxPostCount: e.target.value })
            }
            style={styles.searchInput}
          />
          <button
            onClick={handleSearch}
            style={styles.joinButton}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor =
                styles.joinButtonHover.backgroundColor)
            }
            onMouseOut={(e) =>
              (e.currentTarget.style.backgroundColor =
                styles.joinButton.backgroundColor)
            }
          >
            Search
          </button>
        </div>

        {/* Group cards */}
        {groups.map((group) => {
          // Check if the user is already a member of the group
          const isMember = groupsForSidebar.some(
            (userGroup) => userGroup.groupId === group.id
          );

          return (
            <div key={group.id} style={styles.groupCard}>
              <img
                src="https://via.placeholder.com/80"
                alt="Group"
                style={styles.groupImage}
              />
              <div>
                <h3 style={styles.groupTitle}>{group.name}</h3>
                <p style={styles.groupDescription}>{group.description}</p>
                <p>Posts Count: {group.postsCount}</p>
                <p>Average Likes: {group.averageLikes}</p>
              </div>

              {userId === "5" && (
                <button
                  onClick={() => handleOpenSuspendModal(group.id)}
                  style={{
                    ...styles.joinButton,
                    backgroundColor: "red",
                    color: "white",
                  }}
                >
                  Suspend Group
                </button>
              )}

              {isMember ? (
                <button
                  style={{
                    ...styles.joinButton,
                    backgroundColor: "#ddd", // Disabled style
                    cursor: "not-allowed",
                  }}
                  disabled
                >
                  Already a Member
                </button>
              ) : (
                <button
                  style={styles.joinButton}
                  onClick={() => handleJoinRequest(group.id)}
                  onMouseOver={(e) =>
                    (e.currentTarget.style.backgroundColor = "#ddd")
                  }
                  onMouseOut={(e) =>
                    (e.currentTarget.style.backgroundColor = "#eee")
                  }
                >
                  Join Group
                </button>
              )}
            </div>
          );
        })}
      </div>
      {/* Modal for creating a new group */}
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={handleCloseModal}
        style={styles.modal}
        contentLabel="Create New Group"
      >
        <h2>Create New Group</h2>
        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label htmlFor="name" style={styles.formLabel}>
              Group Name
            </label>
            <input
              type="text"
              id="name"
              name="name"
              value={newGroup.name}
              onChange={handleInputChange}
              style={styles.formInput}
              required
            />
          </div>
          <div style={styles.formGroup}>
            <label htmlFor="userId" style={styles.formLabel}>
              User ID
            </label>
            <input
              type="text"
              id="userId"
              name="userId"
              value={userId}
              style={styles.formInput}
              disabled
            />
          </div>
          <div style={styles.formGroup}>
            <label htmlFor="pdfFile" style={styles.formLabel}>
              Upload PDF
            </label>
            <input
              type="file"
              id="pdfFile"
              name="pdfFile"
              onChange={handleFileChange}
              style={styles.formInput}
              accept=".pdf"
              required
            />
          </div>
          <button type="submit" style={styles.formButton}>
            Create Group
          </button>
          <button
            type="button"
            onClick={handleCloseModal}
            style={{ ...styles.formButton, backgroundColor: "#f44336" }}
          >
            Cancel
          </button>
        </form>
      </Modal>
      {/* Suspend group modal */}
      <Modal
        isOpen={suspendModalIsOpen}
        onRequestClose={handleCloseSuspendModal}
        style={suspendModalStyles}
        contentLabel="Suspend Group"
      >
        <h2>Suspend Group</h2>
        <textarea
          placeholder="Reason for suspension"
          value={suspendReason}
          onChange={(e) => setSuspendReason(e.target.value)}
          style={styles.formInput}
        />
        <button onClick={handleSuspendGroup} style={styles.formButton}>
          Confirm Suspend
        </button>
        <button onClick={handleCloseSuspendModal} style={styles.formButton}>
          Cancel
        </button>
      </Modal>
    </div>
  );
};

export default GroupsPage;
