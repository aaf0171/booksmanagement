import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/authService'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem('accessToken') || null)
  const refreshToken = ref(localStorage.getItem('refreshToken') || null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!accessToken.value)

  async function login(credentials) {
    const response = await authService.login(credentials)
    accessToken.value = response.accessToken
    refreshToken.value = response.refreshToken
    localStorage.setItem('accessToken', response.accessToken)
    localStorage.setItem('refreshToken', response.refreshToken)
    user.value = null
  }

  async function refreshAccessToken() {
    if (!refreshToken.value) {
      throw new Error('No refresh token available')
    }
    const response = await authService.refresh(refreshToken.value)
    accessToken.value = response.accessToken
    localStorage.setItem('accessToken', response.accessToken)
  }

  async function logout() {
    if (refreshToken.value) {
      try {
        await authService.logout(refreshToken.value)
      } catch (e) {
        // ignore error during logout
      }
    }
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  async function fetchUser() {
    if (accessToken.value) {
      user.value = await authService.getMe()
    }
  }

  return {
    accessToken,
    refreshToken,
    user,
    isAuthenticated,
    login,
    refreshAccessToken,
    logout,
    fetchUser
  }
})
