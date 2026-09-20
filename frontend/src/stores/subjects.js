import { defineStore } from 'pinia'
import { apiRequest } from '../api/client'

export const useSubjectStore = defineStore('subjects', {
  state: () => ({ items: [], tasksByNote: {}, loading: false, error: '' }),
  actions: {
    async fetchAll() {
      this.loading = true; this.error = ''
      try {
        const items = await apiRequest('/api/subjects')
        this.items = Array.isArray(items) ? items : []
      }
      catch (error) { this.error = error.message; throw error }
      finally { this.loading = false }
    },
    async save(subject) {
      const saved = await apiRequest(subject.id ? `/api/subjects/${subject.id}` : '/api/subjects', { method: subject.id ? 'PUT' : 'POST', body: JSON.stringify(subject) })
      if (!Array.isArray(this.items)) this.items = []
      const index = this.items.findIndex(item => item.id === saved.id)
      if (index >= 0) this.items[index] = saved; else this.items.unshift(saved)
    },
    async remove(id) {
      await apiRequest(`/api/subjects/${id}`, { method: 'DELETE' })
      this.items = Array.isArray(this.items) ? this.items.filter(item => item.id !== id) : []
      delete this.tasksByNote[id]
    },
    async fetchTasks(noteId) {
      const tasks = await apiRequest(`/api/subjects/${noteId}/tasks`)
      this.tasksByNote[noteId] = Array.isArray(tasks) ? tasks : []
    },
    async addTask(noteId, title) {
      const task = await apiRequest(`/api/subjects/${noteId}/tasks`, { method: 'POST', body: JSON.stringify({ title }) })
      const tasks = this.tasksByNote[noteId] || []
      this.tasksByNote[noteId] = [...tasks, task]
    },
    async toggleTask(noteId, taskId) {
      const updated = await apiRequest(`/api/subjects/${noteId}/tasks/${taskId}/toggle`, { method: 'POST' })
      this.tasksByNote[noteId] = (this.tasksByNote[noteId] || []).map(task => task.id === taskId ? updated : task)
    },
    async removeTask(noteId, taskId) {
      await apiRequest(`/api/subjects/${noteId}/tasks/${taskId}`, { method: 'DELETE' })
      this.tasksByNote[noteId] = (this.tasksByNote[noteId] || []).filter(task => task.id !== taskId)
    }
  }
})
