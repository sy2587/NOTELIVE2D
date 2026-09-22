import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'
import { notifyLive2D } from '../utils/live2d'

export const useNoteStore = defineStore('notes', {
  state: () => ({ items: [], loading: false, error: '', page: 0, totalPages: 0, totalElements: 0 }),
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
    async search(filters = {}) {
      this.loading = true
      this.error = ''
      const params = new URLSearchParams()
      if (filters.q?.trim()) params.set('q', filters.q.trim())
      if (filters.folderId) params.set('folderId', filters.folderId)
      if (filters.tagId) params.set('tagId', filters.tagId)
      if (filters.favorite === true) params.set('favorite', 'true')
      params.set('page', String(filters.page ?? 0))
      params.set('size', String(filters.size ?? 10))
      try {
        const result = await apiRequest(`/api/notes/search?${params}`)
        this.items = result.items || []
        this.page = result.page || 0
        this.totalPages = result.totalPages || 0
        this.totalElements = result.totalElements || 0
      } catch (error) {
        this.error = error.message
        throw error
      } finally { this.loading = false }
    },
    async fetchOne(id) {
      const note = await apiRequest(`/api/notes/${id}`)
      const index = this.items.findIndex(item => item.id === note.id)
      if (index >= 0) this.items[index] = note
      else this.items.unshift(note)
      return note
    },
    async save(note) {
      const creating = !note.id
      const saved = await apiRequest(note.id ? `/api/notes/${note.id}` : '/api/notes', { method: note.id ? 'PUT' : 'POST', body: JSON.stringify(note) })
      const index = this.items.findIndex(item => item.id === saved.id)
      if (index >= 0) this.items[index] = saved
      else this.items.unshift(saved)
      this.items.sort((a, b) => Number(b.pinned) - Number(a.pinned))
      notifyLive2D(creating ? '新筆記收好了，今天又多了一點累積。' : '筆記已儲存，記得留下最重要的一句話。')
      return saved
    },
    async remove(id) {
      await apiRequest(`/api/notes/${id}`, { method: 'DELETE' })
      this.items = this.items.filter(note => note.id !== id)
    }
  }
})
