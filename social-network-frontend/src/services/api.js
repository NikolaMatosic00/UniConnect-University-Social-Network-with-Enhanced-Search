import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api"; // Promeni na odgovarajuću URL adresu backend-a

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Registracija korisnika
export const registerUser = (registrationRequest) =>
  api.post("/users/register", registrationRequest);

// Prijava korisnika
export const loginUser = (credentials) => api.post("/users/login", credentials);

// Promena lozinke
export const changePassword = (changePasswordRequest) =>
  api.post("/change-password", changePasswordRequest);

// Dobavljanje prijatelja korisnika
export const getUserFriends = (userId) =>
  api.get(`/users/get-friends/${userId}`);

// Dobavljanje svih korisnika
export const getAllUsers = () => api.get("/users/get-all");
