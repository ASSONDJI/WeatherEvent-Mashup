import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getAgenda = async (city: string, date: string) => {
  const response = await api.get('/agenda', { params: { city, date } });
  return response.data;
};

export const getBenchmark = async (city: string, date: string) => {
  const response = await api.get('/agenda/benchmark', { params: { city, date } });
  return response.data;
};

export const getHealth = async () => {
  const response = await api.get('/health');
  return response.data;
};
