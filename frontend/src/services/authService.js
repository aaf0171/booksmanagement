import apiClient from '@/api/axios'

export const authService = {
  async login(credentials) {
    const response = await apiClient.post('/auth/login', credentials)
    return response.data
  },

  async refresh(refreshToken) {
    const response = await apiClient.post('/auth/refresh', { refreshToken })
    return response.data
  },

  async logout(refreshToken) {
    await apiClient.post('/auth/logout', { refreshToken })
  },

  async getMe() {
    const response = await apiClient.get('/auth/me')
    return response.data
  }
}
