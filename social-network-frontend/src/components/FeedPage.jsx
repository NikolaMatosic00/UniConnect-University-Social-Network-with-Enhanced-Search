import React, { useEffect, useState } from "react";
import axios from "axios";
import "./css/FeedPage.css";
import { useNavigate } from "react-router-dom";
import Modal from "react-modal";

const FeedPage = () => {
  const navigate = useNavigate();
  const [error, setError] = useState(null);
  const [posts, setPosts] = useState([]);
  const [selectedPost, setSelectedPost] = useState(null);
  const [replyToCommentId, setReplyToCommentId] = useState(null);
  const [replyText, setReplyText] = useState("");
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [newPost, setNewPost] = useState({
    userId: "",
    title: "",
    pdfFile: null,
  });
  const [searchParams, setSearchParams] = useState({
    title: "",
    content: "",
    minLikes: "",
    maxLikes: "",
  });

  const userId = localStorage.getItem("userId");
  useEffect(() => {
    const fetchPosts = async () => {
      try {
        let response;

        if (userId == 5) {
          // Ako je userId jednak 5, pozovi getAllPosts endpoint
          response = await axios.get("http://localhost:8080/api/posts/getall");
        } else {
          // U suprotnom, koristi endpoint za određenog korisnika
          response = await axios.get(
            "http://localhost:8080/api/posts/" + userId
          );
        }

        setPosts(response.data);
        console.log(response.data);
      } catch (error) {
        console.error("Error fetching posts:", error);
      }
    };

    fetchPosts();
  }, [userId]);

  const styles = {
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

  const handleOpenModal = () => {
    setModalIsOpen(true);
  };

  const handleCloseModal = () => {
    setModalIsOpen(false);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewPost((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e) => {
    setNewPost((prev) => ({
      ...prev,
      pdfFile: e.target.files[0],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const formData = new FormData();
      formData.append("title", newPost.title);
      formData.append("userId", userId);
      formData.append("pdfFile", newPost.pdfFile);

      console.log(newPost.name);
      console.log(newPost.pdfFile);
      console.log(userId);

      await axios.post("http://localhost:8080/api/posts/create", formData, {
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

  const handleReact = async (postId, reactionType) => {
    try {
      await axios.post(
        `http://localhost:8080/api/reactions/${reactionType}`,
        null,
        {
          params: {
            postId,
            userId: parseInt(localStorage.getItem("userId")),
          },
        }
      );

      let response;
      const userId = parseInt(localStorage.getItem("userId"));
      if (userId === 5) {
        // Ako je userId jednak 5, pozovi getAllPosts endpoint
        response = await axios.get("http://localhost:8080/api/posts/getall");
      } else {
        // U suprotnom, koristi endpoint za određenog korisnika
        response = await axios.get("http://localhost:8080/api/posts/" + userId);
      }

      setPosts(response.data);
    } catch (error) {
      console.error("Error sending reaction:", error);
    }
  };

  const handleAddComment = async (postId, text) => {
    try {
      await axios.post(`http://localhost:8080/api/comments/add`, null, {
        params: {
          postId,
          userId: parseInt(localStorage.getItem("userId")),
          text,
        },
      });

      let response;
      const userId = parseInt(localStorage.getItem("userId"));
      if (userId === 5) {
        // Ako je userId jednak 5, pozovi getAllPosts endpoint
        response = await axios.get("http://localhost:8080/api/posts/getall");
      } else {
        // U suprotnom, koristi endpoint za određenog korisnika
        response = await axios.get("http://localhost:8080/api/posts/" + userId);
      }

      setPosts(response.data);
    } catch (error) {
      console.error("Error adding comment:", error);
    }
  };

  const handleReply = async () => {
    try {
      await axios.post("http://localhost:8080/api/comments/reply", null, {
        params: {
          parentCommentId: replyToCommentId,
          userId: parseInt(localStorage.getItem("userId")),
          text: replyText,
        },
      });

      let response;
      const userId = parseInt(localStorage.getItem("userId"));
      if (userId === 5) {
        // Ako je userId jednak 5, pozovi getAllPosts endpoint
        response = await axios.get("http://localhost:8080/api/posts/getall");
      } else {
        // U suprotnom, koristi endpoint za određenog korisnika
        response = await axios.get("http://localhost:8080/api/posts/" + userId);
      }

      setPosts(response.data);
      setReplyText("");
      setReplyToCommentId(null);
    } catch (error) {
      console.error("Error replying to comment:", error);
    }
  };

  const openComments = (post) => {
    setSelectedPost(post);
  };

  const closeComments = () => {
    setSelectedPost(null);
    setReplyToCommentId(null);
  };

  const handleReplyButtonClick = (commentId) => {
    setReplyToCommentId(commentId);
  };

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
        "http://localhost:8080/api/posts/search",
        {
          params: filteredParams,
        }
      );
      setPosts(response.data);
      console.log(response.data);
    } catch (error) {
      setError("Error searching groups");
    }
  };

  if (error) return <div>{error}</div>;

  return (
    <div className="page-container">
      {/* Sidebar */}
      <div className="sidebar">
        <h3>Connect</h3>
        <ul>
          <li>
            <a href="/feed">Feed</a>
          </li>
          <li>
            <a href="/groups">Groups</a>
          </li>
          <li>
            <a href="/friends">Friends</a>
          </li>
          <li>
            <a href="/profile">Profile</a>
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
          <button
            onClick={handleOpenModal}
            style={styles.joinButton}
            onMouseOver={(e) =>
              (e.currentTarget.style.backgroundColor = "#ddd")
            }
            onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#eee")}
          >
            Create New Post
          </button>
        </ul>
      </div>

      {/* Main content (Feed) */}
      <div className="feed-container">
        <div style={styles.searchBar}>
          <input
            type="text"
            placeholder="title"
            value={searchParams.title}
            onChange={(e) =>
              setSearchParams({ ...searchParams, title: e.target.value })
            }
            style={styles.searchInput}
          />
          <input
            type="text"
            placeholder="Content"
            value={searchParams.content}
            onChange={(e) =>
              setSearchParams({ ...searchParams, content: e.target.value })
            }
            style={styles.searchInput}
          />
          <input
            type="number"
            placeholder="Min Likes"
            value={searchParams.minLikes}
            onChange={(e) =>
              setSearchParams({ ...searchParams, minLikes: e.target.value })
            }
            style={styles.searchInput}
          />
          <input
            type="number"
            placeholder="Max Likes"
            value={searchParams.maxLikes}
            onChange={(e) =>
              setSearchParams({ ...searchParams, maxLikes: e.target.value })
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
        <h1>Feed</h1>
        <div className="posts">
          {posts.map((post) => (
            <div key={post.id} className="post">
              <div className="post-header">
                <div className="post-username">User:{post.username}</div>
                <div className="post-username">Group:{post.groupName}</div>
                <div className="post-username">Title:{post.title}</div>
                <div className="post-timestamp">
                  {new Date(post.creationDate).toLocaleString()}
                </div>
              </div>
              <div className="post-content">{post.content}</div>
              <div className="post-reactions">
                <button
                  className="reaction-button like"
                  onClick={() => handleReact(post.id, "like")}
                >
                  👍 Like ({post.likeCount})
                </button>
                <button
                  className="reaction-button dislike"
                  onClick={() => handleReact(post.id, "dislike")}
                >
                  👎 Dislike ({post.dislikeCount})
                </button>
                <button
                  className="reaction-button heart"
                  onClick={() => handleReact(post.id, "heart")}
                >
                  ❤️ Heart ({post.heartCount})
                </button>
                <button
                  className="reaction-button comments"
                  onClick={() => openComments(post)}
                >
                  💬 Comments
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Popup for comments */}
      {selectedPost && (
        <div className="popup-container">
          <div className="popup">
            <h3>Comments for {selectedPost.username}'s post</h3>
            <div className="comments-section">
              {[...selectedPost.comments] // Create a copy of the comments array to avoid mutating the original
                .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp)) // Sort by timestamp in ascending order
                .map((comment) => (
                  <div key={comment.id} className="comment">
                    <div className="comment-username">
                      id.{comment.id} {comment.username}
                    </div>
                    <div className="comment-text">{comment.text}</div>
                    <div className="comment-timestamp">
                      {new Date(comment.timestamp).toLocaleString()}
                    </div>
                    {comment.parentId && (
                      <div className="comment-reply-info">
                        Replying to comment ID: {comment.parentId}
                      </div>
                    )}
                    <button
                      className="reply-button"
                      onClick={() => handleReplyButtonClick(comment.id)}
                    >
                      Reply
                    </button>
                  </div>
                ))}
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  if (replyToCommentId) {
                    handleReply();
                  } else {
                    handleAddComment(
                      selectedPost.id,
                      e.target.elements.commentText.value
                    );
                  }
                  closeComments();
                }}
              >
                <input
                  type="text"
                  name="commentText"
                  value={replyText}
                  onChange={(e) => setReplyText(e.target.value)}
                  className="comment-input"
                  placeholder="Write a comment..."
                />
                <button type="submit" className="comment-button">
                  {replyToCommentId ? "Add Reply" : "Add Comment"}
                </button>
              </form>
            </div>
            <button className="close-popup" onClick={closeComments}>
              Close
            </button>
          </div>
        </div>
      )}
      {/* Modal for creating a new group */}
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={handleCloseModal}
        style={styles.modal}
        contentLabel="Create New Group"
      >
        <h2>Create New Post</h2>
        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label htmlFor="title" style={styles.formLabel}>
              Post Title
            </label>
            <input
              type="text"
              id="title"
              name="title"
              value={newPost.title}
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
    </div>
  );
};

export default FeedPage;
