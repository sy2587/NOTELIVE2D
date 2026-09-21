import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({ summary: null, loading: false, error: '' }),
  actions: {
    async fetch() {
      this.loading = true
      this.error = ''
      try { this.summary = await apiRequest('/api/dashboard') }
      catch (error) { this.error = error.message; throw error }
      finally { this.loading = false }
    }
  }
})
