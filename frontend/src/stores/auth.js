import { defineStore } from 'pinia'
import { apiRequest, clearCsrfToken } from '../api/client'

export const useAuthStore = defineStore('auth', {
  state: () => ({ user: null, initialized: false }),
  getters: { isAuthenticated: state => Boolean(state.user) },
  actions: {
    async restore() {
      if (this.initialized) return
      try { this.user = await apiRequest('/api/auth/me') } catch { this.user = null }
      this.initialized = true
    },
    async login(credentials) {
      clearCsrfToken()
      this.user = await apiRequest('/api/auth/login', {
        method: 'POST', body: JSON.stringify(credentials)
      })
    },
    async register(payload) {
      return apiRequest('/api/auth/register', {
        method: 'POST', body: JSON.stringify(payload)
      })
    },
    async logout() {
      await apiRequest('/api/auth/logout', { method: 'POST' })
      this.user = null
      clearCsrfToken()
    }
  }
})
