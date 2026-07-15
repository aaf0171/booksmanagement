import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      const authStore = useAuthStore()
      originalRequest._retry = true

      if (authStore.refreshToken) {
        try {
          await authStore.refreshAccessToken()
          originalRequest.headers.Authorization = `Bearer ${authStore.accessToken}`
          return apiClient(originalRequest)
        } catch (refreshError) {
          authStore.logout()
          window.location.href = '/login'
          return Promise.reject(refreshError)
        }
      }
    }

    return Promise.reject(error)
  }
)

export default apiClient
