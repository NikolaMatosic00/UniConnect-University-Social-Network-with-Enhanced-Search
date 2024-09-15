import axios from "axios";

const API_URL = "http://localhost:8080/api"; // Promenite URL po potrebi

export const register = (userData) => {
  return axios.post(`${API_URL}/users`, userData);
};

export const login = (userData) => {
  return axios.post(`${API_URL}/login`, userData); // Implementirati login endpoint na backendu
};

export const getUser = (id) => {
  return axios.get(`${API_URL}/users/${id}`);
};
