import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://localhost:8080/api', // La ruta base del backend
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para inyectar el token JWT en las peticiones protegidas
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('blossom_token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});