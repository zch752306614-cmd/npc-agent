import axios from 'axios';
import { ref } from 'vue';

const pendingRequestCount = ref(0);

export const isHttpLoading = pendingRequestCount;

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:59999/api/game',
  timeout: 12000
});

httpClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  pendingRequestCount.value += 1;

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

httpClient.interceptors.response.use(
  (response) => {
    pendingRequestCount.value = Math.max(0, pendingRequestCount.value - 1);
    const payload = response.data;
    if (payload?.code !== 200) {
      return Promise.reject(new Error(payload?.message || '请求失败'));
    }
    return payload.data;
  },
  (error) => {
    pendingRequestCount.value = Math.max(0, pendingRequestCount.value - 1);
    return Promise.reject(error instanceof Error ? error : new Error('网络请求失败'));
  }
);

export default httpClient;
