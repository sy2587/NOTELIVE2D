import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'

export const useNoteStore = defineStore('notes', {
  state: () => ({ items: [], loading: false, error: '' }),
  actions: {
    async fetchAll() {
      this.loading = true
      this.error = ''
      try {
        const items = await apiRequest('/api/notes')
        this.items = Array.isArray(items) ? items : []
      } catch (error) {
        this.error = error.message
        throw error
      } finally { this.loading = false }
    },
    async save(note) {
      const saved = await apiRequest(note.id ? `/api/notes/${note.id}` : '/api/notes', { method: note.id ? 'PUT' : 'POST', body: JSON.stringify(note) })
      const index = this.items.findIndex(item => item.id === saved.id)
      if (index >= 0) this.items[index] = saved
      else this.items.unshift(saved)
      this.items.sort((a, b) => Number(b.pinned) - Number(a.pinned))
      return saved
    },
    async remove(id) {
      await apiRequest(`/api/notes/${id}`, { method: 'DELETE' })
      this.items = this.items.filter(note => note.id !== id)
    }
  }
})
