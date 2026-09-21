import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'

export const useFolderStore = defineStore('folders', {
  state: () => ({ items: [], loading: false, error: '' }),
  actions: {
    async fetchAll() {
      this.loading = true; this.error = ''
      try { this.items = await apiRequest('/api/folders') }
      catch (error) { this.error = error.message; throw error }
      finally { this.loading = false }
    },
    async create(name) {
      const saved = await apiRequest('/api/folders', { method: 'POST', body: JSON.stringify({ name }) })
      this.items = [...this.items, saved].sort((a, b) => a.name.localeCompare(b.name, 'zh-TW'))
      return saved
    },
    async remove(id) {
      await apiRequest(`/api/folders/${id}`, { method: 'DELETE' })
      this.items = this.items.filter(folder => folder.id !== id)
    }
  }
})
